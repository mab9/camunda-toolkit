package ch.mab.camunda;

import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/*
    This is a simple delegate expression for an service task
 */
@Service
public class RequestProcessorService implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void execute(DelegateExecution delegate) {
        Map<String, Object> variables = delegate.getVariables();

        log.info("Method execute was invoked with following parameter:");  // this is not the way we should log ;-)
        variables.keySet().forEach(key -> log.info("Key: {} and value: {}", key, variables.get(key)));
    }
}
