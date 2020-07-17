package org.technbolts.sandboxgateway.infra.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseErrorEvent;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

@Service
@ConditionalOnExpression("${vault.enabled}")
public class VaultSecretsProvider implements SecretsProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaultSecretsProvider.class);

    private final SecretLeaseContainer secretLeaseContainer;

    public VaultSecretsProvider(SecretLeaseContainer secretLeaseContainer) {
        this.secretLeaseContainer = secretLeaseContainer;
    }

    public Secrets renewableSecrets(String path) {
        AtomicReference<VaultSecrets> leaseBox = new AtomicReference<>();
        RequestedSecret secret = RequestedSecret.renewable(path);
        secretLeaseContainer.addLeaseListener(leaseEvent -> {
            if (!secret.equals(leaseEvent.getSource())) {
                return;
            }

            Lease lease = leaseEvent.getLease();
            if (leaseEvent instanceof SecretLeaseCreatedEvent) {
                SecretLeaseCreatedEvent event = (SecretLeaseCreatedEvent) leaseEvent;
                VaultSecrets secrets = new VaultSecrets(lease, event.getSecrets());
                LOGGER.info("Lease ({}) created {} for {}s (username: {})",
                        path,
                        leaseId(lease),
                        leaseDuration(lease),
                        secrets.username());
                leaseBox.set(secrets);
            } else if (leaseEvent instanceof SecretLeaseErrorEvent) {
                LOGGER.warn("Lease ({}) expired {} for {}s ({})",
                        path,
                        leaseId(lease),
                        leaseDuration(lease),
                        leaseEvent.getClass().getSimpleName());
            } else {
                LOGGER.debug("Lease ({}) updated {} for {}s ({})",
                        path,
                        leaseId(lease),
                        leaseDuration(lease),
                        leaseEvent.getClass().getSimpleName());
            }
        });
        secretLeaseContainer.addRequestedSecret(secret);
        return leaseBox.get();
    }

    private static Duration leaseDuration(@Nullable Lease lease) {
        return lease != null ? lease.getLeaseDuration() : null;
    }

    private static String leaseId(@Nullable Lease lease) {
        return lease != null ? lease.getLeaseId() : null;
    }
}
