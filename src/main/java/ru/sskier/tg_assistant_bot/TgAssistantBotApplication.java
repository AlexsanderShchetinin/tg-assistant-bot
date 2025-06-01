package ru.sskier.tg_assistant_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = RestTemplateAutoConfiguration.class)
public class TgAssistantBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgAssistantBotApplication.class, args);
    }

}
