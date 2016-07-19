package org.tiogasolutions.identity.engine.resources;

import org.tiogasolutions.app.standard.execution.ExecutionManager;
import org.tiogasolutions.identity.kernel.IdentityKernel;

import javax.ws.rs.core.SecurityContext;

public class ResourceSupport {

    protected final ExecutionManager<IdentityKernel> executionManager;

    public ResourceSupport(ExecutionManager<IdentityKernel> executionManager) {
        this.executionManager = executionManager;
    }

    public final IdentityKernel getKernel() {
        return executionManager.getContext().getDomain();
    }

    public final SecurityContext getSecurityContext() {
        return executionManager.getContext().getSecurityContext();
    }
}
