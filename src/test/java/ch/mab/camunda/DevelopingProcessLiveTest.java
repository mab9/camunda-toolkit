package ch.mab.camunda;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DevelopingProcessLiveTest {

    private final String KEY_DEVELOPMENT_PROCESS = "development-process";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;


    @Test
    @Deployment(resources = KEY_DEVELOPMENT_PROCESS + ".bpmn")
    public void start_process() {

        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY_DEVELOPMENT_PROCESS);
        assertThat(processInstance).isStarted();
    }


    @Test
    public void start_process_byManualDeployment() {
        org.camunda.bpm.engine.repository.Deployment deploy = repositoryService.createDeployment()
            .addClasspathResource(KEY_DEVELOPMENT_PROCESS + ".bpmn").deploy();
        // loads all deployed process definitions
        //List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(deploy.getId()).singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinition.getKey());
        assertThat(processInstance).isStarted();
    }
}
