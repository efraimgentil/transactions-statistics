package me.efraimgentil.transactionsstatistics.controller;

import me.efraimgentil.transactionsstatistics.IntegrationTest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionControllerIT extends IntegrationTest{

    public static final String TRANSACTIONS_ENDPOINT = "/transactions";

    @Test
    public void shouldReturnCreatedIfTransactionWithinSixtySecondsAgoRange(){
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String,Object>(){{
                    put("amount" , 10.0);
                    put("timestamp" , Instant.now().toEpochMilli() );
                }})
        .when()
            .post(TRANSACTIONS_ENDPOINT)
        .then()
                .assertThat()
                .body(isEmptyBody())
                .and().statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void shouldReturnNoContentIfTransactionIsOlderThanSixtySecondsAgoRange(){
         given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String, Object>() {{
                    put("amount", 10.0);
                    put("timestamp", Instant.now().minusSeconds(60).toEpochMilli());
                }})
         .when()
                .post(TRANSACTIONS_ENDPOINT)
         .then().assertThat()
                .body(isEmptyBody())
                .and().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void shouldReturnBadRequestIfTimestampIsMissing() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String, Object>() {{
                    put("amount", 10.0);
                }})
        .when()
                .post(TRANSACTIONS_ENDPOINT)
        .then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors.size()", is(1))
                .body("errors[0].field", is("timestamp"))
                .body("errors[0].violation", is("must not be null"));
    }

    @Test
    public void shouldReturnBadRequestIfAmountIsMissing() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String, Object>() {{
                    put("timestamp", Instant.now().toEpochMilli() );
                }})
        .when()
                .post(TRANSACTIONS_ENDPOINT)
        .then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors.size()", is(1))
                .body("errors[0].field", is("amount"))
                .body("errors[0].violation", is("must not be null"));
    }

    @Test
    public void shouldReturnBadRequestIfAmountIsZeroOrLestThanZero() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String, Object>() {{
                    put("amount", 0.0);
                    put("timestamp", Instant.now().toEpochMilli() );
                }})
        .when()
                .post(TRANSACTIONS_ENDPOINT)
        .then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors.size()", is(1))
                .body("errors[0].field", is("amount"))
                .body("errors[0].violation", is("must be greater than 0"));

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String, Object>() {{
                    put("amount", -999.0);
                    put("timestamp", Instant.now().toEpochMilli() );
                }})
        .when()
                .post(TRANSACTIONS_ENDPOINT)
        .then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors.size()", is(1))
                .body("errors[0].field", is("amount"))
                .body("errors[0].violation", is("must be greater than 0"));
    }

    @Test
    public void shouldReturnBadRequestIfTimestampIsInTheFuture() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new HashMap<String, Object>() {{
                    put("amount", 1.0);
                    put("timestamp", Instant.now().plusSeconds(1).toEpochMilli());
                }})
        .when()
                .post(TRANSACTIONS_ENDPOINT)
        .then().assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errors.size()", is(1))
                .body("errors[0].field", is("timestamp"))
                .body("errors[0].violation", is("timestamp in the future"));
    }

        private Matcher<String> isEmptyBody(){
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return o.toString().length() == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Body should be empty");
            }

        };
    }

}
