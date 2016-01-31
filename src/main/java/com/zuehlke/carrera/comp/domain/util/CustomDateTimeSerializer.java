package com.zuehlke.carrera.comp.domain.util;

import java.io.IOException;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom Jackson serializer for displaying Joda DateTime objects.
 */
public class CustomDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static DateTimeFormatter formatter = DateTimeFormat
            .forPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        generator.writeString(formatter.print(value.toDateTime()));
    }

}
