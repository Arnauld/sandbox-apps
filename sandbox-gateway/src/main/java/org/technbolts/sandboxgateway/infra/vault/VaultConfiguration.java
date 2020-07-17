package org.technbolts.sandboxgateway.infra.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;
import org.technbolts.sandboxgateway.infra.ShutdownManager;

@Configuration
@ConditionalOnExpression("${vault.enabled}")
public class VaultConfiguration extends AbstractVaultConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaultConfiguration.class);

    private final String vaultHost;
    private final int vaultPort;
    private final String vaultScheme;
    private final String vaultToken;
    private final ShutdownManager shutdownManager;

    public VaultConfiguration(@Value("${vault.host}") String vaultHost,
                              @Value("${vault.port}") int vaultPort,
                              @Value("${vault.scheme}") String vaultScheme,
                              @Value("${vault.token}") String vaultToken,
                              ShutdownManager shutdownManager) {
        this.vaultHost = vaultHost;
        this.vaultPort = vaultPort;
        this.vaultScheme = vaultScheme;
        this.vaultToken = vaultToken;
        this.shutdownManager = shutdownManager;
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        LOGGER.info("Vault endpoint {}://{}:{}.", vaultScheme, vaultHost, vaultPort);

        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setHost(vaultHost);
        endpoint.setPort(vaultPort);
        endpoint.setScheme(vaultScheme);
        return endpoint;
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(vaultToken);
    }

    @Override
    public VaultTemplate vaultTemplate() {
        return new CustomVaultTemplate(
                vaultEndpointProvider(),
                clientHttpRequestFactoryWrapper().getClientHttpRequestFactory(),
                sessionManager(),
                shutdownManager);
    }
}