package io.bankbridge.exception;

import io.bankbridge.utils.BanksUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/***
 * Checked Exception Mapper for BankBridge Application.
 * This should catch all the checked exceptions (which were transformed to {@link BanksCheckedException})
 * and transform them into client friendly response.
 * @see BanksErrorModel
 * @see BanksCheckedException
 */
@Provider
public class BanksCheckedExceptionMapper implements ExceptionMapper<BanksCheckedException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksCheckedExceptionMapper.class);
    @Override
    public Response toResponse(BanksCheckedException ex) {
        //No need to log full stacktrace here, as this must be logged where it was thrown from.
        LOGGER.error("Transforming exception to error response {}  with status code", ex.getMessage(),ex.getErrorType());
        Response.StatusType type = ex.getErrorType();
        BanksErrorModel error = new BanksErrorModel(type.getStatusCode(), type.getReasonPhrase(), ex.getLocalizedMessage());
        //build error response and return response to client.
        return BanksUtils.buildErrorResponse(error);
    }
}
