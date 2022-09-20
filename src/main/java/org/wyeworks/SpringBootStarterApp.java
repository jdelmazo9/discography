package org.wyeworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@Configuration
@EnableRetry
public class SpringBootStarterApp {

    public static void main( String[] args ) {
        SpringApplication.run(SpringBootStarterApp.class);
    }

    @Bean(initMethod="startProcess")
    public DiscographyApplication discographyApplication() {
        return new DiscographyApplication();
    }
}
