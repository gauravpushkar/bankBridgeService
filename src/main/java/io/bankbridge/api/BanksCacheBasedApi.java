package io.bankbridge.api;

import io.bankbridge.handler.BanksCacheBased;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

/****
 * Banks API V1 Implementation.
 * This implementation fetches data from in-memory cache
 */
@Path("/v1/banks/all")
public class BanksCacheBasedApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksCacheBasedApi.class);
    private static final BanksCacheBased BANKS_CACHE_BASED = BanksCacheBased.getInstance();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBanks(@Suspended final AsyncResponse asyncResponse) {
        String jsonResponse = BANKS_CACHE_BASED.handle();
        LOGGER.info("Response-v1 :: {}", jsonResponse);
        asyncResponse.resume(jsonResponse);
    }
}
