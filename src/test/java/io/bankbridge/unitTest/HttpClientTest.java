package io.bankbridge.unitTest;

import io.bankbridge.httpClient.BanksHttpClientFactory;
import io.bankbridge.httpClient.HttpClientEnum;
import io.bankbridge.httpClient.IBanksHttpClient;
import org.junit.Assert;
import org.junit.Test;

public class HttpClientTest {
    private static final String DUMMY_BANK_NAME = "dummyBank";
    private static final String BANK_NAME = "Royal Bank of Boredom";
    private static final String DUMMY_URL = "http://localhost:1111/xyz";

    @Test
    public void testHttpClientFactory() {
        Assert.assertNotNull(BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE));
        Assert.assertNull(BanksHttpClientFactory.getHttpClient(HttpClientEnum.GOOGLE));
    }


    @Test(expected = RuntimeException.class)
    public void testHttpClientGetWithException() {
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        httpClient.handleGet(DUMMY_BANK_NAME, DUMMY_URL, String.class);
    }

}
