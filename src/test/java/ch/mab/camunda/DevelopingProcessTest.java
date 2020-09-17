package ch.mab.camunda;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;

import ch.mab.camunda.dev.process.DevelopingDelegate;
import ch.mab.camunda.dev.process.DevelopingListener;
import ch.mab.camunda.dev.process.LogService;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {InMemProcessEngineConfiguration.class})
public class DevelopingProcessTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule processEngineRule;

    private final String KEY_DEVELOPMENT_PROCESS = "development-process";

    @Autowired
    TaskService taskService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    HistoryService historyService;

    @MockBean(name = "developingListener")
    DevelopingListener developingListener;

    @MockBean(name = "developingDelegate")
    DevelopingDelegate developingDelegate;

    @MockBean(name = "logService")
    LogService logService;

    @PostConstruct
    void initRule() {
        // do as well activate test coverage calculation
        // located at: target/process-test-coverage
        processEngineRule = TestCoverageProcessEngineRuleBuilder.create(processEngine).build();
    }

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void start_process() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);
        assertThat(processInstance).isStarted();
    }

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void testDevelopmentProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);
        // committed is a default variable...
        //ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS).setVariable("committed", false).execute();

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        final String executionId = task.getExecutionId();

        assertThat(processInstance).isWaitingAt("Commitment");

        // human task
        runtimeService.setVariable(executionId, "committed", false);
        complete(BpmnAwareTests.task());
        assertThat(processInstance).isWaitingAt("Commitment");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "committed", true);
        complete(BpmnAwareTests.task());
        assertThat(processInstance).isWaitingAt("go");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "go", false);
        complete(BpmnAwareTests.task());
        assertThat(processInstance).isWaitingAt("go");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "go", true);
        complete(BpmnAwareTests.task());
        assertThat(processInstance).isEnded();

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().activityType("serviceTask").list();
        list.addAll(historyService.createHistoricActivityInstanceQuery().activityType("userTask").list());
        list.sort(Comparator.comparing(HistoricActivityInstance::getStartTime));

        Assertions.assertEquals(12, list.size());

        // todo assert all tasks according execution

    }
}
