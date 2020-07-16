package org.technbolts.sandboxgateway.infra.vault;

import org.springframework.vault.core.lease.domain.Lease;

import java.util.Map;

public class VaultSecrets implements Secrets {
    private final Lease lease;
    private final Map<String, Object> secrets;

    public VaultSecrets(Lease lease, Map<String, Object> secrets) {
        this.lease = lease;
        this.secrets = secrets;
    }

    public Lease lease() {
        return lease;
    }

    @Override
    public Object get(String key) {
        return secrets.get(key);
    }
}
