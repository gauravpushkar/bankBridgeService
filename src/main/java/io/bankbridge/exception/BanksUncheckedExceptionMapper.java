package io.bankbridge.exception;

import io.bankbridge.utils.BanksUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/***
 * Checked Exception Mapper for BankBridge Application.
 * This should catch all the checked exceptions (which were transformed to {@link BanksUncheckedException})
 * and transform them into client friendly response.
 * @see BanksErrorModel
 * @see BanksUncheckedException
 */
@Provider
public class BanksUncheckedExceptionMapper implements ExceptionMapper<BanksUncheckedException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksUncheckedExceptionMapper.class);

    @Override
    public Response toResponse(BanksUncheckedException ex) {
        //No need to log full stacktrace here as this must be logged where it was thrown from.
        LOGGER.error("Transforming exception to error response {}  with status code {}", ex.getMessage(),
                ex.getErrorType());
        Response.StatusType type = ex.getErrorType();
        BanksErrorModel error = new BanksErrorModel(type.getStatusCode(), type.getReasonPhrase(),
                ex.getLocalizedMessage());
        return BanksUtils.buildErrorResponse(error);
    }
}