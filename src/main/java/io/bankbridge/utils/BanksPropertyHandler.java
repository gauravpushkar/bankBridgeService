package io.bankbridge.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/***
 * Implementation to load property file ( json type)
 */
public class BanksPropertyHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksPropertyHandler.class);
    public static final BanksPropertyHandler PROPERTY_HANDLER = new BanksPropertyHandler();
    private BanksApplicationProperties applicationProperties = null;

    private BanksPropertyHandler() {
        try {
            applicationProperties = BanksUtils.getObjectMapper().readValue(
                    Thread.currentThread().getContextClassLoader().getResource("application-properties.json"),
                    BanksApplicationProperties.class);
        } catch (IOException ex) {
            LOGGER.error("Error occurred while initializing property file", ex);
            //Use default properties initialized in BanksApplicationProperties
            applicationProperties = new BanksApplicationProperties();
        }
    }

    public static BanksPropertyHandler getInstance() {
        return PROPERTY_HANDLER;
    }

    public BanksApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    /****
     * This class represents the properties loaded for this application.
     * It should be refreshed with "application-properties.json" if NOT, default values will be used.
     */
    public static class BanksApplicationProperties {

        /* HTTP Client Properties */
        private int httpClientConnectionRequestTimeout = 3000;
        private int httpClientConnectTimeout = 3000;
        private int httpClientSocketTimeout = 3000;
        private int httpClientMaxPooledConnections = 5;
        private int httpClientDefaultMaxPerRoute = 4;

        /*Server Properties*/
        private int jettyServerPort = 8080;
        private String jettyServerIpAddressToBind = "127.0.01";

        /*Cache Properties*/
        private int ehcacheHeapEntryUnit = 2000;

        /* Thread pool termination wait time in seconds*/
        private int threadPoolTerminationWaitTime = 2;

        /* Remote Banks Server Port */
        private int remoteBankServerPort = 1234;
        /* Timeout for async request  top level timeout*/
        private int asyncRequestTimeout = 4000;

        public int getAsyncRequestTimeout() {
            return asyncRequestTimeout;
        }

        public void setAsyncRequestTimeout(int asyncRequestTimeout) {
            this.asyncRequestTimeout = asyncRequestTimeout;
        }

        public int getRemoteBankServerPort() {
            return remoteBankServerPort;
        }

        public void setRemoteBankServerPort(int remoteBankServerPort) {
            this.remoteBankServerPort = remoteBankServerPort;
        }

        public int getThreadPoolTerminationWaitTime() {
            return threadPoolTerminationWaitTime;
        }

        public void setThreadPoolTerminationWaitTime(int threadPoolTerminationWaitTime) {
            this.threadPoolTerminationWaitTime = threadPoolTerminationWaitTime;
        }

        public int getEhcacheHeapEntryUnit() {
            return ehcacheHeapEntryUnit;
        }

        public void setEhcacheHeapEntryUnit(int ehcacheHeapEntryUnit) {
            this.ehcacheHeapEntryUnit = ehcacheHeapEntryUnit;
        }

        public int getJettyServerPort() {
            return jettyServerPort;
        }

        public void setJettyServerPort(int jettyServerPort) {
            this.jettyServerPort = jettyServerPort;
        }

        public String getJettyServerIpAddressToBind() {
            return jettyServerIpAddressToBind;
        }

        public void setJettyServerIpAddressToBind(String jettyServerIpAddressToBind) {
            this.jettyServerIpAddressToBind = jettyServerIpAddressToBind;
        }

        public int getHttpClientConnectionRequestTimeout() {
            return httpClientConnectionRequestTimeout;
        }

        public void setHttpClientConnectionRequestTimeout(int httpClientConnectionRequestTimeout) {
            this.httpClientConnectionRequestTimeout = httpClientConnectionRequestTimeout;
        }

        public int getHttpClientConnectTimeout() {
            return httpClientConnectTimeout;
        }

        public void setHttpClientConnectTimeout(int httpClientConnectTimeout) {
            this.httpClientConnectTimeout = httpClientConnectTimeout;
        }

        public int getHttpClientSocketTimeout() {
            return httpClientSocketTimeout;
        }

        public void setHttpClientSocketTimeout(int httpClientSocketTimeout) {
            this.httpClientSocketTimeout = httpClientSocketTimeout;
        }

        public int getHttpClientMaxPooledConnections() {
            return httpClientMaxPooledConnections;
        }

        public void setHttpClientMaxPooledConnections(int httpClientMaxPooledConnections) {
            this.httpClientMaxPooledConnections = httpClientMaxPooledConnections;
        }

        public int getHttpClientDefaultMaxPerRoute() {
            return httpClientDefaultMaxPerRoute;
        }

        public void setHttpClientDefaultMaxPerRoute(int httpClientDefaultMaxPerRoute) {
            this.httpClientDefaultMaxPerRoute = httpClientDefaultMaxPerRoute;
        }
    }

}
