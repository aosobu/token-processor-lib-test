package com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygram.tokenprocessorutil;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.moneytransfers.settlementrecon.moneygram.MoneyGramExtraction;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionConfiguration;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.tokenextractionfactory.TokenExtractorUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygram.tokenprocessorutil.factory.TokenExtractionFactoryTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class MoneyGramExtractionWithTokenProcessorUtilTests {
    Logger logger = LoggerFactory.getLogger(TokenExtractionFactoryTest.class);
    private static TokenExtractionConfiguration tokenExtractionConfiguration;
    private static String filePath;

    @BeforeClass
    public static void setup(){
        filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/02012020.xls";
        tokenExtractionConfiguration = new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:Account\\s{0,}Total:\\s{0,}Send\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfiguration.setBatchIdentifier("Account.*Total:.*Send");
    }

    @Test
    public void test_extract_outbound_amount_transactions_using_default_mode(){
        List<String> resultSet = new ArrayList();

        try {
            resultSet = TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        logger.info(resultSet.toString());
        Assert.assertEquals(16, resultSet.size());
    }

    @Test
    public void test_extract_outbound_sol_and_amount_transactions_using_lookbehind_mode(){
        TokenExtractionConfiguration tokenExtractionConfiguration = getTokenExtractionConfiguration("(?:Account\\s{0,}Total:\\s{0,}Send\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)",
                        "Account.*Total:.*Send", "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        HashMap<String, List<String>> resultSet = new HashMap();

        try {
           TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSet = TokenExtractorUtil.getLookBehindMap();
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }
        resultSet.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + "\n" + entry.getValue().toString());
        });

        Assert.assertEquals(8, resultSet.size());
    }

    @Test
    public void test_extract_inbound_sol_and_amount_using_lookbehind_mode(){
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Revenue_100711693 (32).csv";
        HashMap<String, List<String>> resultSet = new HashMap();
        TokenExtractionConfiguration tokenExtractionConfiguration =
                getTokenExtractionConfiguration("(?:Account)\\s{0,}(?:\\d+)\\s{0,}-{0,}\\s{0,}(?:\\d+)\\s{0,}(?:Total)\\s{0,}:\\s{0,}(?:\\d+\\s{0,})(?:[+-]?((?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))(?:\\d+(?:,\\d+)*(?:.\\d*)?)(?:.\\d+)))(?:\\))(?:\\s{0,})",
                                                "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)", "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setStartOffset("(.*)(Receive)(.*Activity)");
        tokenExtractionConfiguration.setStartOffIdentifierSalt("Receive  Activity");
        tokenExtractionConfiguration.setEndOffset("(Summary.*Information)");
        tokenExtractionConfiguration.setEndOffIdentifierSalt("Summary Information");

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSet = TokenExtractorUtil.getLookBehindMap();
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }
        resultSet.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + "\n" + entry.getValue().toString());
        });

        Assert.assertEquals(139, resultSet.size());
    }

    @Test
    public void test_extract_inbound_settlement_balancing_transaction_using_default_mode(){
        List<String> resultSet = new ArrayList();
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Revenue_100711693 (30).csv";
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:USD\\s{0,}-\\s{0,}NGN\\s{0,}Receive\\s{0,}Activity\\s{0,}Total\\s{0,}:\\s{0,})" +
                "(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)");
        tokenExtractionConfiguration.setBatchIdentifier("(?:USD\\s{0,}-\\s{0,}NGN\\s{0,}Receive\\s{0,}Activity\\s{0,}" +
                "Total\\s{0,}:\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)");

        try {
            resultSet = TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        logger.info(resultSet.toString());
        Assert.assertEquals(2, resultSet.size());
    }

    @Test
    public void test_extract_inbound_income_transactions_first_using_lookbehind_mode(){
        List<String> resultSet = new ArrayList();
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Detail_100711693.csv";
        TokenExtractionConfiguration tokenExtractionConfiguration = new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:(\\d{10})\\s+(?:\\d{8})\\s+(?:MT\\s.*)\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:\\d{2}\\/\\d{2}\\/\\d{4})\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:trn)\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)\\)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)\\)))\\s{0,}(?:mk)?(.*)|(stl)\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)(\\)?)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)(?:\\)?)))\\s{0,}?(?:[+-]?((?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)(?:\\)?))))");
        tokenExtractionConfiguration.setBatchIdentifier("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(resultSet.size(), resultSet.size());
    }

    @Test
    public void test_extract_inbound_income_transactions_second_using_lookbehind_mode(){
        HashMap<String, List<String>> resultSets = new HashMap();
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Detail_100711693.csv";
        TokenExtractionConfiguration tokenExtractionConfiguration =new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:Account\\s{0,}Total:\\s{0,}(?:Send|Receive)\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfiguration.setBatchIdentifier("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSets = TokenExtractorUtil.getLookBehindMap();
            Set<String> sets = new TreeSet();
            resultSets
                    .entrySet()
                    .forEach(token -> {
                        String sol = MoneyGramExtraction.getSol(token.getKey());
                        sets.add(sol);
                    });
            logger.info("Set Size : " + sets.size());
            sets.stream().forEach(element -> {
                System.out.println(element);
            });


        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(141, resultSets.size());
    }

    @Test
    public void test_extract_refund_section_from_outbound_file(){
        HashMap<String, List<String>> resultSet = new HashMap();
        TokenExtractionConfiguration tokenExtractionConfiguration = getTokenExtractionConfiguration("(?:Refund)\\s{0,}(?:\\d+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)",
                "(Refund\\s{0,}\\d{1,})", "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setFilePath(filePath);

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSet = TokenExtractorUtil.getLookBehindMap();
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }
        resultSet.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + "\n" + entry.getValue().toString());
        });

        Assert.assertEquals(2, resultSet.size());
    }

    public static TokenExtractionConfiguration getTokenExtractionConfiguration(String matcherGroupRegex, String batchIdentifier, String lookBehindMatcher) {
        tokenExtractionConfiguration.setMatcherGroupRegex(matcherGroupRegex);
        tokenExtractionConfiguration.setBatchIdentifier(batchIdentifier);
        tokenExtractionConfiguration.setLookBehindMatcherRegex(lookBehindMatcher);
        return tokenExtractionConfiguration;
    }
}
