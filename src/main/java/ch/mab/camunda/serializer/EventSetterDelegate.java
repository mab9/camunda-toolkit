package ch.mab.camunda.serializer;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.builder.ObjectValueBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class EventSetterDelegate implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void execute(DelegateExecution delegate) {
        log.info("Set event");
        Event event = new Event(LocalDate.of(2021, 6, 1), LocalDate.of(2021, 7, 15), "Vacations");
        setComplexVariable(delegate, "event", event);
    }

    void setComplexVariable(VariableScope variableScope, String constant, Object value) {
        variableScope.setVariable(constant, createJsonSerializedObject(value));
    }

    public ObjectValue createJsonSerializedObject(Object object) {
        ObjectValueBuilder builder = Variables.objectValue(object);
        return builder.serializationDataFormat(Variables.SerializationDataFormats.JSON).create();
    }
}
