package io.bankbridge.exception;

import javax.ws.rs.core.Response;

/***
 * This class represents custom checked exception for Bank-Bridge Application.
 * This is for transforming such exceptions to json response
 * @see BanksUncheckedException
 * @see BanksErrorModel
 */
public class BanksCheckedException extends Exception {
    private final Response.StatusType errorType;

    public BanksCheckedException(String errorMessage, Throwable error, Response.StatusType errorType) {
        super(errorMessage, error);
        this.errorType = errorType;
    }

    public Response.StatusType getErrorType() {
        return errorType;
    }
}
