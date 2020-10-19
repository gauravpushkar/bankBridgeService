package io.bankbridge.api;

import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.utils.BanksPropertyHandler;
import io.bankbridge.utils.Constants;
import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/****
 * Banks API V2 implementation, this implementation fetches data from in-memory cache,if not available it hits the
 * Bank's remote end point to fetch and aggregate the data.
 */
@Path("/v2/banks/all")
public class BanksRemoteCallsApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksCacheBasedApi.class);
    private static final BanksRemoteCalls BANKS_REMOTE_CALLS = BanksRemoteCalls.getInstance();
    private static final BanksPropertyHandler PROPERTY_HANDLER = BanksPropertyHandler.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getBanks(@Suspended final AsyncResponse asyncResponse) {
        //configure time-out and set timeout handler, while debugging request this needs to be set to higher value
        asyncResponse.setTimeout(PROPERTY_HANDLER.getApplicationProperties().getAsyncRequestTimeout(), TimeUnit.MILLISECONDS);
        asyncResponse.setTimeoutHandler(ar -> ar.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(
                Constants.REQUEST_TIMED_OUT).build()));
        //invoke backend sub-routine
        String jsonResponse = BANKS_REMOTE_CALLS.handle();
        LOGGER.debug("Response-v2 :: {}", jsonResponse);
        //Handle response in async fashion as this could be a time consuming operation
        asyncResponse.resume(jsonResponse);
    }
}
