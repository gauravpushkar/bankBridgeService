package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bankbridge.cache.CacheFactory;
import io.bankbridge.exception.BanksUncheckedException;
import io.bankbridge.model.BankModelList;
import io.bankbridge.utils.ApiVersionEnum;
import io.bankbridge.utils.BanksUtils;
import io.bankbridge.utils.Constants;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/***
 * Backend implementation for {@link io.bankbridge.api.BanksCacheBasedApi}
 */
public class BanksCacheBased implements IBanksHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksCacheBased.class);
    private static final BanksCacheBased INSTANCE = new BanksCacheBased();
    private static final AtomicBoolean CACHE_INITIALIZED = new AtomicBoolean(false);
    private static final ReentrantLock lock = new ReentrantLock();
    private static Cache<String, String>  cache;

    public static BanksCacheBased getInstance() {
        return INSTANCE;
    }

    //This could have been tied with bean lifecycle with a Dependency injection framework
    // However for this assignment we can invoke it once and rest all threads will find it initialized
    // This arrangement is optimal and won't have any scale impact (it's just lazy init)
    public void init() throws Exception {
        lock.lock();
        //validate again in case any thread was blocked and meanwhile cache is init by some other thread
        if (CACHE_INITIALIZED.get()) {
            LOGGER.warn("Cache is already initialized , returning");
            return;
        }
        try {
            cache = CacheFactory.getCache(ApiVersionEnum.V1);
            //Parse json and create the model collection
            BankModelList models = BanksUtils.getParsedObject(Constants.JSON_FILE_V1, BankModelList.class);
            models.getBanks().forEach(bankModel -> cache.put(bankModel.getBic(), bankModel.getName()));
        } catch (Exception ex) {
            LOGGER.error("Error occurred while initializing  cache from banks-v1.json", ex);
            throw ex;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String handle() {
        if (!CACHE_INITIALIZED.get()) {
            try {
                init();
            } catch (Exception ex) {
                //Transform the exception and throw
                throw new BanksUncheckedException("Error occurred while initializing cache for V1 endpoint", ex,
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        List<Map<String, String>> result = new ArrayList<>();
        cache.forEach(entry -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", entry.getKey());
            map.put("name", entry.getValue());
            result.add(map);
        });
        try {
            String resultAsString = BanksUtils.getObjectMapper().writeValueAsString(result);
            LOGGER.debug("Successfully returning data for V1 endpoint {}",resultAsString);
            return resultAsString;
        } catch (JsonProcessingException ex) {
            //Transform the exception and throw
            throw new BanksUncheckedException("Error occurred while handling request for V1 endpoint", ex,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
