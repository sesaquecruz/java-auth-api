package org.auth.api.infrastructure.config.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.Callable;

public final class Json {
    private Json() { }

    private static final ObjectMapper mapper = new Jackson2ObjectMapperBuilder()
            .featuresToDisable(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
            )
            .build();

    public static ObjectMapper mapper() {
        return mapper.copy();
    }

    public static String marshal(final Object obj) {
        return invoke(() -> mapper.writeValueAsString(obj));
    }

    public static <T> T unmarshal(final String json, final Class<T> clazz) {
        return invoke(() -> mapper.readValue(json, clazz));
    }

    private static <T> T invoke(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
