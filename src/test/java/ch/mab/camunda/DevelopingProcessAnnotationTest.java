package ch.mab.camunda;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import ch.mab.camunda.dev.process.DevelopingDelegate;
import ch.mab.camunda.dev.process.DevelopingListener;
import ch.mab.camunda.dev.process.DevelopingService;
import ch.mab.camunda.dev.process.LogService;
import javax.annotation.PostConstruct;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {InMemProcessEngineConfiguration.class})
public class DevelopingProcessAnnotationTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule;

    private final String KEY_DEVELOPMENT_PROCESS = "development-process";

    @Autowired
    ProcessEngine processEngine;

    @MockBean(name = "developingListener")
    DevelopingListener developingListener;

    @MockBean(name = "developingDelegate")
    DevelopingDelegate developingDelegate;

    @MockBean(name = "logService")
    LogService logService;

    @Autowired
    TaskService taskService;

    @PostConstruct
    void initRule() {
        rule = TestCoverageProcessEngineRuleBuilder.create(processEngine).build();
    }

/*    @After
    public void calculateCoverageForAllTests() throws Exception {
        ProcessTestCoverage.calculate(rule.getProcessEngine());
    }*/

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void test_demo() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);
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

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void testDevelopmentProcess2() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);

        assertThat(processInstance).isWaitingAt("Commitment");

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();

        runtimeService.setVariable(task.getExecutionId(), "committed", "true");
        taskService.complete(task.getId());
        assertThat(processInstance).isWaitingAt("go");
    }

}
