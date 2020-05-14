package ch.mab.camunda;

import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RequestProcessorService implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void execute(DelegateExecution delegate) throws Exception {
        Map<String, Object> variables = delegate.getVariables();

        log.info("Method execute was invoked");
        variables.keySet().forEach(key -> {
            log.info("Key: {} and value: {}", key, variables.get(key));
        });


        //

        // start bpmn
        // wait bei import
        // execute import
        // set vars
        //
    }
}
