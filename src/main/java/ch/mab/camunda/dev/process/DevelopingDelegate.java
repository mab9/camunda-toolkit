package ch.mab.camunda.dev.process;

import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/*
    This is a simple delegate expression for a service task

    developingDelegate is the Bean Id distributed by Spring! Compare with the delegate expression entry on camunda modeler
 */
@Service
public class DevelopingDelegate implements JavaDelegate {

    private Expression processPlanningExpression;
    private FixedValue processPlanningFixedValue;

    @Autowired
    private DevelopingService developingService;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public void execute(DelegateExecution delegate) {
        Map<String, Object> variables = delegate.getVariables();

        log.info("Input field value Expression 1 {}", processPlanningExpression);
        log.info("Input field value Expression 2 {}", processPlanningExpression.getExpressionText());
        log.info("Input field value FixedValue 1 {}", processPlanningFixedValue);
        log.info("Input field value FixedValue 2 {}", processPlanningFixedValue.getExpressionText());


        log.info("Devs implement a incredible feature: {}", developingService.developeNewFeature());

        log.info("Hard work for devs. Service task params are following.");  // this is not the way we should log ;-)
        variables.keySet().forEach(key -> log.info("Key: {} and value: {}", key, variables.get(key)));
    }
}
