package com.sodo.xmarketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {"com.sodo.xmarketing"})
@EnableMongoRepositories({"com.sodo.xmarketing.repository"})
@EnableAutoConfiguration(exclude = {})
@EnableAsync
public class XmarketingApplication {

  public static void main(String[] args) {
    SpringApplication.run(XmarketingApplication.class, args);
  }
}
