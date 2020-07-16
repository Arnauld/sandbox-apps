package org.technbolts.sandboxgateway.infra.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.RestOperationsCallback;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.client.HttpStatusCodeException;
import org.technbolts.sandboxgateway.infra.ShutdownManager;


public class CustomVaultTemplate extends VaultTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaultConfiguration.class);

    private final ShutdownManager shutdownManager;

    public CustomVaultTemplate(VaultEndpointProvider endpointProvider,
                               ClientHttpRequestFactory clientHttpRequestFactory,
                               SessionManager sessionManager,
                               ShutdownManager shutdownManager) {
        super(endpointProvider, clientHttpRequestFactory, sessionManager);
        this.shutdownManager = shutdownManager;
    }

    @Override
    public VaultResponse read(String path) {
        Assert.hasText(path, "Path cannot be empty");
        return readResponse(path);
    }

    private VaultResponse readResponse(final String path) {
        return doWithSession(restOperations -> {
            try {
                return restOperations.getForObject(path, VaultResponse.class);
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                throw VaultResponses.buildException(e, path);
            }
        });
    }

    public <T> T doWithSession(RestOperationsCallback<T> sessionCallback) {
        return super.doWithSession(restOperations -> {
            try {
                return sessionCallback.doWithRestOperations(restOperations);
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                    LOGGER.error("HTTP 403 encountered when calling Vault - Vault token is invalid (probably expired).");
                    shutdownManager.initiateShutdown("Vault");
                }
                throw e;
            }
        });
    }
}