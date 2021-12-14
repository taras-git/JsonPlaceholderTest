# JsonPlaceholder REST Api test

## Tools used:
Java 11

RestAssured 4.4.0

TestNg 7.4.0

Maven 3.6.3

## Configuration files :
Configurations are stored in  text files with key-value pairs:

    src/test/resources/app.properties  
general values (i.e hosts, endpoints ...)

    src/test/resources/test.properties
test specific values (i.e usernames ...)

## Test cases
Test cases divided into 3 categories:
### Schema validation tests
    src/test/java/apitest/schematest

### Smoke tests
    src/test/java/apitest/smoketest

### Complex tests (email validation)
    src/test/java/apitest/emailvalidationtest

To run the test there are choices:
- from IDE (IntelliJ): run the appropriate test class
- from CLI: execute command mvn install from the root folder in a terminal

