package com.idigiwave.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CbsMockServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(CbsMockServiceApplication.class, args);

        String port    = context.getEnvironment().getProperty("local.server.port", "unknown");
        String cbsUrl  = System.getenv("RENDER_EXTERNAL_URL") != null
                ? System.getenv("RENDER_EXTERNAL_URL") + "/cbs"
                : "http://localhost:" + port + "/cbs";

        System.out.printf("""
                ╔══════════════════════════════════════════════════════════╗
                ║   CBS Mock Service Started                               ║
                ║   Internal Port : %s                                  ║
                ║   DB            : PostgreSQL (Render)                    ║
                ║   Base URL      : %s              ║
                ║   Swagger       : %s/swagger-ui.html ║
                ╚══════════════════════════════════════════════════════════╝
                %n""", port, cbsUrl, cbsUrl);
    }
}
