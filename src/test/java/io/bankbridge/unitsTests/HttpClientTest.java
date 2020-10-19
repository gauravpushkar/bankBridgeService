package io.bankbridge.unitsTests;

import io.bankbridge.RemoteBankServers;
import io.bankbridge.httpClient.BanksHttpClientFactory;
import io.bankbridge.httpClient.HttpClientEnum;
import io.bankbridge.httpClient.IBanksHttpClient;
import io.bankbridge.model.BankModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpClientTest {
    private static final int SPARK_SERVER_PORT = 9999;
    private static final String DUMMY_BANK_NAME = "dummyBank";
    private static final String BANK_NAME = "Royal Bank of Boredom";
    private static final String DUMMY_URL = "http://localhost:1111/xyz";
    private static final String URL = "http://localhost:9999/rbb";

    @BeforeClass
    public static void startSparkTestServer() {
        RemoteBankServers.startServer(SPARK_SERVER_PORT);
    }


    @AfterClass
    public static void stopSparkTestServer() {
        RemoteBankServers.stopServer();
    }

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

    @Test
    public void testHttpClientGet() {
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        BankModel bankModel = httpClient.handleGet(BANK_NAME, URL, BankModel.class);
        Assert.assertNotNull(bankModel);
        Assert.assertEquals("1234", bankModel.getBic());
    }

}
