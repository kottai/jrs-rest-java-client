package com.jaspersoft.jasperserver.jaxrs.client.builder;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Map;

public class OperationResult<T> {

    private Response response;
    private Class<T> entityClass;
    private ErrorDescriptor error;

    public OperationResult(Response response, Class<T> entityClass) {
        this.response = response;
        this.entityClass = entityClass;
        if (response.getStatus() == 500 || response.getStatus() == 400)
            error = response.readEntity(ErrorDescriptor.class);
    }

    public T getEntity() {
        if (response.getStatus() == 404)
            return null;

        try {
            return response.readEntity(entityClass);
        } catch (Exception e) {
            return null;
        }
    }

    public Response getResponse() {
        return response;
    }

    public ErrorDescriptor getError() {
        return error;
    }

    public String getSessionId() {
        Map<String, NewCookie> cookies = response.getCookies();
        NewCookie jsessionid;

        if (cookies != null &&
                (jsessionid = cookies.get("JSESSIONID")) != null)
            return jsessionid.getValue();
        else
            return null;
    }

}
