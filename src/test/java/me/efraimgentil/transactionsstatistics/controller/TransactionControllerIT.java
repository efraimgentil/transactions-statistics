package me.efraimgentil.transactionsstatistics.controller;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import me.efraimgentil.transactionsstatistics.IntegrationTest;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.*;
import java.time.temporal.TemporalField;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

public class TransactionControllerIT extends IntegrationTest{

    @Test
    public void shouldReturnCreatedIfTransactionWithinSixtySecondsAgoRange(){
        given()
                .body(new HashMap<String,Object>(){{
                    put("amount" , 10.0);
                    put("timestamp" , Instant.now().toEpochMilli() );
                }})
        .when()
            .post("/transactions")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .and().body(isEmptyBody());
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
                .post("/transactions")
         .then().assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .and().body(isEmptyBody());
    }



    public Matcher<String> isEmptyBody(){
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
