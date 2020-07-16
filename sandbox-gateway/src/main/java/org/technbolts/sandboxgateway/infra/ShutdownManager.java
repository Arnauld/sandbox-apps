package org.technbolts.sandboxgateway.infra;

public interface ShutdownManager {
    void initiateShutdown(String reason);
}
