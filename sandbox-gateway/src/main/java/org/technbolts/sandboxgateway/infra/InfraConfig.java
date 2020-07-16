package org.technbolts.sandboxgateway.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfraConfig {

    @Bean
    public ShutdownManager shutdownManager() {
        return new ShutdownManager() {
            @Override
            public void initiateShutdown(String reason) {

            }
        };
    }
}
