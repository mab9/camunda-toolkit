package ch.mab.camunda.serializer;

import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat;
import org.camunda.spin.spi.DataFormatConfigurator;
import spinjar.com.fasterxml.jackson.databind.ObjectMapper;
import spinjar.com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDate;

public class JacksonDataFormatConfigurator implements DataFormatConfigurator<JacksonJsonDataFormat> {

    public void configure(JacksonJsonDataFormat dataFormat) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new SpinLocalDateSerializer());
        module.addDeserializer(LocalDate.class, new SpinLocalDateDeserializer());

        ObjectMapper objectMapper = dataFormat.getObjectMapper();
        objectMapper.registerModule(module);
    }

    public Class<JacksonJsonDataFormat> getDataFormatClass() {
        return JacksonJsonDataFormat.class;
    }

}
