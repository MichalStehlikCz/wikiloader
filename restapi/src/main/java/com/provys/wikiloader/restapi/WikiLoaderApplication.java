package com.provys.wikiloader.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "com.provys")
@ConfigurationPropertiesScan(basePackages = "com.provys")
@EnableSwagger2
public class WikiLoaderApplication {

        public static void main(String[] args) {
                SpringApplication.run(WikiLoaderApplication.class, args);
        }

        @Bean
        UiConfiguration uiConfig() {
                return UiConfigurationBuilder.builder()
                        .validatorUrl(null)
                        .build();
        }
}