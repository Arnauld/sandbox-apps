package org.technbolts.sandboxgateway.infra.vault;

public interface Secrets {
    Object get(String key);

    default String username() {
        return (String) get("username");
    }

    default String password() {
        return (String) get("password");
    }
}
