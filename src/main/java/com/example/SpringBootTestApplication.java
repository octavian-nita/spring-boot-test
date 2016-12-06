package com.example;

import com.example.repository.impl.BaseRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class SpringBootTestApplication {
    private static final Logger log = LoggerFactory.getLogger(SpringBootTestApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootTestApplication.class, args);
        System.out.println(context.getEnvironment().getProperty("spring.config.name"));
    }
}
