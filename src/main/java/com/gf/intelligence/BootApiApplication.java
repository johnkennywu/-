package com.gf.intelligence;

import com.gf.intelligence.web.WebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
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
        ConfigurableApplicationContext context = SpringApplication.run(BootApiApplication.class, args);
        WebSocket.setApplicationContext(context);
    }
}
