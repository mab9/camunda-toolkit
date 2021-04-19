package ch.mab.camunda.serializer;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class EventGetterDelegate implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void execute(DelegateExecution delegate) {
        Map<String, Object> variables = delegate.getVariables();
        Event event = (Event) variables.get("event");
        long daysBetween = DAYS.between(event.getEndDate(), event.getStartDate());
        log.info("Logical calendar days between start end end event '{}'", daysBetween);


        event = (Event) getVariable(delegate, "event");
        daysBetween = DAYS.between(event.getEndDate(), event.getStartDate());
        log.info("Logical calendar days between start end end event '{}'", daysBetween);
    }

    Object getVariable(VariableScope variableScope, String constant) {
        return variableScope.getVariable(constant);
    }


}