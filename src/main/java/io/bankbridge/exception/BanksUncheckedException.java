package io.bankbridge.exception;

import javax.ws.rs.core.Response;

/***
 * This class represents custom runtime exception for Bank-Bridge Application.
 * This is for transforming such exceptions to json response
 * @see BanksErrorModel
 * @see BanksCheckedException
 */
public class BanksUncheckedException extends RuntimeException {
    private final Response.StatusType errorType;

    public BanksUncheckedException(String errorMessage, Throwable error, Response.StatusType errorType) {
        super(errorMessage, error);
        this.errorType = errorType;
    }

    public Response.StatusType getErrorType() {
        return errorType;
    }
}
