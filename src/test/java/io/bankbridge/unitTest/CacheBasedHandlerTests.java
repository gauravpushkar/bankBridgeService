package io.bankbridge.unitTest;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.model.ResultModel;
import io.bankbridge.utils.BanksUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CacheBasedHandlerTests {
    @Test
    public void testCacheBasedHandler() throws IOException {
        BanksCacheBased banksCacheBased = BanksCacheBased.getInstance();
        String jsonString = banksCacheBased.handle();
        Assert.assertNotNull(jsonString);
        ResultModel[] testModels = BanksUtils.transformJson(jsonString, ResultModel[].class);
        Assert.assertNotNull(testModels);
    }


}
