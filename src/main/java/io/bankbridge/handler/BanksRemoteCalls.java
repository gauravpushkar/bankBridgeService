package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bankbridge.exception.BanksUncheckedException;
import io.bankbridge.httpClient.BanksHttpClientFactory;
import io.bankbridge.httpClient.HttpClientEnum;
import io.bankbridge.model.BankModel;
import io.bankbridge.utils.BanksPropertyHandler;
import io.bankbridge.utils.BanksUtils;
import io.bankbridge.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/***
 * @author gauravk
 * Backend implementation for remote {@link io.bankbridge.api.BanksRemoteCallsApi}
 * <p>This implementation invokes all the bank endpoints in async fashion using {@link CompletableFuture}
 * and {@link org.apache.http.client.HttpClient}.
 * </p>
 */
public class BanksRemoteCalls implements IBanksHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksRemoteCalls.class);
    private static final BanksRemoteCalls INSTANCE = new BanksRemoteCalls();
    private static final AtomicBoolean BANKS_ENDPOINT_DETAILS_LOADED = new AtomicBoolean(false);
    private static final ReentrantLock lock = new ReentrantLock();
    //private static final Cache<String, String> CACHE = CacheFactory.getCache(ApiVersionEnum.V2);
    private static Map<String, String> config;

    public static BanksRemoteCalls getInstance() {
        return INSTANCE;
    }

    //This could have been tied with bean lifecycle with a Dependency injection framework
    // However for this assignment we can invoke it once and rest all threads will find it initialized
    // This arrangement is optimal and won't have any scale impact (it's just lazy init)
    public static void init() throws Exception {
        lock.lock();
        //validate again in case any thread was blocked and meanwhile endpoints are read by some other thread
        if (BANKS_ENDPOINT_DETAILS_LOADED.get()) {
            LOGGER.warn("EndPoints are already loaded in-memory , returning");
            return;
        }
        try {
            config = BanksUtils.getParsedObject(Constants.JSON_FILE_V2, HashMap.class);
        } catch (Exception ex) {
            LOGGER.error("Error occurred while loaded bank endpoints in-memory from banks-v2.json", ex);
            throw ex;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String handle() {
        if (!BANKS_ENDPOINT_DETAILS_LOADED.get()) {
            try {
                init();
            } catch (Exception ex) {
                //Transform the exception and throw
                throw new BanksUncheckedException("Error occurred while loading bank-endpoints in memory from " +
                        "banks-v2.json", ex,
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        String result = null;
        //This pool can be reused for all request at can be global but it needs to benchmarked and then number of
        // threads need to be set accordingly
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        List<CompletableFuture<BankModel>> completableFutureList = new ArrayList<>();
        List<Map<String, String>> dataPresentInCache = new ArrayList<>();
        try {
            config.forEach((key, value) -> {

                CompletableFuture<BankModel> completableFuture = CompletableFuture.supplyAsync(() -> {
                    BankModel bankModel = BanksHttpClientFactory.getHttpClient(HttpClientEnum.APACHE).handleGet(key,
                            value, BankModel.class);
                    LOGGER.info("Fetched data from bank ::  {} with url :: {},\ndata:: {}", key, value, bankModel);
                    return bankModel;
                }, threadPool).exceptionally(ex -> {
                    LOGGER.error("Error occurred while fetching data from bank {} with endpoint {}", key, value, ex);
                    //ignore exception and collect successful results
                    return null;
                });
                completableFutureList.add(completableFuture);
            });


            //Aggregate all Futures
            CompletableFuture<Void> futures =
                    CompletableFuture.allOf(completableFutureList.toArray
                            (new CompletableFuture[completableFutureList.size()]));

            //Map the return type to a List
            CompletableFuture<List<BankModel>> aggregatedFuture = futures.thenApply(future -> {
                return completableFutureList.stream().map(completableFuture -> completableFuture.join()).
                        filter(Objects::nonNull).collect(Collectors.toList());
            }).exceptionally(ex -> {
                String errorMsg = "Error occurred while aggregating the results";
                LOGGER.error(errorMsg, ex);
                throw new BanksUncheckedException(errorMsg, ex.getCause(),
                        Response.Status.INTERNAL_SERVER_ERROR);
            });

            List<BankModel> bankModels = aggregatedFuture.get();
            result = BanksUtils.transformResult(bankModels);
        } catch (JsonProcessingException | ExecutionException | InterruptedException ex) {
            //Any exception at this stage is a candidate of 5XX
            String errorMsg = "Error occurred while processing request for V2 endpoint";
            LOGGER.error("errorMsg", ex);
            //transform the error so that we can send error response to client
            throw new BanksUncheckedException(errorMsg, ex.getCause(),
                    Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            threadPool.shutdown();
            try {
                //Default wait time is 2 seconds which can be configured as well
                while (!threadPool.awaitTermination(BanksPropertyHandler.getInstance().getApplicationProperties().
                        getThreadPoolTerminationWaitTime(), TimeUnit.SECONDS)) {
                    LOGGER.warn("Waiting to shutdown thread pool");
                }
            } catch (InterruptedException ex) {
                throw new BanksUncheckedException("Error occurred while shutting down thread pool", ex.getCause(),
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        return result;
    }
}
