package ch.mab.camunda;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;

// https://docs.camunda.org/manual/7.5/user-guide/testing/
// https://camunda.com/best-practices/testing-process-definitions/
public class DevelopingProcessTest extends AbstractProcessEngineRuleTest {

    private final String KEY_DEVELOPMENT_PROCESS = "development-process";

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void test_demo() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        assertThat(processInstance).isWaitingAt("Commitment");
    }

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void testDevelopmentProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);

        assertThat(processInstance).isWaitingAt("Commitment");

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        assertThat(processInstance).isWaitingAt("Commitment");

        runtimeService.setVariable(task.getExecutionId(), "committed", "false");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().singleResult();
        assertThat(processInstance).isWaitingAt("Commitment");
    }
}
