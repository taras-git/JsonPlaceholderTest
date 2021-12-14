package apitest.smoketest;

import apitest.basetest.BaseTest;
import io.restassured.http.ContentType;
import model.Post;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static utils.Utils.getUsersUrl;
import static utils.Utils.setHost;


public class SmokeCRUDTest extends BaseTest {

    public static final String TITLE = "title";
    private static final Logger LOG = Logger.getLogger( SmokeCRUDTest.class.getName() );


    @Test
    public void givenCreateUser_thenResponse_201()
            throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        String url = getUsersUrl().toString();

        // get users count
        int usersLength = given()
                .when()
                .get(url)
                .then()
                .statusCode( HttpStatus.SC_OK )
                .extract()
                .jsonPath()
                .getList("$")
                .size();

        LOG.info("users: " + usersLength);

        // create new empty user
        // technically this POST call should not be allowed as all fields are sent as "null", but is OK for our testing purposes
        int createdUserId = given()
                .when()
                .contentType(ContentType.JSON)
                .post(url)
                .then()
                .statusCode( HttpStatus.SC_CREATED )
                .extract()
                .path("id");

        LOG.info("createdUserId: {}" + createdUserId);

        // created user's id should be auto-incremented
        Assert.assertEquals(usersLength + 1, createdUserId);
    }


    @Test
    public void givenPatchPosts_thenResponse_200()
            throws IOException, URISyntaxException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
        URL url = setHost().setPath("/posts/1").build().toURL();

        // get title of 1st post
        Post firstPost = given()
                .when()
                .get(url)
                .then()
                .statusCode( HttpStatus.SC_OK )
                .extract()
                .as(Post.class);

        LOG.info("postTitle before: " + firstPost.getTitle());

        // change the title of the 1st post
        String newTitle = "newShinyTitle";
        JSONObject newTitleJson = new JSONObject()
                .put( TITLE, newTitle );

        String newTitleCreated = given()
                .when()
                .contentType(ContentType.JSON)
                .body(newTitleJson.toString())
                .patch(url)
                .then()
                .statusCode( HttpStatus.SC_OK )
                .extract()
                .path(TITLE);

        LOG.info("postTitle after: " + newTitleCreated);

        Assert.assertEquals(newTitleCreated, newTitle);
    }

    @Test
    public void givenDeletePosts_thenResponse_200()
            throws IOException, URISyntaxException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
        URL url = setHost().setPath("/posts/1").build().toURL();

        given()
                .when()
                .contentType(ContentType.JSON)
                .delete(url)
                .then()
                .statusCode( HttpStatus.SC_OK )
                .assertThat()
                .body("size()", is(0));
    }
}
