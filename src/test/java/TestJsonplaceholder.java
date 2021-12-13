import io.restassured.http.ContentType;
import model.Comment;
import model.Post;
import model.User;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static utils.Utils.*;


public class TestJsonplaceholder {

    public static final String TITLE = "title";
    private SoftAssertions softAssertions;
    private static final Logger LOG = Logger.getLogger( TestJsonplaceholder.class.getName() );

    @BeforeClass
    public void setUp() {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
    }

    @AfterClass
    public void tearDown() {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
    }


    @Test
    public void JsonUsersSchemaValidationTest() throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        File userSchema = getUserSchema();
        String usersUrl = getUsersUrl().toString();

        given()
                .get(usersUrl)
                .then()
                .body(matchesJsonSchema(userSchema));
    }

    @Test
    public void JsonPostsSchemaValidationTest() throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        File postSchema = getPostSchema();
        String postsUrl = getPostsUrl().toString();

        given()
                .get(postsUrl)
                .then()
                .body(matchesJsonSchema(postSchema));
    }

    @Test
    public void JsonCommentsSchemaValidationTest() throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        File commentSchema = getCommentSchema();
        String commentsUrl = getCommentsUrl().toString();

        given()
                .get(commentsUrl)
                .then()
                .body(matchesJsonSchema(commentSchema));
    }

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
    public void givenGetUsername_thenVerifyCommentsEmails() throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        String usersUrl = getUsersUrl().toString();
        // get all users
        User[] users = getUsers(usersUrl);

        // try to get userId of a user with a userName == "Samantha"
        Integer userId = null;
        String userName = getTestProperty("userName");
        for(User u : users) {
            if(u.getUsername().equals(userName)) {
                userId = u.getId();
                break;
            }
        }

        // if userId is not found -> terminate the test
        if (userId == null) {
            throw new RuntimeException("Can not find a userId for a user " + userName);
        }

        LOG.info("Found userId for " + userName);

        // get all userName posts
        String userPostsUrl = getPostsUrl().setParameter("userId", userId.toString()) .toString();
        Post[] userPosts = getPosts(userPostsUrl);

        // if posts are empty -> terminate the test
        if (userPosts.length == 0){
            throw new RuntimeException("User posts not found!");
        }

        LOG.info("Found user posts for " + userName);

        // extract userName posts ids
        ArrayList<Integer> postsIds = new ArrayList<>();
        for (Post p : userPosts) {
            postsIds.add(p.getId());
        }

        EmailValidator emailValidator = EmailValidator.getInstance();
        softAssertions = new SoftAssertions();

        // iterate over each post comments
        for (Integer postId : postsIds) {
            String userPostCommentsUrl = getCommentsUrl().setParameter("postId", postId.toString()) .toString();
            Comment[] postComments = getComments(userPostCommentsUrl);

            for (Comment c : postComments) {
                if (!emailValidator.isValid(c.getEmail())){
                    LOG.info("email " + c.getEmail() + " is not valid");
                }
                softAssertions.assertThat(emailValidator.isValid(c.getEmail())).isTrue();
            }
        }
        softAssertions.assertAll();
    }


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
            throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
        String url = getPostsUrl().toString() + "/1";

        // get title of 1st post
        String postOldTitle = given()
                .when()
                .get(url)
                .then()
                .statusCode( HttpStatus.SC_OK )
                .extract()
                .path(TITLE);

        LOG.info("postTitle before: " + postOldTitle);

        // change the title of the 1st post
        String newTitle = "newShinyTitle";
        JSONObject newTitleJson = new JSONObject()
                .put( TITLE, newTitle );

        String newTitleCreated = given()
                .when()
                .contentType(ContentType.JSON)
                .body(newTitleJson.toString())
                .put(url)
                .then()
                .statusCode( HttpStatus.SC_OK )
                .extract()
                .path(TITLE);

        LOG.info("postTitle after: " + newTitleCreated);

        Assert.assertEquals(newTitleCreated, newTitle);
    }


    private Comment[] getComments(String userPostCommentsUrl) {
        Comment[] postComments = given()
                .when()
                .get(userPostCommentsUrl)
                .then()
                .statusCode(200)
                .extract()
                .as(Comment[].class);
        return postComments;
    }

    private Post[] getPosts(String userPostsUrl) {
        Post[] userPosts = given()
                .when()
                .get(userPostsUrl)
                .then()
                .statusCode(200)
                .extract()
                .as(Post[].class);
        return userPosts;
    }

    private User[] getUsers(String usersUrl) {
        User[] users = given()
                .when()
                .get(usersUrl)
                .then()
                .statusCode(200)
                .extract()
                .as(User[].class);
        return users;
    }

}
