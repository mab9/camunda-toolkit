package ch.mab.camunda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/*
    This is a simple delegate expression for a service task

    logService is the Bean Id distributed by Spring! Compare with the delegate expression entry on camunda modeler
 */
@Service
public class LogService {

    // todo the diff to delegate execution -> no scope access

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /*
        This method is invoked by the camunda process!
     */
    public void log(String customParams) {
        log.info("those are the provided params: {} ", customParams);
    }

    /*
        This method is invoked by the camunda process!
    */
    public void doNothing() {
        log.info("do nothing");
    }

    public void printMessage(String message) {
        log.info("{}", message);
    }
}
