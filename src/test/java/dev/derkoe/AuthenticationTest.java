package dev.derkoe;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class AuthenticationTest {

    @TestHTTPEndpoint(FrontendApiResource.class)
    @TestHTTPResource("hello")
    URL frontendUrl;

    @TestHTTPEndpoint(FrontendApiResource.class)
    @TestHTTPResource("hello")
    URL backendUrl;

    @Test
    void testBackendWithAuth() {
        given()
                .redirects().follow(false)
                .auth().basic("admin", "password")
                .when().get(backendUrl)
                .then()
                .statusCode(200)
                .body(containsString("Backend API"));
    }

    @Test
    void testFrontendEndpoint() {
        given()
                .redirects().follow(false)
                .when().get(frontendUrl)
                .then()
                .statusCode(302)
                .header("Location", containsString("openid"));
    }

    @Test
    void testBackendEndpoint() {
        given()
                .redirects().follow(false)
                .when().get(backendUrl)
                .then()
                .statusCode(401)
                .header("www-authenticate", containsString("basic"));
    }
}
