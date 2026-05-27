package com.zafta.client.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableFeignClients
@PropertySources({
        @PropertySource(value = "file:config/application.yml", ignoreResourceNotFound = true),
        @PropertySource(value = "file:./config/application.yml", ignoreResourceNotFound = true),
        @PropertySource(value = "file:app/config/application.yml", ignoreResourceNotFound = true),
        @PropertySource(value = "file:./app/config/application.yml", ignoreResourceNotFound = true)
})
public class ClientAgentApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(ClientAgentApplication.class, args);
    }
}
