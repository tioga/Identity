package org.tiogasolutions.identity.engine.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.identity.client.core.PubItem;
import org.tiogasolutions.identity.client.core.PubStatus;
import org.tiogasolutions.lib.jaxrs.providers.TiogaJaxRsExceptionMapper;
import org.tiogasolutions.notify.notifier.Notifier;

import javax.ws.rs.core.Response;

public class IdentityExceptionMapper extends TiogaJaxRsExceptionMapper {

    private final Notifier notifier;

    @Autowired
    public IdentityExceptionMapper(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    protected void log5xxException(String msg, Throwable throwable) {
        super.log5xxException(msg, throwable);

        notifier.begin()
                .summary(msg)
                .exception(throwable)
                .topic("Unhandled")
                //.trait("http-status", unknown)
                .send();
    }

    protected Response createResponse(int code, Throwable ex) {
        String msg = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getName();
        PubItem item = new PubItem(new PubStatus(code, msg));
        return Response.status(code).entity(item).build();
    }
}
