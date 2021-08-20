package ch.mab.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PostDeployProccesses {

    @Autowired
    private RuntimeService runtimeService;

    @EventListener
    public void processPostDeploy(PostDeployEvent event) {
        // runtimeService.startProcessInstanceByKey("schdulerMainProcess");
        // runtimeService.startProcessInstanceByKey("cycleTest");
        // runtimeService.startProcessInstanceByKey("external-task");
        // runtimeService.startProcessInstanceByKey("parallel_with_timer");
        // runtimeService.startProcessInstanceByKey("development-process");
        // runtimeService.startProcessInstanceByKey("postDeployProcess");
        runtimeService.startProcessInstanceByKey("retries-process");
    }
}
