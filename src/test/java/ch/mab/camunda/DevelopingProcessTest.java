package ch.mab.camunda;

import ch.mab.camunda.dev.process.DevelopingService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

// https://docs.camunda.org/manual/7.5/user-guide/testing/
// https://camunda.com/best-practices/testing-process-definitions/

public class DevelopingProcessTest extends AbstractProcessEngineRuleTest {

    @Mock // Mockito mock instantiated by PowerMockRunner
    private DevelopingService developingService;

    @After
    public void teardown() {
        Mocks.reset();
    }

    @Test
    @Deployment(resources = "development-process.bpmn")
    public void testDevelopmentProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("development-process");

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        Assertions.assertEquals("Commitment", task.getName());
    }

}
