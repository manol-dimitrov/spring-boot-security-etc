package com.zamrad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zamrad"})
@EnableAutoConfiguration
@PropertySources({@PropertySource("classpath:auth0/auth0-${spring.profiles.active}.properties")})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
