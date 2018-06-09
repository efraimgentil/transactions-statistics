package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.IntegrationTest;
import me.efraimgentil.transactionsstatistics.domain.Transaction;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatisticsControllerIT extends IntegrationTest{

    final String STATISTICS_ENDPOINT = "/statistics";
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

    @Test
    public void shouldReturnTheLastTransactionStatus(){
        Instant now = Instant.now();
        postTransaction(new Transaction( 10.0 , now.toEpochMilli() ));

        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(10.0) )
                .body(AVG  , is(10.0) )
                .body(MAX  , is(10.0) )
                .body(MIN  , is(10.0) )
                .body(COUNT  , is(1));
    }

    @Test
    public void shouldReturnTheStatusWithTheStatisticsUpdated(){
        Instant now = Instant.now();
        postTransaction(new Transaction( 10.0 , now.toEpochMilli() ));

        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(10.0) )
                .body(AVG  , is(10.0) )
                .body(MAX  , is(10.0) )
                .body(MIN  , is(10.0) )
                .body(COUNT  , is(1));

        postTransaction(new Transaction( 0.05 , now.minusSeconds(5).toEpochMilli() ));

        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(10.05) )
                .body(AVG  , closeTo(5.02 , DECIMAL_ERROR_RANGE ) )
                .body(MAX  , is(10.0) )
                .body(MIN  , is(0.05) )
                .body(COUNT  , is(2));
    }

    @Test
    public void shouldReturnStatisticsConsideringTheExpirationAndInsertionOfTransactions() throws InterruptedException {
        int TWO_SECOND = 2000;
        Instant now = Instant.now();
        postTransaction(new Transaction( 10.0 , now.minusSeconds(6).toEpochMilli() ));
        postTransaction(new Transaction( 1.99 , now.minusSeconds(8).toEpochMilli() ));

        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(11.99) )
                .body(AVG  , closeTo(5.99 , DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(10.00) )
                .body(MIN  , is(1.99) )
                .body(COUNT  , is(2));
        Thread.sleep(TWO_SECOND);
        postTransaction(new Transaction( 22.00 , now.toEpochMilli() ));
        postTransaction(new Transaction( 11.00 , Instant.now().minusSeconds(6).toEpochMilli() ));
        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(43.00) )
                .body(AVG  , closeTo(14.33 , DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(22.00) )
                .body(MIN  , is(10.0) )
                .body(COUNT  , is(3));
        Thread.sleep(TWO_SECOND);
        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(33.00) )
                .body(AVG  , closeTo(16.50 , DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(22.00) )
                .body(MIN  , is(11.0) )
                .body(COUNT  , is(2));

    }

    @Test
    public void shouldReturnTheStatusWithTheStatisticsUpdatedOverTimeUtilIsTotallyClean() throws InterruptedException {
        int TWO_SECOND = 2000;
        Instant now = Instant.now();
        postTransaction(new Transaction( 10.0 , now.minusSeconds(2).toEpochMilli() ));
        postTransaction(new Transaction( 5.25 , now.minusSeconds(4).toEpochMilli() ));
        postTransaction(new Transaction( 25.50, now.minusSeconds(6).toEpochMilli() ));
        postTransaction(new Transaction( 1.99 , now.minusSeconds(8).toEpochMilli() ));

        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(42.74) )
                .body(AVG  , closeTo(10.68 , DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(25.50) )
                .body(MIN  , is(1.99) )
                .body(COUNT  , is(4));
        Thread.sleep(TWO_SECOND);
        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(40.75) )
                .body(AVG  , closeTo(13.58 , DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(25.50) )
                .body(MIN  , is(5.25) )
                .body(COUNT  , is(3));
        Thread.sleep(TWO_SECOND);
        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(15.25) )
                .body(AVG  , closeTo(7.62 , DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(10.0) )
                .body(MIN  , is(5.25) )
                .body(COUNT  , is(2));
        Thread.sleep(TWO_SECOND);
        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(10.0) )
                .body(AVG  , closeTo(10.0, DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(10.0) )
                .body(MIN  , is(10.0) )
                .body(COUNT  , is(1));
        Thread.sleep(TWO_SECOND);
        when()
                .get(STATISTICS_ENDPOINT )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(SUM ,  is(0.0) )
                .body(AVG  , closeTo(0.0, DECIMAL_ERROR_RANGE) )
                .body(MAX  , is(0.0) )
                .body(MIN  , is(0.0) )
                .body(COUNT  , is(0));
    }


    private void postTransaction(Transaction transaction){
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(transaction).when().post(TransactionControllerIT.TRANSACTIONS_ENDPOINT).then();
    }

}
