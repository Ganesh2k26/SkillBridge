package com.ganesh.skillbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkillBridgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillBridgeApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════════╗
                ║   SkillBridge Platform — STARTED ✅       ║
                ║   API: http://localhost:8080              ║
                ║   Docs: http://localhost:8080/swagger-ui  ║
                ╚══════════════════════════════════════════╝
                """);
    }
}
