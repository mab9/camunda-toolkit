package ch.mab.camunda.dev.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/*
    This is a simple delegate expression for a service task

    logService is the Bean Id distributed by Spring! Compare with the delegate expression entry on camunda modeler
 */
@Service
public class LogService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /*
        This method is invoked by the camunda process!
     */
    public void log(String customParams) {
        log.info("those are the provided params: {} ", customParams);
    }

    public void doNothing() {
        log.info("do nothing");
    }

}
