package org.technbolts.sandboxgateway.infra.vault;

public interface SecretsProvider {
    Secrets renewableSecrets(String path);
}
