package ch.mab.camunda;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import ch.mab.camunda.dev.process.DevelopingDelegate;
import ch.mab.camunda.dev.process.DevelopingListener;
import ch.mab.camunda.dev.process.LogService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.ClassRule;
import org.junit.Ignore;
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
public class DevelopingProcessMockedTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule processEngineRule;

    private final String KEY_DEVELOPMENT_PROCESS = "development-process";
    private final String TASK_ID_COMMITMENT = "Commitment";
    private final String TASK_ID_G0 = "go";
    private final String TASK_ID_PLANNING = "planning";
    private final String TASK_ID_DEVELOPING = "Developing";
    private final String TASK_ID_TESTING = "Testing";
    private final String TASK_ID_DEPLOYMENT = "Deployment";
    private final String TASK_ID_REVIEW = "Review";

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

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

    @Ignore ("error: annotation @Deployment deletes deployment for DevelopingProcessMockedTest.start_process_byManualDeployment")
    @Test
    public void start_process_byManualDeployment() {
        org.camunda.bpm.engine.repository.Deployment deploy = repositoryService.createDeployment().addClasspathResource(KEY_DEVELOPMENT_PROCESS + ".bpmn").deploy();
        // loads all deployed process definitions
        //List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinition.getKey());
        assertThat(processInstance).isStarted();
    }

    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void testDevelopmentProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);
        // committed is a default variable...
        //ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS).setVariable("committed", false).execute();

        Task task = taskService.createTaskQuery().singleResult();
        final String executionId = task.getExecutionId();

        assertThat(processInstance).isWaitingAt("Commitment");

        // human task
        runtimeService.setVariable(executionId, "committed", false);
        complete(task());
        assertThat(processInstance).isWaitingAt("Commitment");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "committed", true);
        complete(task());
        assertThat(processInstance).isWaitingAt("go");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "go", false);
        complete(task());
        assertThat(processInstance).isWaitingAt("go");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "go", true);
        complete(task());
        assertThat(processInstance).isEnded();

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().activityType("serviceTask").list();
        list.addAll(historyService.createHistoricActivityInstanceQuery().activityType("userTask").list());
        list.sort(Comparator.comparing(HistoricActivityInstance::getStartTime));

        // the process has 12 tasks (service and user tasks)
        Assertions.assertEquals(12, list.size());

        // assert the expected history with all the tasks that could not be tested
        // within the normal flow from above.
        List<String> expectedHistory = Arrays.asList(TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_DEVELOPING, TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEVELOPING, TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEPLOYMENT, TASK_ID_REVIEW);
        for (int i = 0; i < expectedHistory.size(); i++) {
            Assertions.assertEquals(expectedHistory.get(i), list.get(i).getActivityId());
        }

        // shorter way without history service
        assertThat(processInstance).isEnded().hasPassedInOrder(
            TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_DEVELOPING, TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEVELOPING, TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEPLOYMENT, TASK_ID_REVIEW);
    }
}
