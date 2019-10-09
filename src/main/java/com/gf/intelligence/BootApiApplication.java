package com.gf.intelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wushubiao
 * @Title: BootApiApplication
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/8
 */

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class BootApiApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(BootApiApplication.class, args);
    }
}
