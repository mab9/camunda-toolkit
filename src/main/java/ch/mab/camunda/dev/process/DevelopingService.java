package ch.mab.camunda.dev.process;

import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/*
    This is a simple delegate expression for a service task

    developingService is the Bean Id distributed by Spring! Compare with the delegate expression entry on camunda modeler
 */
@Service
public class DevelopingService implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void execute(DelegateExecution delegate) {
        Map<String, Object> variables = delegate.getVariables();

        log.info("Hard work for devs. Service task params are following.");  // this is not the way we should log ;-)
        variables.keySet().forEach(key -> log.info("Key: {} and value: {}", key, variables.get(key)));
    }
}
