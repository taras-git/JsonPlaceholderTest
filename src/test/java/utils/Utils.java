package utils;

import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {

    public static final URIBuilder HTTPS = new URIBuilder().setScheme("https");
    public static final String USER_DIR = System.getProperty("user.dir");

    public static URIBuilder setHost() throws IOException {
        URIBuilder builder = HTTPS
                .setHost(getAppProperty("HOST"));
        return builder;
    }

    public static URIBuilder getPostsUrl() throws IOException {
        URIBuilder builder = setHost()
                .setPath(getAppProperty("POSTS"));
        return builder;
    }

    public static URIBuilder getUsersUrl() throws IOException {
        URIBuilder builder = setHost()
                .setPath(getAppProperty("USERS"));
        return builder;
    }

    public static URIBuilder getCommentsUrl() throws IOException {
        URIBuilder builder = setHost()
                .setPath(getAppProperty("COMMENTS"));
        return builder;
    }

    public static File getUserSchema() throws IOException {
        return getSchema("/user_schema.json");
    }

    public static File getPostSchema() throws IOException {
        return getSchema("/post_schema.json");
    }

    public static File getCommentSchema() throws IOException {
        return getSchema("/comment_schema.json");
    }

    private static File getSchema(String schemaJson) throws IOException {
        File schema = new File(USER_DIR + getAppProperty("JSON_SCHEMA_FOLDER") + schemaJson);
        return schema;
    }

    public static String getTestProperty(String propertyName) throws IOException {
        return getProperty(propertyName, "test.properties");
    }

    private static String getAppProperty(String propertyName) throws IOException {
        return getProperty(propertyName, "app.properties");
    }

    private static String getProperty(String propertyName, String propertyFile) throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String testConfig = rootPath + propertyFile;
        Properties testProps = new Properties();
        testProps.load(new FileInputStream(testConfig));
        String property = testProps.getProperty(propertyName);
        return property;
    }
}
