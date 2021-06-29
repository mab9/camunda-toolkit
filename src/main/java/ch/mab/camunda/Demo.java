package ch.mab.camunda;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableProcessApplication
@SpringBootApplication
@EnableScheduling
public class Demo {

    public static void main(String... args) {
        SpringApplication.run(Demo.class, args);
    }
}
