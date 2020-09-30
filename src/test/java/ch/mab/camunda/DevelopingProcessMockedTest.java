package ch.mab.camunda;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.externalTask;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import ch.mab.camunda.dev.process.DevelopingDelegate;
import ch.mab.camunda.dev.process.DevelopingListener;
import ch.mab.camunda.dev.process.LogService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
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

    private final String MULTI_INSTANCE_TASK_POSTFIX = "#multiInstanceBody";


    private final String KEY_DEVELOPMENT_PROCESS = "development-process";
    private final String TASK_ID_COMMITMENT = "Commitment";
    private final String TASK_ID_G0 = "go";
    private final String TASK_ID_PLANNING = "planning";
    private final String TASK_ID_DEVELOPING = "Developing";
    private final String TASK_ID_TESTING = "Testing";
    private final String TASK_ID_DEPLOYMENT = "Deployment";
    private final String TASK_ID_REVIEW = "Review";
    private final String TASK_ID_LOOPER = "Looper";
    private final String TASK_ID_RETRO = "Retro";
    private final String TASK_ID_RESET = "Reset";

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    HistoryService historyService;

    @Autowired
    ExternalTaskService externalTaskService;

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

    /*
        nothing is done here, as we just want to check for exceptions during
        deployment
     */
    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void start_process() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);
        assertThat(processInstance).isStarted();
    }

    /*
        Uses the bpmnAwareTest class to invoke services.
        Fantastic way to reduce the code used for the test setup.

        nothing is done here, as we just want to check for exceptions during
        deployment
     */
    @Ignore("Can't deploy: annotation @Deployment deletes deployment for DevelopingProcessMockedTest.start_process_byManualDeployment")
    @Test
    public void start_process_byManualDeployment() {
        org.camunda.bpm.engine.repository.Deployment deploy = repositoryService().createDeployment()
            .addClasspathResource(KEY_DEVELOPMENT_PROCESS + ".bpmn").deploy();
        // loads all deployed process definitions
        //List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        RuntimeService runtimeService = processEngine().getRuntimeService();
        ProcessDefinition processDefinition = repositoryService().createProcessDefinitionQuery()
            .deploymentId(deploy.getId()).singleResult();
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

        // asynchronous service tasks has to be handled
        assertThat(processInstance).isWaitingAt(TASK_ID_PLANNING);
        execute(job());

        Task task = taskService.createTaskQuery().singleResult();

        // same as processInstance.getId(). Pay attention this ID may change!
        final String executionId = task.getExecutionId();

        assertThat(processInstance).isWaitingAt("Commitment");

        // human task
        runtimeService.setVariable(executionId, "committed", false);
        complete(task());

        // asynchronous service tasks has to be handled
        assertThat(processInstance).isWaitingAt(TASK_ID_PLANNING);
        execute(job());

        assertThat(processInstance).isWaitingAt("Commitment");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "committed", true);
        complete(task());

        // asynchronous external task has to be handled
        assertThat(processInstance).isWaitingAt("Testing");
        ExternalTask externalTask = externalTask(processInstance);
        assertThat(externalTask).hasActivityId("Testing");
        assertThat(externalTask).hasTopicName("externals");
        complete(externalTask);

        assertThat(processInstance).isWaitingAt("go");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "go", false);
        complete(task());

        // asynchronous external task has to be handled
        assertThat(processInstance).isWaitingAt("Testing");
        externalTask = externalTask(processInstance);
        assertThat(externalTask).hasActivityId("Testing");
        assertThat(externalTask).hasTopicName("externals");
        complete(externalTask);

        assertThat(processInstance).isWaitingAt("go");

        // human task
        runtimeService.setVariable(task.getExecutionId(), "go", true);
        complete(task());

        // asynchronous service tasks has to be handled
        assertMultiInstanceTaskIsWaitingAt(TASK_ID_LOOPER);
        execute(job());

        // asynchronous multi instance task has to be handled
        assertThat(processInstance).isWaitingAt(TASK_ID_RETRO);
        List<Job> jobs = processEngineRule.getManagementService().createJobQuery().list();
        Assertions.assertEquals(2, jobs.size());   // 2 loops is defined within the process definition
        jobs.forEach(BpmnAwareTests::execute);

        // asynchronous service tasks has to be handled
        assertThat(processInstance).isWaitingAt(TASK_ID_RESET);
        execute(job());

        assertThat(processInstance).isEnded();

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
            .activityType("serviceTask").list();
        list.addAll(historyService.createHistoricActivityInstanceQuery().activityType("userTask").list());
        list.sort(Comparator.comparing(HistoricActivityInstance::getStartTime));

        // the process passes x tasks (service and user tasks) inclusive the loops
        Assertions.assertEquals(15, list.size());

        // assert the expected history with all the tasks that could not be tested
        // within the normal flow from above.
        List<String> expectedHistory = Arrays
            .asList(TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_DEVELOPING,
                TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEVELOPING, TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEPLOYMENT,
                TASK_ID_REVIEW, TASK_ID_RETRO, TASK_ID_RETRO, TASK_ID_RESET);
        for (int i = 0; i < expectedHistory.size(); i++) {
            Assertions.assertEquals(expectedHistory.get(i), list.get(i).getActivityId());
        }

        // shorter way without history service
        assertThat(processInstance).isEnded().hasPassedInOrder(
            TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_PLANNING, TASK_ID_COMMITMENT, TASK_ID_DEVELOPING,
            TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEVELOPING, TASK_ID_TESTING, TASK_ID_G0, TASK_ID_DEPLOYMENT,
            TASK_ID_REVIEW, TASK_ID_RETRO, TASK_ID_RETRO, TASK_ID_RESET);
    }

    protected void assertMultiInstanceTaskIsWaitingAt(String activityId) {
        List<String> activeActivityIds = runtimeService().getActiveActivityIds(job().getExecutionId());
        org.assertj.core.api.Assertions.assertThat(activeActivityIds).isNotEmpty();

        org.assertj.core.api.Assertions.assertThat(activeActivityIds.get(0))
            .isEqualTo(activityId + MULTI_INSTANCE_TASK_POSTFIX);
    }
}
