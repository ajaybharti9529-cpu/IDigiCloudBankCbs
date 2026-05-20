package com.idigiwave.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CbsMockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbsMockServiceApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════════════════════════╗
                ║   CBS Mock Service Started                               ║
                ║   Port   : 8081                                          ║
                ║   DB     : MySQL (cbs_mock_db)                           ║
                ║   Swagger: http://localhost:8081/cbs/swagger-ui.html     ║
                ╚══════════════════════════════════════════════════════════╝
                """);
    }
}
