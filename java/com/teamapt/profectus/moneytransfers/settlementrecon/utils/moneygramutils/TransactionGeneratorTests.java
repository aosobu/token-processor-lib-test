package com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygramutils;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.moneytransfers.settlementrecon.moneygram.TransactionGenerator;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionConfiguration;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.tokenextractionfactory.TokenExtractorUtil;
import com.teamapt.profectus.settlementrecon.lib.model.Transaction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class TransactionGeneratorTests {
    private static TokenExtractionConfiguration tokenExtractionConfiguration;
    private static String filePath;

    @BeforeClass
    public static void setup(){
        filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/08012020.xls";
        tokenExtractionConfiguration = new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);
    }

    @Test
    public void test_generate_outbound_transactions(){
        HashMap<String, List<String>> resultSet;

        List<Transaction> inboundTransactions = new ArrayList();
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:Account\\s{0,}Total:\\s{0,}Send\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfiguration.setBatchIdentifier("Account.*Total:.*Send");
        tokenExtractionConfiguration.setLookBehindMatcherRegex("\\s{0,}FCMB\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setFilePath(filePath);

        TokenExtractionConfiguration tokenExtractionConfiguration_refund =  new TokenExtractionConfiguration();
        tokenExtractionConfiguration_refund.setMatcherGroupRegex("(?:Refund)\\s{0,}(?:\\d+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfiguration_refund.setBatchIdentifier("(Refund\\s{0,}\\d{1,})");
        tokenExtractionConfiguration_refund.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration_refund.setFilePath(filePath);

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration_refund);

            resultSet = TokenExtractorUtil.getLookBehindMap();

            inboundTransactions = TransactionGenerator.transaction(resultSet);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(6, inboundTransactions.size());
    }

    @Test
    public void test_generate_inbound_transactions(){
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Revenue_100711693-(1).xls";
        HashMap<String, List<String>> resultSet = new HashMap();
        List<Transaction> outboundTransactions = new ArrayList();

        TokenExtractionConfiguration tokenExtractionConfiguration =
                getTokenExtractionConfiguration("(?:Account)\\s{0,}(?:\\d+)\\s{0,}-{0,}\\s{0,}(?:\\d+)\\s{0,}(?:Total)\\s{0,}:\\s{0,}(?:\\d+\\s{0,})([+-]?\\(?\\d+[.]?\\d+\\)?)",
                        "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)",
                        "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setStartOffset("(.*)(Receive)(.*Activity)");
        tokenExtractionConfiguration.setStartOffIdentifierSalt("Receive  Activity");
        tokenExtractionConfiguration.setEndOffset("(Summary.*Information)");
        tokenExtractionConfiguration.setEndOffIdentifierSalt("Summary Information");

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSet = TokenExtractorUtil.getLookBehindMap();
            outboundTransactions = TransactionGenerator.generateOutboundTransaction(resultSet);

        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(outboundTransactions.size() , resultSet.size());
    }

    @Test
    public void test_generate_inbound_transactions_for_headofficesol_using_lookbehind_mode(){
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Revenue_100711693 (26).csv";
        HashMap<String, List<String>> resultSet = new HashMap();
        List<Transaction> outboundTransactions = new ArrayList();

        TokenExtractionConfiguration tokenExtractionConfiguration =
                getTokenExtractionConfiguration("(?:Daily\\s{0,}Total\\s{0,}:)\\s{0,}\\d+\\s{0,}(?=.*[0-9])([-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)",
                        "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)",
                        "\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setStartOffset("(.*)(Receive)(.*Activity)");
        tokenExtractionConfiguration.setStartOffIdentifierSalt("Receive  Activity");
        tokenExtractionConfiguration.setEndOffset("(Summary.*Information)");
        tokenExtractionConfiguration.setEndOffIdentifierSalt("Summary Information");

        try {

            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSet = TokenExtractorUtil.getLookBehindMap();
            outboundTransactions = TransactionGenerator.generateOutboundTransactionForSol(resultSet);

        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(1, outboundTransactions.size());
    }

    @Test
    public void test_generate_inbound_income_transactions_second_from_lookbehind_mode(){
        HashMap<String, List<String>> resultSets;
        HashMap<String, List<String>> resultSetsComplimentary;
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Detail_1A.csv";

        List<Transaction> inboundIncomeTransactions = new ArrayList();

        TokenExtractionConfiguration tokenExtractionConfiguration =new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);

        tokenExtractionConfiguration.setMatcherGroupRegex("(?:Account\\s{0,}Total:\\s{0,}(?:Send|Receive)\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfiguration.setBatchIdentifier("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setEndOffset("(Summary.*Information)");
        tokenExtractionConfiguration.setEndOffIdentifierSalt("Summary Information");

        TokenExtractionConfiguration tokenExtractionConfigurationComplimentary =new TokenExtractionConfiguration();
        tokenExtractionConfigurationComplimentary.setFilePath(filePath);
        tokenExtractionConfigurationComplimentary.setMatcherGroupRegex("(Account\\s{0,}Total:)?\\s{0,}(?:Receive|Send)\\s{0,}(?:\\d+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfigurationComplimentary.setBatchIdentifier("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfigurationComplimentary.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfigurationComplimentary.setEndOffset("(Summary.*Information)");
        tokenExtractionConfigurationComplimentary.setEndOffIdentifierSalt("Summary Information");

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSets = TokenExtractorUtil.getLookBehindMap();
            TokenExtractorUtil.setLookBehindMap();

            TokenExtractionUtil.processTokens(tokenExtractionConfigurationComplimentary);
            resultSetsComplimentary = TokenExtractorUtil.getLookBehindMap();

            inboundIncomeTransactions = TransactionGenerator.generateIncomeTransaction(resultSets);
            TransactionGenerator.generateIncomeTransactionsForIncome(inboundIncomeTransactions, resultSetsComplimentary);

        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(141 , inboundIncomeTransactions.size());
    }

    @Test
    public void test_generate_inbound_income_transactions_second_from_lookbehind_mode_with_new_regex(){
        HashMap<String, List<String>> resultSets;
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Detail_1A.csv";

        List<Transaction> inboundIncomeTransactions = new ArrayList();

        TokenExtractionConfiguration tokenExtractionConfiguration =new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);

        tokenExtractionConfiguration.setMatcherGroupRegex("(?:Receive|Send)\\s{0,}(?:\\d+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)");
        tokenExtractionConfiguration.setBatchIdentifier("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setEndOffset("(Summary.*Information)");
        tokenExtractionConfiguration.setEndOffIdentifierSalt("Summary Information") ;


        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            resultSets = TokenExtractorUtil.getLookBehindMap();
            inboundIncomeTransactions = TransactionGenerator.generateIncomeTransactionBatch(resultSets);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(140 , inboundIncomeTransactions.size());
    }

    @Test
    public void test_generate_inbound_income_transactions_first_from_lookbehind_mode(){
        List<String> resultSet = new ArrayList();
        List<Transaction> inboundIncomeEntriesForHeadOffice = new ArrayList();
        HashMap<String, List<String>> result = new HashMap();
        String filePath =  "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Detail_1A.csv";

        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:(\\d{10})\\s+(?:\\d{8})\\s+(?:MT\\s.*)\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:\\d{1,}\\/\\d{1,}\\/\\d{2,})\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:trn)\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)\\)))\\s{0,}(?:[+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)(?:.\\d+)\\)))\\s{0,}(?:mk)?(.*))");
        tokenExtractionConfiguration.setBatchIdentifier("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setLookBehindMatcherRegex("\\s{0,}(?:FCMB|FIRST\\s{0,}CITY\\s{0,}MONUMENT\\s{0,}BANK\\s{0,}(?:LTD|LIMITED))\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setEndOffset("(Summary.*Information)");
        tokenExtractionConfiguration.setEndOffIdentifierSalt("Summary Information");

        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            result = TokenExtractorUtil.getLookBehindMap();
            inboundIncomeEntriesForHeadOffice = TransactionGenerator.generateIncomeTransactionsForHeadOffice(result);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(1, inboundIncomeEntriesForHeadOffice.size());
    }

    public static TokenExtractionConfiguration getTokenExtractionConfiguration(String matcherGroupRegex, String batchIdentifier, String lookBehindMatcher) {
        tokenExtractionConfiguration.setMatcherGroupRegex(matcherGroupRegex);
        tokenExtractionConfiguration.setBatchIdentifier(batchIdentifier);
        tokenExtractionConfiguration.setLookBehindMatcherRegex(lookBehindMatcher);
        return tokenExtractionConfiguration;
    }

}
