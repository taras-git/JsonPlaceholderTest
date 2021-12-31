package apitest.smoketest;

import apitest.basetest.BaseTest;
import model.Root;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static utils.Utils.getPostsUrl;
import static utils.Utils.getUsersUrl;


public class SmokeGetTest extends BaseTest {
    private static final Logger LOG = Logger.getLogger( SmokeGetTest.class.getName() );

    @Test
    public void givenGetUsers_thenResponse_200()
            throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        String url = getUsersUrl().toString();
        // Given
        HttpUriRequest request = new HttpGet( url );
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
        // Then
        assertThat(
                httpResponse
                        .getStatusLine()
                        .getStatusCode(),
                equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void givenGetPosts_thenResponse_200()
            throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        String url = getPostsUrl().toString();
        // Given
        HttpUriRequest request = new HttpGet( url );
        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
        // Then
        assertThat(
                httpResponse
                        .getStatusLine()
                        .getStatusCode(),
                equalTo(HttpStatus.SC_OK));
    }


    @Test
    public void truconnectTest() {
        String url = "https://staging.truconnect.fit/api/config";
        Root root = getRoot(url);

        LOG.info("ios: " + root.getData().getIosMinVersion());
        LOG.info("android: " + root.getData().getAndroidMinVersion());
    }


    private Root getRoot(String url) {
        Root root = given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .as(Root.class);
        return root;
    }

}
