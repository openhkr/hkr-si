package com.reachauto.hkr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Administrator on 2017/7/12.
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(value = {"com.reachauto.hkr.si"})
public class SiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SiApplication.class, args);
    }
}
