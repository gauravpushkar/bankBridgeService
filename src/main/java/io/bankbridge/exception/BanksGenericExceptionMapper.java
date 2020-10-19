package io.bankbridge.exception;

import io.bankbridge.utils.BanksUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/***
 * Generic Exception Mapper for BankBridge Application.
 * This should catch all uncaught exceptions and transform them into client friendly response
 */
@Provider
public class BanksGenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksGenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        LOGGER.error("Transforming exception to error response", ex);
        //This is global exception mapper so 500 should be fine
        Response.StatusType type = Response.Status.INTERNAL_SERVER_ERROR;
        BanksErrorModel error = new BanksErrorModel(type.getStatusCode(), type.getReasonPhrase(),
                ex.getLocalizedMessage());
        return BanksUtils.buildErrorResponse(error);
    }
}
