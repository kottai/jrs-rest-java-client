package com.jaspersoft.jasperserver.jaxrs.client.rest_services.demo;

import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import com.jaspersoft.jasperserver.dto.authority.ClientUser;
import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
import com.jaspersoft.jasperserver.jaxrs.client.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.ResponseStatus;
import com.jaspersoft.jasperserver.jaxrs.client.builder.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.builder.permissions.PermissionMask;
import com.jaspersoft.jasperserver.jaxrs.client.builder.permissions.PermissionRecipient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

public class AuthorityDemoTest extends Assert {

    private static ClientUser user;

    @BeforeClass
    public static void setUp() {
        user = new ClientUser()
                .setUsername("john.doe")
                .setPassword("12345678")
                .setEmailAddress("john.doe@email.net")
                .setEnabled(true)
                .setExternallyDefined(false)
                .setFullName("John Doe");
    }

    @AfterClass
    public static void tearDown() {
        JasperserverRestClient
                .authenticate("jasperadmin", "jasperadmin")
                .usersService()
                .username(user.getUsername())
                .delete();
    }

    @Test(enabled = false)
    public void testSomething() {

        ClientUser user = new ClientUser()
                .setUsername("john.doe")
                .setPassword("12345678")
                .setEmailAddress("john.doe@email.net")
                .setEnabled(true)
                .setExternallyDefined(false)
                .setFullName("John Doe");

        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .createOrUpdate(user);

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.CREATED);

        OperationResult<ClientUser> result1 =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .get();

        ClientUser requestedUser = result1.getEntity();
        assertNotEquals(requestedUser, null);
        assertEquals(requestedUser.getUsername(), user.getUsername());

        OperationResult result2 =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .delete();

        Response result2Response = result2.getResponse();
        assertEquals(result2Response.getStatus(), ResponseStatus.NO_CONTENT);

        OperationResult<ClientUser> result3 =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .get();

        requestedUser = result3.getEntity();
        assertEquals(requestedUser, null);
    }

    @Test
    public void testCreateUser() {
        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .createOrUpdate(user);

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.CREATED);
    }

    @Test(dependsOnMethods = "testCreateUser", enabled = true)
    public void testUpdateUserToAdminRole() {

        ClientRole role =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .rolesService()
                        .rolename("ROLE_ADMINISTRATOR")
                        .get()
                        .getEntity();

        assertNotEquals(role, null);

        Set<ClientRole> roles = new HashSet<ClientRole>();
        roles.add(role);
        user.setRoleSet(roles);

        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .createOrUpdate(user);

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.UPDATED);
    }

    @Test(dependsOnMethods = "testCreateUser")
    public void testAddUserAttribute() {

        ClientUserAttribute attribute = new ClientUserAttribute()
                .setName("someAttribute")
                .setValue("hello");

        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .attribute(attribute.getName())
                        .createOrUpdate(attribute);

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.CREATED);
    }

    @Test(dependsOnMethods = "testCreateUser")
    public void testAddPermissionForUser() {

        RepositoryPermission permission = new RepositoryPermission()
                .setRecipient("user:/john.doe")
                .setUri("/")
                .setMask(PermissionMask.READ_ONLY);

        OperationResult result = JasperserverRestClient
                .authenticate("jasperadmin", "jasperadmin")
                .permissionsService()
                .create(permission);

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.CREATED);
    }

    @Test(dependsOnMethods = {"testAddPermissionForUser", "testUpdateUserToAdminRole"}, enabled = true)
    public void testLoginAsNewUserAndGetPermission() {

        OperationResult<RepositoryPermission> result =
                JasperserverRestClient
                        .authenticate("john.doe", "12345678")
                        .permissionsService()
                        .resource("/")
                        .permissionRecipient(PermissionRecipient.USER, "john.doe")
                        .get();

        RepositoryPermission permission = result.getEntity();
        assertEquals(permission.getMask().intValue(), PermissionMask.READ_ONLY);
    }

    @Test(dependsOnMethods = {"testAddUserAttribute"})
    public void testDeleteUserAttribute() {
        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .attribute("someAttribute")
                        .delete();

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.NO_CONTENT);

    }

    @Test(dependsOnMethods = {"testLoginAsNewUserAndGetPermission"})
    public void testDeleteUserPermission() {
        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .permissionsService()
                        .resource("/")
                        .permissionRecipient(PermissionRecipient.USER, "john.doe")
                        .delete();

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.NO_CONTENT);

    }

    @Test(dependsOnMethods = {"testAddPermissionForUser", "testAddUserAttribute",
            "testDeleteUserPermission", "testDeleteUserAttribute"})
    public void testDeleteUser() {
        OperationResult result =
                JasperserverRestClient
                        .authenticate("jasperadmin", "jasperadmin")
                        .usersService()
                        .username(user.getUsername())
                        .delete();

        Response response = result.getResponse();
        assertEquals(response.getStatus(), ResponseStatus.NO_CONTENT);

    }

}
