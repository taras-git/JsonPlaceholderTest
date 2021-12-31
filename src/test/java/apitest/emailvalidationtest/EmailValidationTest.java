package apitest.emailvalidationtest;

import apitest.basetest.BaseTest;
import model.Comment;
import model.Post;
import model.User;
import org.apache.commons.validator.routines.EmailValidator;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static utils.Utils.*;


public class EmailValidationTest extends BaseTest {
    private SoftAssertions softAssertions;
    private static final Logger LOG = Logger.getLogger( EmailValidationTest.class.getName() );

    @Test
    public void givenGetUsername_thenVerifyCommentsEmails() throws IOException {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
        Integer userId = null;
        String userName = getTestProperty("userName");

        userId = getUserId(userId, userName);

        LOG.info("Found userId for " + userName);

        // get all userName posts
        Post[] userPosts = getUserPosts(userId);

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

    private Post[] getUserPosts(Integer userId) throws IOException {
        String userPostsUrl = getPostsUrl().setParameter("userId", userId.toString()) .toString();
        Post[] userPosts = getPosts(userPostsUrl);

        // if posts are empty -> terminate the test
        if (userPosts.length == 0){
            throw new RuntimeException("User posts not found!");
        }
        return userPosts;
    }

    private Integer getUserId(Integer userId, String userName) throws IOException {
        String usersUrl = getUsersUrl().toString();
        // get all users
        User[] users = getUsers(usersUrl);

        // try to get userId of a user with a userName == "Samantha"
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
        return userId;
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
