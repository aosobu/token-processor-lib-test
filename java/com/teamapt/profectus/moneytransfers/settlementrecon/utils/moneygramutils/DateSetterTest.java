package com.teamapt.profectus.moneytransfers.settlementrecon.utils.moneygramutils;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.moneytransfers.settlementrecon.constants.MoneyTransferConstants;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionConfiguration;
import com.teamapt.profectus.moneytransfers.settlementrecon.tokenprocessorutil.TokenExtractionUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.utils.MoneyGramExtractionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DateSetterTest {
    Logger logger = LoggerFactory.getLogger(DateSetterTest.class);

    @Test
    public void get_date_from_outbound_or_details_file(){
        MoneyGramExtractionUtil moneyGramExtractionUtil = new MoneyGramExtractionUtil();
        String regExForDate = "business date";
        List<String> regExList = new ArrayList();
        regExList.add(regExForDate);

        List<List<String>> extractedTokens = null;
        extractedTokens = moneyGramExtractionUtil.extractTokensUsingRegularExpression(regExList, "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/08012020.xls");

        String[] parts = extractedTokens.get(0).toArray()[0].toString().split(" ");
        String regex = "^([0-9]{1,2})\\/([0-9]{1,2})\\/([0-9]{1,4})$";
        for (String part : parts)
            if (Pattern.matches(regex, part))
                MoneyTransferConstants.setDate(part);
        Assert.assertEquals("1/8/2020", MoneyTransferConstants.getDate());
    }

    @Test
    public void get_date_from_inbound_file(){
        List<String> resultSet;
        String filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Revenue_100711693 (26).csv";

        TokenExtractionConfiguration tokenExtractionConfiguration = new TokenExtractionConfiguration();
        tokenExtractionConfiguration.setMatcherGroupRegex("((?:[0-9]{1,2})\\/(?:[0-9]{1,2})\\/(?:[0-9]{1,4}))");
        tokenExtractionConfiguration.setBatchIdentifier("\\s{0,}FCMB\\s{0,}(?:-\\s{0,}(#\\d+))?\\s{0,}-\\s{0,}(.*)\\s{0,}-\\s(.*)");
        tokenExtractionConfiguration.setFilePath(filePath);

        try {
            resultSet = TokenExtractionUtil.processTokens(tokenExtractionConfiguration);
            MoneyTransferConstants.setDate(MoneyTransferConstants.getDatePart(resultSet));
            logger.info("Date : " + MoneyTransferConstants.getDate());

        } catch (CosmosServiceException | IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("23/04/2019", MoneyTransferConstants.getDate());
    }
}
