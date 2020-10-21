package io.bankbridge.unitTest;

import io.bankbridge.httpClient.BanksHttpClientFactory;
import io.bankbridge.httpClient.HttpClientEnum;
import io.bankbridge.httpClient.IBanksHttpClient;
import io.bankbridge.model.BankModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static spark.Spark.get;
import static spark.Spark.port;

public class HttpClientTest {
    private static final String DUMMY_BANK_NAME = "dummyBank";
    private static final String BANK_NAME = "Royal Bank of Boredom";
    private static final String DUMMY_URL = "http://localhost:1111/xyz";
    private static final String LOCAL_HOST = "http://localhost:";

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
    public void testHttpClient() throws InterruptedException {
        IBanksHttpClient httpClient = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE);
        startServer();
        BankModel bankModel = httpClient.handleGet(BANK_NAME, LOCAL_HOST+"4567"+"/rbb", BankModel.class);
        Assert.assertNotNull(bankModel);
        Assert.assertEquals("1234",bankModel.getBic());
    }

    private void startServer() throws InterruptedException {
        get("/rbb", (request, response) -> "{\n" +
                "\"bic\":\"1234\",\n" +
                "\"name\":\"Royal Bank of Boredom\",\n" +
                "\"countryCode\":\"GB\",\n" +
                "\"auth\":\"OAUTH\"\n" +
                "}");
        //Let the server start or we not poll if the server is up
        Thread.sleep(2000);
    }
}
