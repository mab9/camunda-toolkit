package ch.mab.camunda.dev.process;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;


/*
    This is a simple execution listener for a delegate expression.

    developingListener is the Bean Id distributed by Spring! Compare with the delegate expression entry on camunda modeler
 */
@Service
public class DevelopingListener implements ExecutionListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());


    @Override
    public void notify(DelegateExecution delegate) {

        Map<String, Object> variables = delegate.getVariables();

        log.info("Method execute was invoked with following parameter:");  // this is not the way we should log ;-)
        variables.keySet().forEach(key -> log.info("Key: {} and value: {}", key, variables.get(key)));
    }
}
