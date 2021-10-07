package com.teamapt.profectus.moneytransfers.settlementrecon.utils.model;

import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MoneyGramTestConfiguration {

    public List<String> getReportsPath(String  fileName) throws IOException {
        return Collections.singletonList(getFilePath(fileName));
    }

    private String getFilePath(String fileName) throws IOException {
        Resource classPathResource = new ClassPathResource(fileName);
        return classPathResource.getFile().getPath();
    }

    public static<T> void asserts(T expected, T actual){
        Assert.assertEquals(expected,actual);
    }
}
