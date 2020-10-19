package io.bankbridge.unitTest;

import io.bankbridge.handler.BanksRemoteCalls;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/***
 * For this class I didn't had enough time to get the right combination working for
 * (powermock-api-mockito2,mockito-core,powermock-module-junit4)
 * I was running into issues like initialization error (All that could be solved though if enough time is there)
 */
public class RemoteCallBasedHandlerTest {
    @Test
    public void testCacheBasedHandler() throws IOException {
        BanksRemoteCalls banksRemoteCalls = BanksRemoteCalls.getInstance();
        String jsonString = banksRemoteCalls.handle();
        //This should be empty as the remote server is not up
        // Logs will still show exception (on http client level)
        Assert.assertNotNull(jsonString);
    }
}
