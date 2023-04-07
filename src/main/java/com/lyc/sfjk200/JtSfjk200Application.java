package com.lyc.sfjk200;

import cn.allbs.influx.annotation.EnableAllbsInflux;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
@EnableAllbsInflux
public class JtSfjk200Application {

    public static void main(String[] args) {
        SpringApplication.run(JtSfjk200Application.class, args);
    }

}
