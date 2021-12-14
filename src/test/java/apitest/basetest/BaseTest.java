package apitest.basetest;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.logging.Logger;


public class BaseTest {
    private static final Logger LOG = Logger.getLogger( BaseTest.class.getName() );

    @BeforeTest
    public void setUp() {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
    }

    @AfterTest
    public void tearDown() {
        LOG.info("> Running " + new Throwable().getStackTrace()[0].getMethodName());
    }

}
