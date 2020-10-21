package io.bankbridge.integrationTest;

import io.bankbridge.BankBridgeJettyServer;
import io.bankbridge.httpClient.BanksHttpClient;
import io.bankbridge.httpClient.BanksHttpClientFactory;
import io.bankbridge.httpClient.HttpClientEnum;
import io.bankbridge.httpClient.IBanksHttpClient;
import io.bankbridge.model.ResultModel;
import org.junit.Assert;
import org.junit.Test;


public class E2ETestError {
    private static final String APPLICATION_SERVER_ENDPOINT_V2 = "http://localhost:8888/v1/banks/all";
    private static final String BANK_NAME = "all-banks";
    @Test(expected = RuntimeException.class)
    public void testServerNotAvailable()
    {
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        Assert.assertEquals(BanksHttpClient.class,httpClient.getClass());
        httpClient.handleGet(BANK_NAME, APPLICATION_SERVER_ENDPOINT_V2, ResultModel[].class);
    }
}
