package ch.mab.camunda.serializer;

import spinjar.com.fasterxml.jackson.core.JsonGenerator;
import spinjar.com.fasterxml.jackson.databind.SerializerProvider;
import spinjar.com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SpinLocalDateSerializer extends StdSerializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public SpinLocalDateSerializer() {
        super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(localDate));
    }
}
