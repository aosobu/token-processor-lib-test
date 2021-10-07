package com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygram.tokenprocessorutil.factory;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionConfiguration;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.tokenextractionfactory.TokenExtractor;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.tokenextractionfactory.TokenExtractorUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygram.tokenprocessorutil.MoneyGramExtractionWithTokenProcessorUtilTests;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TokenExtractionFactoryTest {
    Logger logger = LoggerFactory.getLogger(TokenExtractionFactoryTest.class);
    private static TokenExtractionConfiguration tokenExtractionConfiguration;
    private static String filePath;

    @BeforeClass
    public static void setup(){
        tokenExtractionConfiguration = new TokenExtractionConfiguration();
        filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/ria_payments.xlsx";
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:(?:\\s{0,}Settlement\\s{0,}Amount\\s{0,}USD\\s{0,}))([+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))");
        tokenExtractionConfiguration.setBatchIdentifier("Settlement Amount USD");
    }

    @Test
    public void test_default_mode_factory(){
        List<String> resultSet = new ArrayList();

        try {
                resultSet = TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
        } catch (CosmosServiceException | IOException e) {
            logger.info("Error encountered while extracting tokens >>> " + e.getMessage());
        }

        Assert.assertEquals(2, resultSet.size());
    }

    @Test
    public void test_look_behind_mode_factory_with_core_parameters(){
        TokenExtractionConfiguration tokenExtractionConfiguration
                                = MoneyGramExtractionWithTokenProcessorUtilTests.getTokenExtractionConfiguration("(?:Account\\s{0,}Total:\\s{0,}Send\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)",
                "Account.*Total:.*Send", "\\s{0,}FCMB\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
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
    public void test_look_behind_mode_factory_with_core_parameters_plus_optional_parameters(){
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Revenue_100711693 (32).csv";
        HashMap<String, List<String>> resultSet = new HashMap();
        TokenExtractionConfiguration tokenExtractionConfiguration =
                MoneyGramExtractionWithTokenProcessorUtilTests.getTokenExtractionConfiguration("(?:Daily\\s{0,}Total\\s{0,}:\\s{0,})(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])([-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)\\s{0,}(?=.*[0-9])(?:[-.,+()0-9]+)",
                        "\\s{0,}FCMB\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)", "\\s{0,}FCMB\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
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

}
