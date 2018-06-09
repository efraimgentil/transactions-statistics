package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.IntegrationTest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class StatisticsControllerIT extends IntegrationTest{

    final String STATISTICS_ENDPOINT = "/statistic";
    final String SUM = "sum";
    final String AVG = "avg";
    final String MAX = "max";
    final String MIN = "min";
    final String COUNT = "count";
    final double DECIMAL_ERROR_RANGE = 0.1;

    @Test
    public void shouldReturnCreatedIfTransactionWithinSixtySecondsAgoRange(){
        when()
            .get(STATISTICS_ENDPOINT )
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(0.0) )
                .body(AVG  , is(0.0) )
                .body(MAX  , is(0.0) )
                .body(MIN  , is(0.0) )
                .body(COUNT  , is(0));
    }




}
