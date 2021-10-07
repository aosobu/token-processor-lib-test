package com.teamapt.profectus.moneytransfers.settlementrecon.utils.westernunion;

import com.teamapt.profectus.moneytransfers.settlementrecon.westernUnion.model.WesternUnionConfig;
import com.teamapt.profectus.moneytransfers.settlementrecon.westernUnion.repository.WesternUnionConfigRepository;
import com.teamapt.profectus.moneytransfers.settlementrecon.westernUnion.service.WesternUnionConfigService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author sokunniga
 * @date 20/12/2019
 */
public class WesternUnionConfigServiceTest {

    WesternUnionConfigService westernUnionConfigService;

    @Before
    public void setUp() throws Exception {
        westernUnionConfigService = new WesternUnionConfigService();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPrepareConfigurationFromFileCSV() throws Exception {
        File resourcesDirectory = new File("src/test/java/resources");
        String filePath = resourcesDirectory.getAbsolutePath() + "/sample.csv";
        List<WesternUnionConfig> westernUnionConfigs = westernUnionConfigService.prepareWesternUnionConfigurationFromFile(Collections.singletonList(filePath), westernUnionConfigService.getExportableFields());
        assertEquals(11, westernUnionConfigs.size());
    }

}