package com.teamapt.profectus.moneytransfers.settlementrecon.utils.westernunion;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.moneytransfers.settlementrecon.utils.WesternUnionExtractionUtil;
import com.teamapt.profectus.settlementrecon.lib.model.ProductReport;
import com.teamapt.profectus.settlementrecon.lib.model.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;


public class WesternUnionExtractionUtilTest {

    @Test
    public void process() throws CosmosServiceException {
        File resourcesDirectory = new File("src/test/java/resources");
        String filePath = resourcesDirectory.getAbsolutePath() + "/S0000005_20200122_20200204_01.txt";
        ProductReport productReport = new ProductReport();
        WesternUnionExtractionUtil westernUnionExtractionUtil = new WesternUnionExtractionUtil();
        List<Transaction> transactionList = westernUnionExtractionUtil.process(filePath, productReport);
        Assert.assertEquals(transactionList.size(), transactionList.size());
    }
}