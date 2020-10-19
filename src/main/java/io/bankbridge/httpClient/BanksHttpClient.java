package io.bankbridge.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.utils.BanksPropertyHandler;
import io.bankbridge.utils.BanksUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/****
 *
 * Basic implementation of Apache HTTP client.
 * <p>Following features have been accommodated:
 * a) Connections are pooled, which is configurable
 * b) In case of failure retry is done by default 3 times (which can be configured as well)
 * c) No handling is done for idle connections due to time limitations for the assignment
 * </p>
 * @author gauravk
 */
public class BanksHttpClient<T> implements IBanksHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksHttpClient.class);
    private static final BanksHttpClient INSTANCE = new BanksHttpClient();
    private static final BanksPropertyHandler PROPERTY_HANDLER = BanksPropertyHandler.getInstance();
    //Pooling connection Manager instantiated on global level
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    //This client should be reused across requests unless we see performance degradation
    private static CloseableHttpClient client;

    public static BanksHttpClient getInstance() {
        return INSTANCE;
    }

    /***
     * We want to configure the client much before this handles any request
     * This can also be done inside init method
     */
    static {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(PROPERTY_HANDLER.getApplicationProperties().getHttpClientConnectTimeout())
                .setConnectionRequestTimeout(PROPERTY_HANDLER.getApplicationProperties().getHttpClientConnectionRequestTimeout())
                .setSocketTimeout(PROPERTY_HANDLER.getApplicationProperties().getHttpClientSocketTimeout())
                .build();
        connManager.setMaxTotal(PROPERTY_HANDLER.getApplicationProperties().getHttpClientMaxPooledConnections());
        connManager.setDefaultMaxPerRoute(PROPERTY_HANDLER.getApplicationProperties().getHttpClientDefaultMaxPerRoute());
        client = HttpClients.custom().setConnectionManager(connManager)
                //set request config
                .setDefaultRequestConfig(requestConfig)
                //set default retry handler (retries for 3 times and for IDEMPOTENT requests)
                .setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    }

    /***
     * This method implements Http GET to the upstream bank servers.
     * Given we are dealing with single kind of GET request we are safe to return {@code BankModel}, however
     * we should return generic object so that the same client can be used for different models.
     * @param bankName String
     * @param bankEndPoint String
     * @return {@link Object}
     */
    @Override
    public <T> T handleGet(String bankName, String bankEndPoint, Class<T> clazz) {
        T availableBankDetails = null;
        HttpGet httpGet = new HttpGet(bankEndPoint);
        try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Non Success response code :: {} for GET Operation", statusCode);
                return null;
            }
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            availableBankDetails = BanksUtils.transformJson(responseString, clazz);
            LOGGER.info("Successfully fetched details of the bank {} from endpoint {}", bankName, bankEndPoint);
            //Only for trouble shooting purposes, such sensitive details shouldn't be in logs
            LOGGER.debug("Fetched data :: {}", responseString);
        } catch (Exception ex) {
            LOGGER.error("Error occurred while communicating with upstream endpoint {}", bankEndPoint, ex);
            /*Let the business-layer/caller transform and handle this exception
            Making it Lambda friendly/ Java's checked exceptions is definitely not inline with modern programming
            paradigm*/
            throw new RuntimeException(ex);
        }
        return availableBankDetails;
    }

    /*** Below methods are just for illustration purposes, they could be very well removed from interface if we are
     * sure that we wont need anything other than GET ***/

    @Override
    public <T> boolean handlePost(String bankName, String bankEndpoint, Class<T> clazz) {
        return false;
    }

    @Override
    public <T> boolean handlePut(String bankName, String bankEndpoint, Class<T> clazz) {
        return false;
    }

    @Override
    public <T> boolean handleDelete(String bankName, String bankEndpoint, Class<T> clazz) {
        return false;
    }

    @Override
    public <T> boolean handleConnect(String bankName, String bankEndpoint, Class<T> clazz) {
        return false;
    }

    @Override
    public <T> boolean handleTrace(String bankName, String bankEndpoint, Class<T> clazz) {
        return false;
    }

    @Override
    public <T> boolean handlePatch(String bankName, String bankEndpoint, Class<T> clazz) {
        return false;
    }
}
