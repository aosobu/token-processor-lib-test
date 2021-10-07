package com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygram.tokenprocessorutil;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionConfiguration;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TokenExtractionUtilTest {
    private static TokenExtractionUtil tokenExtractionUtil;

    @BeforeClass
    public static void setUp(){
        tokenExtractionUtil = new TokenExtractionUtil();
    }

    @Test
    public void test_extract_tokens() {
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Detail_100711693.csv";
        List<List<String>> tokenList = null;
        try {
            tokenList = tokenExtractionUtil.extractTokens(filePath);
        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(tokenList.size(), tokenList.size());
    }

    @Test
    public void test_process_tokens() throws CosmosServiceException {
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/12112019.csv";
        TokenExtractionConfiguration tokenExtractionConfiguration = new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);
        try {
            TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_process_tokens_async() throws CosmosServiceException {
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/ria_payments.xlsx";
        TokenExtractionConfiguration tokenExtractionConfiguration = new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setFilePath(filePath);
        tokenExtractionConfiguration.setMatcherGroupRegex("(?:(?:\\s{0,}Settlement\\s{0,}Amount\\s{0,}USD\\s{0,}))([+-]?(?:(?:\\d+(?:,\\d+)*(?:.\\d*)?)|(?:.\\d+)))");
        tokenExtractionConfiguration.setBatchIdentifier("Settlement Amount USD");
        tokenExtractionConfiguration.setStartOffset("Settlement Adjustments");
        try {
             CompletableFuture<List<String>> futureString = TokenExtractionUtil.processTokensAsync(tokenExtractionConfiguration);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
