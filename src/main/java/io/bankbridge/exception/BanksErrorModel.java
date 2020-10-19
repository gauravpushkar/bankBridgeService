package io.bankbridge.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

/***
 * Error model for Banks application
 */
@JsonRootName("error")
public class BanksErrorModel {
    private int httpStatusCode;
    private String description;
    @JsonIgnore
    //Detailed error message is for logging purposes only, not for clients
    private String errorMessage;

    public BanksErrorModel(int httpStatusCode, String description, String errorMessage) {
        this.httpStatusCode = httpStatusCode;
        this.description = description;
        this.errorMessage = errorMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
