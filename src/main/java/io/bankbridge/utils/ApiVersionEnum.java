package io.bankbridge.utils;

/**
 * Enum representing API versioning
 */
public enum ApiVersionEnum {
    V1("cache-based"),
    V2("api-based");
    private String description;

    ApiVersionEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
