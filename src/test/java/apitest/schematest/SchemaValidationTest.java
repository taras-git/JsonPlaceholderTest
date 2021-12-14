package apitest.schematest;

import basetest.BaseTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static utils.Utils.*;


public class SchemaValidationTest extends BaseTest {
        private static final Logger LOG = Logger.getLogger( SchemaValidationTest.class.getName() );

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

}
