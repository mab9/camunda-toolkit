package ch.mab.camunda.process.retries;


import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/***
 *  This class simulates a task that gets executed several time, until it succeed.
 *  The task throws an incident until the variable 'processDone' is set to true.
 *  The variable 'processDone' may be set via the human task or via the cockpit.
 */
@Service
public class RetriesDummyTask {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    /*
        This method is invoked by the camunda process!
    */
    public void execute(boolean success) {

        // Don't use this code on production. This is only for demo
        processEngineConfiguration.setCreateIncidentOnFailedJobEnabled(true);
        log.info("execute task with incident: {}", success);
        if (!success) {
            throw new NullPointerException("artificially created error");
        }
    }
}
