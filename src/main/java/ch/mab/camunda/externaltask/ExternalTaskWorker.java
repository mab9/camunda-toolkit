package ch.mab.camunda.externaltask;

import org.camunda.bpm.engine.rest.dto.externaltask.LockedExternalTaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalTaskWorker {


    private static final Logger LOG = LoggerFactory.getLogger(ExternalTaskWorker.class);

    @Autowired
    private ExternalTaskService externalTaskService;

    @Scheduled(fixedDelayString = "${camunda.external.tasks.polling.interval:30000}")
    private void taskRunner() {
        LOG.info("Worker '{}' scheduled start", "workerId");

        List<LockedExternalTaskDto> tasks = externalTaskService.fetchAndLock("externalWorkerId", "101010", 10);

        for (LockedExternalTaskDto task : tasks) {
            try {
                String topic = task.getTopicName();

                // work on task for that topic
//    ...

                // if the work is successful, mark the task as completed
                //if (success) {
                //    externalTaskService.complete(task.getId(), variables);
                //} else {
                //    // if the work was not successful, mark it as failed
                //    externalTaskService.handleFailure(
                //            task.getId(),
                //            "externalWorkerId",
                //            "Address could not be validated: Address database not reachable",
                //            1, 10L * 60L * 1000L);
                //}
            } catch (Exception e) {
                //... handle exception
            }
        }
    }


}
