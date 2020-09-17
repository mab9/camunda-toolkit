package ch.mab.camunda;

import static org.junit.Assert.assertEquals;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

// https://docs.camunda.org/manual/7.5/user-guide/testing/
// https://camunda.com/best-practices/testing-process-definitions/

//@RunWith(SpringRunner.class)
public class DevelopingProcessTest extends AbstractProcessEngineRuleTest {

    private final String KEY_DEVELOPMENT_PROCESS = "development-process";

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void test_demo() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        assertEquals("Commitment", task.getName());
    }

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void testDevelopmentProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        assertTaskName("Commitment", task);

        runtimeService.setVariable(task.getExecutionId(), "committed", "false");
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().singleResult();
        assertTaskName("Commitment", task);
    }

    private void assertTaskName(String name, Task task) {
        Assertions.assertEquals(name, task.getName());
    }

}
