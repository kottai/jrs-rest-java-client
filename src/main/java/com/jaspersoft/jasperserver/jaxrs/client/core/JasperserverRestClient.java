/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jaxrs.client.core;

import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.filters.SessionOutputFilter;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JasperserverRestClient {
    private final RestClientConfiguration configuration;

    public JasperserverRestClient(RestClientConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("You must define the configuration");
        }
        this.configuration = configuration;
    }
    public Session authenticate(String username, String password) {
        AuthenticationCredentials credentials = new AuthenticationCredentials(username, password);
        SessionStorage sessionStorage = new SessionStorage(configuration, credentials);
        login(sessionStorage);
        return new Session(sessionStorage);
    }

    public AnonymousSession getAnonymousSession() {
        return new AnonymousSession(new SessionStorage(configuration, null));
    }

    private void login(SessionStorage storage) {

        AuthenticationCredentials credentials = storage.getCredentials();
        WebTarget rootTarget = storage.getRootTarget();
        Form form = new Form();
        form.param("j_username", credentials.getUsername()).param("j_password", credentials.getPassword());

        WebTarget target = rootTarget.path("/rest/login");
        Response response = target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        String sessionId = null;
        if (response.getStatus() == ResponseStatus.OK) {
            sessionId = response.getCookies().get("JSESSIONID").getValue();
            storage.setSessionId(sessionId);
        } else {
            new DefaultErrorHandler().handleError(response);
        }
        rootTarget.register(new SessionOutputFilter(sessionId));
    }

}
