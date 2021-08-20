package ch.mab.camunda.externaltask.process;

import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.runtime.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalTaskWorker {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final String EXTERNAL_TASK_TOPIC = "worker";
    private final String EXTERNAL_TASK_WORKER_ID = "externalWorkerId";

    @Autowired
    private ExternalTaskService externalTaskService;

    @Autowired
    private RuntimeService runtimeService;

    @Scheduled(fixedDelay = 15000)
    private void scheduleExternalTasks() {

        log.info("External worker started");

        checkExternalTasks();
        checkIncidents();
        List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(1, EXTERNAL_TASK_WORKER_ID).topic(EXTERNAL_TASK_TOPIC, 60 * 1000).execute();
        checkExternalTasks();

        for (LockedExternalTask task : tasks) {
            String topic = task.getTopicName();
            String activityId = task.getActivityId();
            String workerId = task.getWorkerId();

            log.info("--> start external task with activityId '{}', topic '{}' and workerId '{}'.", activityId, topic, workerId);

            // do some heavy work
            checkLockedExternalTasks();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // set interrupted flag and ignore rest
            }
            checkLockedExternalTasks();


            log.info("<-- end external task with activityId '{}', topic '{}' and workerId '{}'.", activityId, topic, workerId);
            // if the work is successful, mark the task as completed
            //externalTaskService.complete(task.getId(), EXTERNAL_TASK_WORKER_ID);
            externalTaskService.handleFailure(task.getId(), EXTERNAL_TASK_WORKER_ID, "No space left on device.", 0, 3000);  // 0 = retries, 3000 = lock duration
            checkIncidents();
            checkExternalTasks();
            checkLockedExternalTasks();
        }
    }

    private void checkIncidents() {
        List<Incident> incidents = runtimeService.createIncidentQuery().list();


        if (!incidents.isEmpty()) {
            String activityId = incidents.get(0).getActivityId();
            log.info("Incident with activityId '{}'.", activityId);
        } else {
            log.info("no incident found");
        }
    }

    private void checkExternalTasks() {
        ExternalTask task = externalTaskService.createExternalTaskQuery().singleResult();

        if (task != null) {
            String topic = task.getTopicName();
            String activityId = task.getActivityId();
            String workerId = task.getWorkerId();
            log.info("external task with activityId '{}', topic '{}' and workerId '{}' exists.", activityId, topic, workerId);
        } else {
            log.info("no external task found");
        }
    }

    private void checkLockedExternalTasks() {
        ExternalTask task = externalTaskService.createExternalTaskQuery().locked().singleResult();

        if (task != null) {
            String topic = task.getTopicName();
            String activityId = task.getActivityId();
            String workerId = task.getWorkerId();
            log.info("external task with activityId '{}', topic '{}' and workerId '{}' is still locked.", activityId, topic, workerId);
        } else {
            log.info("no external task locked");
        }
    }
}
