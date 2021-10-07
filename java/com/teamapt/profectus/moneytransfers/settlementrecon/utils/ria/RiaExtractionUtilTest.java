package com.teamapt.profectus.moneytransfers.settlementrecon.utils.ria;

import com.teamapt.exceptions.CosmosServiceException;
import com.teamapt.profectus.file.util.exceptions.ReportExtractionException;
import com.teamapt.profectus.file.util.model.ReportExtractionConfiguration;
import com.teamapt.profectus.file.util.services.ReportExtractionUtil;
import com.teamapt.profectus.moneytransfers.settlementrecon.model.RIATransaction;
import com.teamapt.profectus.moneytransfers.settlementrecon.ria.RIADailyTotal;
import com.teamapt.profectus.settlementrecon.lib.model.ProductReport;
import com.teamapt.profectus.settlementrecon.lib.model.ProductReportColumnConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiaExtractionUtilTest {
    private static String filePath;
    private static ProductReport productReport;
    private static ProductReportColumnConfig productReportColumnConfig;
    private static ProductReportColumnConfig productReportColumnConfigOne;
    private static ProductReportColumnConfig productReportColumnConfigTwo;
    private static ProductReportColumnConfig productReportColumnConfigThree;
    private static ProductReportColumnConfig productReportColumnConfigFour;
    private static ProductReportColumnConfig productReportColumnConfigFive;
    private static List<ProductReportColumnConfig> productReportColumnConfigs;

    @BeforeClass
    public static void setup(){
        filePath = "/Users/aosobu/Documents/ProjectTeamApt/profectus-money-transfers-settlementrecon-lib/src/test/java/resources/Correspondent Payment Report_AR 51552.xlsx";
        productReport = new ProductReport();
        productReportColumnConfig = new ProductReportColumnConfig();
        productReportColumnConfig.setName("Transaction Pin");
        productReportColumnConfig.setColumnIndex(3);
        productReportColumnConfig.setReflectionName("transactionPin");

        productReportColumnConfigOne = new ProductReportColumnConfig();
        productReportColumnConfigOne.setName("Beneficiary Name First");
        productReportColumnConfigOne.setColumnIndex(6);
        productReportColumnConfigOne.setReflectionName("beneficiaryNameFirst");

        productReportColumnConfigTwo = new ProductReportColumnConfig();
        productReportColumnConfigTwo.setName("Beneficiary Name Last1");
        productReportColumnConfigTwo.setColumnIndex(7);
        productReportColumnConfigTwo.setReflectionName("beneficiaryNameLast1");

        productReportColumnConfigThree = new ProductReportColumnConfig();
        productReportColumnConfigThree.setName("Branch No");
        productReportColumnConfigThree.setColumnIndex(12);
        productReportColumnConfigThree.setReflectionName("branchNo");

        productReportColumnConfigFour = new ProductReportColumnConfig();
        productReportColumnConfigFour.setName("Payout Amount");
        productReportColumnConfigFour.setColumnIndex(18);
        productReportColumnConfigFour.setReflectionName("payoutAmount");

        productReportColumnConfigFive = new ProductReportColumnConfig();
        productReportColumnConfigFive.setName("Commission");
        productReportColumnConfigFive.setColumnIndex(22);
        productReportColumnConfigFive.setReflectionName("commission");

        productReportColumnConfigs = new ArrayList<>();
        productReportColumnConfigs.add(productReportColumnConfig);
        productReportColumnConfigs.add(productReportColumnConfigOne);
        productReportColumnConfigs.add(productReportColumnConfigTwo);
        productReportColumnConfigs.add(productReportColumnConfigThree);
        productReportColumnConfigs.add(productReportColumnConfigFour);
        productReportColumnConfigs.add(productReportColumnConfigFive);

        productReport.setProductReportColumnConfigs(productReportColumnConfigs);
        productReport.setName("RIA");
        productReport.setFileExtension(".xlsx");
        productReport.setReportTypeIndex(6);
        productReport.setDateFormat("MM/dd/yyyy hh:mm:ss");
    }


    @Test
    public void test_auto_detect_column_configuration() throws CosmosServiceException {
        try {
            ReportExtractionConfiguration<RIATransaction, ProductReportColumnConfig> extractionConfiguration =
                    new ReportExtractionConfiguration(filePath, RIATransaction.class, false);

            extractionConfiguration.setReportName(productReport.getName());
            extractionConfiguration.setRowMatchingCondition("NGN");
            extractionConfiguration.setColumnConfigClass(ProductReportColumnConfig.class);
            extractionConfiguration.setReportColumnConfigs(productReport.getProductReportColumnConfigs());
            extractionConfiguration.setFirstRowContainsColumnConfig(false);
            extractionConfiguration.setIsUsingOcpPackage(false);
            extractionConfiguration.setColumnConfigFromHeadersName(true);
            extractionConfiguration.setTransverseWorkBook(true);

            List<RIATransaction> transactions = ReportExtractionUtil.extract(extractionConfiguration);
            System.out.println("size " + transactions.size());
            Assert.assertEquals(104, transactions.size());

        } catch (ReportExtractionException | IOException e) {
            throw new CosmosServiceException("An error Occured while attempting to extract record from uploaded RIA file " + e.getMessage());
        }
    }

    @Test
    public void test_ria_extract_all_tokens() throws CosmosServiceException {
        List<List<String>> tokens = new RIADailyTotal().extractTokens(filePath);
        Assert.assertEquals(tokens.size(), tokens.size());
    }

}
