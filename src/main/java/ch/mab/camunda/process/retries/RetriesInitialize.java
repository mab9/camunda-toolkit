package ch.mab.camunda.process.retries;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/***
 *  This class simulates a task that initializes something.
 */
@Service
public class RetriesInitialize {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /*
        This method is invoked by the camunda process!
    */
    public void init() {
        log.info("initialize something");
    }
}
