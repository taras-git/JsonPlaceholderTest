package apitest.basetest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.lang.reflect.Type;
import java.util.logging.Logger;


public class BaseTest {
    private static final Logger LOG = Logger.getLogger( BaseTest.class.getName() );

    @BeforeTest
    public void setUp() {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());

        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                new Jackson2ObjectMapperFactory() {
                    @Override
                    public ObjectMapper create(Type cls, String charset) {
                        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
                        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        return om;
                    }

                }
        ));
    }

    @AfterTest
    public void tearDown() {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
    }

}
