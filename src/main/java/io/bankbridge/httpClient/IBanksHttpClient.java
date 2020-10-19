package io.bankbridge.httpClient;

/***
 * HttpClient contract for Banks Application
 */
public interface IBanksHttpClient {
    <T> T handleGet(String bankName, String bankEndpoint, Class<T> clazz);

    <T> boolean  handlePost(String bankName, String bankEndpoint, Class<T> clazz);

    <T> boolean handlePut(String bankName, String bankEndpoint, Class<T> clazz);

    <T> boolean handleDelete(String bankName, String bankEndpoint, Class<T> clazz);

    <T> boolean handleConnect(String bankName, String bankEndpoint, Class<T> clazz);

    <T> boolean handleTrace(String bankName, String bankEndpoint, Class<T> clazz);

    <T> boolean handlePatch(String bankName, String bankEndpoint, Class<T> clazz);

}
