package ca.reppy.reppy_backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Encode: Object -> JSON String
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode to JSON", e);
        }
    }

    // Decode: JSON String -> Object of given class
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode from JSON", e);
        }
    }
}
