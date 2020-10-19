package io.bankbridge.integrationTests;

import io.bankbridge.BankBridgeJettyServer;
import io.bankbridge.httpClient.BanksHttpClientFactory;
import io.bankbridge.httpClient.HttpClientEnum;
import io.bankbridge.httpClient.IBanksHttpClient;
import io.bankbridge.unitsTests.TestModel;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class E2ETest {
    private static final Logger LOGGER = LoggerFactory.getLogger(E2ETest.class);
    private static final String APPLICATION_SERVER_PORT = "8888";
    private static final int REMOTE_BANK_SERVER_PORT = 9999;
    private static final String APPLICATION_SERVER_ENDPOINT_V1 = "http://localhost:8888/v1/banks/all";
    private static final String APPLICATION_SERVER_ENDPOINT_V2 = "http://localhost:8888/v2/banks/all";
    private static final String BANK_NAME = "all-banks";
    private static final String FETCHED_BANK_NAME = "Royal Bank of Boredom";

    @BeforeClass
    public static void startServers() {
        //This should start RemoteBankServer internally
        //Main thread is blocked while starting Jetty
        Thread thread = new Thread(() -> {
            try {
                BankBridgeJettyServer.main(new String[]{APPLICATION_SERVER_PORT});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    @AfterClass
    public static void stopServers() {
        BankBridgeJettyServer.stopJettyServer();
    }

    @Test
    public void testBanksCacheBasedEndpoint() throws InterruptedException {
        //Jetty takes a while to start
        //Waiting for sometime shouldn't be an issue in test
        Thread.sleep(2000);
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        TestModel[] testModels = httpClient.handleGet(BANK_NAME, APPLICATION_SERVER_ENDPOINT_V1, TestModel[].class);
        Assert.assertEquals(3,testModels.length);
    }

    @Test
    public void testBanksRemoteServerBasedEndpoint() {
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        TestModel[] testModels = httpClient.handleGet(BANK_NAME, APPLICATION_SERVER_ENDPOINT_V2, TestModel[].class);
        Assert.assertEquals(3,testModels.length);
    }

    @Test (expected = RuntimeException.class)
    public void testServerNotAvailable()
    {
        BankBridgeJettyServer.stopJettyServer();
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        httpClient.handleGet(BANK_NAME, APPLICATION_SERVER_ENDPOINT_V2, TestModel[].class);
    }
}
