package io.bankbridge.httpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Factory implementation for Http Clients
 */
public class BanksHttpClientFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksHttpClientFactory.class);

    public static IBanksHttpClient getHttpClient(HttpClientEnum clientType) {
        IBanksHttpClient httpClient = null;
        if (clientType == HttpClientEnum.APACHE) {
            httpClient = BanksHttpClient.getInstance();
        } else {
            LOGGER.error("Request for Unsupported HttpClient,returning null");
            //We could return other clients if we would have implemented
        }
        return httpClient;
    }
}
