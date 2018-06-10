# Transactions Statistics 

We would like to have a restful API for our statistics. The main use case for our API is to
calculate realtime statistic from the last 60 seconds. There will be two APIs, one of them is
called every time a transaction is made. It is also the sole input of this rest API. The other one
returns the statistic based of the transactions of the last 60 seconds.


### Running the project

```
$mvn spring-boot:run 
```

### Configuration

In the `application.properties` you can set how long the transaction 
can be kept in th statistics

> For the integration tests the amount is reduced to '10' seconds

```properties
statistics.rangeInSeconds=60
```

### Tests

You can run the unit test suit using
```
mvn test
```
You can run the unit test plus integration test suit using
```
mvn integration-test
```
Or simple to also run the tests and build the package
```
mvn clean install 
```

> If you are running on linux you can keep track of the statistics in real time using the following
command `while true; do curl http://localhost:8080/statistics | json_pp --json_opt=canonical,pretty && sleep 0.5 && clear; done`
just be sure to have the `json_pp` and `curl` available' 


### Tech stack
 
- Java 8
- Spring Boot
- JSR303/Hibernate Validator
- RestAssured (For Integration Tests) 
- Maven