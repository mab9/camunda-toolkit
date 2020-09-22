package ch.mab.camunda;

import java.lang.reflect.Method;
import javax.sql.DataSource;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.HistoryServiceImpl;
import org.camunda.bpm.engine.impl.RepositoryServiceImpl;
import org.camunda.bpm.engine.impl.RuntimeServiceImpl;
import org.camunda.bpm.engine.impl.TaskServiceImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.spring.SpringProcessWithCoverageEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Adapted from: https://github.com/camunda/camunda-bpm-platform/blob/master/engine-spring/src/test/java/org/camunda/bpm/engine/spring/test/configuration/InMemProcessEngineConfiguration.java
 */
@TestConfiguration
public class InMemProcessEngineConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public HistoryService historyService() {
        return new HistoryServiceImpl();
    }

    @Bean
    public RepositoryService repositoryService() {
        return new RepositoryServiceImpl();
    }

    @Bean
    public TaskService taskService() {
        return new TaskServiceImpl();
    }

    @Bean
    public RuntimeService runtimeService() {
        return new RuntimeServiceImpl();
    }

    @Bean
    public ProcessEngineConfigurationImpl processEngineConfiguration() throws Exception {

        SpringProcessWithCoverageEngineConfiguration config = new SpringProcessWithCoverageEngineConfiguration();

        try {
            Method setApplicationContext = SpringProcessEngineConfiguration.class.getDeclaredMethod("setApplicationContext", ApplicationContext.class);
            setApplicationContext.invoke(config, applicationContext);
        } catch (NoSuchMethodException e) {
            // expected for Camunda < 7.8.0
        }
        config.setExpressionManager(expressionManager());
        config.setTransactionManager(transactionManager());
        config.setDataSource(dataSource());
        config.setDatabaseSchemaUpdate("true");
        config.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
        config.setHistoryService(historyService());
        config.setJobExecutorActivate(false);
        config.setTaskService(taskService());
        config.setRuntimeService(runtimeService());
        config.setRepositoryService(repositoryService());
        config.init();
        return config;
    }

    @Bean
    ExpressionManager expressionManager() {
        return new SpringExpressionManager(applicationContext, null);
    }

    @Bean
    public ProcessEngineFactoryBean processEngine() throws Exception {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

}