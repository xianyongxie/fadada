package com.gxs.fadada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:fadada-config.properties")
public class FadadaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FadadaApplication.class, args);
    }

}
