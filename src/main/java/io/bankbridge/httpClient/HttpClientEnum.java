package io.bankbridge.httpClient;

/***
 * Enum representing possible different implementations for HttpClient
 */
public enum HttpClientEnum {
    /*This is just to show how we might end up supporting multiple clients
    In real world scenario this could be legacy clients or new clients
    or same could be derived from property than an Enum
     */
    APACHE,
    GOOGLE,
    CUSTOM
}
