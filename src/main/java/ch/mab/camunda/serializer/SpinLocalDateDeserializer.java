package ch.mab.camunda.serializer;

import spinjar.com.fasterxml.jackson.core.JsonParser;
import spinjar.com.fasterxml.jackson.databind.DeserializationContext;
import spinjar.com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SpinLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String date = parser.getValueAsString();
        return LocalDate.parse(date, formatter);
    }
}
