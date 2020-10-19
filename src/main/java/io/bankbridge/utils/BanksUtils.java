package io.bankbridge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.exception.BanksErrorModel;
import io.bankbridge.model.BankModel;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/***
 * Util class for the application
 */
public class BanksUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static Response buildErrorResponse(BanksErrorModel errorModel) {
        return Response.status(errorModel.getHttpStatusCode())
                .entity(errorModel)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static <T> T getParsedObject(String jsonFile, Class<T> clazz) throws IOException {
        T model = OBJECT_MAPPER.readValue(
                Thread.currentThread().getContextClassLoader().getResource(jsonFile), clazz);
        return model;
    }

    public static  <T> T transformJson(String jsonString, Class<T> clazz) throws IOException {
        T bankModel = new ObjectMapper().readValue(jsonString, clazz);
        return bankModel;
    }

    public static <T> String getJsonString(T model) throws JsonProcessingException {
        String jsonString = StringUtils.EMPTY;
        if (Objects.isNull(model)) {
            return jsonString;
        }
        return OBJECT_MAPPER.writeValueAsString(model);
    }

}
