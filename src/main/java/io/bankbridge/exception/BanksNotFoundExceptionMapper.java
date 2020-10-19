package io.bankbridge.exception;

import io.bankbridge.utils.BanksUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
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
public class BanksNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanksNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(NotFoundException ex) {
        //No need to log full stacktrace here as this must be logged where it was thrown from.
        LOGGER.error("Transforming exception to error response {}  with status code {}", ex.getMessage(),
                Response.Status.NOT_FOUND);
        Response.StatusType type = Response.Status.NOT_FOUND;
        BanksErrorModel error = new BanksErrorModel(type.getStatusCode(), type.getReasonPhrase(),
                ex.getLocalizedMessage());
        return BanksUtils.buildErrorResponse(error);
    }
}