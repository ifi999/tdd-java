package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.TransactionType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PointControllerTest {

    @Test
    void 포인트_충전() {
        // given
        final long 사용자_ID = 1L;
        final long 충전금액 = 10000L;

        // when
        final JsonPath 포인트_충전_응답 =
            given()
                .log().all()
                .body(충전금액)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .patch("/point/{id}/charge", 사용자_ID)
            .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
            .extract()
                .jsonPath();

        // then
        final long 충전된_포인트_ID = 포인트_충전_응답.getLong("id");
        final long 충전된_금액 = 포인트_충전_응답.getLong("point");

        assertThat(충전된_포인트_ID).isEqualTo(1L);
        assertThat(충전된_금액).isEqualTo(10000L);
    }

    @Test
    void 포인트_사용() {
        // given
        final long 사용자_ID = 2L;
        final long 충전금액 = 10000L;
        final long 사용금액 = 1000L;

        given()
            .log().all()
            .body(충전금액)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .patch("/point/{id}/charge", 사용자_ID)
        .then()
            .statusCode(HttpStatus.OK.value())
            .log().all()
        .extract()
            .jsonPath();

        // when
        final JsonPath 포인트_사용_응답 =
            given()
                .log().all()
                .body(사용금액)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
            .patch("/point/{id}/use", 사용자_ID)
                .then()
            .statusCode(HttpStatus.OK.value())
                .log().all()
            .extract()
                .jsonPath();

        // then
        final long 사용된_포인트_ID = 포인트_사용_응답.getLong("id");
        final long 사용된_금액 = 포인트_사용_응답.getLong("point");

        assertThat(사용된_포인트_ID).isEqualTo(2L);
        assertThat(사용된_금액).isEqualTo(9000L);
    }

    @Test
    void 포인트_조회() {
        // given
        final long 사용자_ID = 3L;
        final long 충전금액 = 15000L;

        given()
            .log().all()
            .body(충전금액)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .patch("/point/{id}/charge", 사용자_ID)
        .then()
            .statusCode(HttpStatus.OK.value())
            .log().all()
        .extract()
            .jsonPath();

        // when
        final JsonPath 포인트_조회_응답 =
            given()
                .log().all()
            .when()
                .get("/point/{id}", 사용자_ID)
            .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
            .extract()
                .jsonPath();

        // then
        final long 조회된_포인트_ID = 포인트_조회_응답.getLong("id");
        final long 조회된_금액 = 포인트_조회_응답.getLong("point");

        assertThat(조회된_포인트_ID).isEqualTo(3L);
        assertThat(조회된_금액).isEqualTo(15000L);
    }

    @Test
    void 포인트_내역_조회() {
        // given
        final long 사용자_ID = 4L;
        final long 충전금액 = 5000L;

        given()
            .log().all()
            .body(충전금액)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .patch("/point/{id}/charge", 사용자_ID)
        .then()
            .statusCode(HttpStatus.OK.value())
            .log().all()
        .extract()
            .jsonPath();

        // when
        final JsonPath 포인트_내역_조회_응답 =
            given()
                .log().all()
            .when()
                .get("/point/{id}/histories", 사용자_ID)
            .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
            .extract()
                .jsonPath();

        // then
        int 포인트_내역_목록_크기 = 포인트_내역_조회_응답.getList("$").size();
        final long 포인트_내역_사용자ID = 포인트_내역_조회_응답.getLong("[0].userId");
        final long 포인트_내역_포인트 = 포인트_내역_조회_응답.getLong("[0].amount");
        final String 포인트_내역_타입 = 포인트_내역_조회_응답.getString("[0].type");

        assertThat(포인트_내역_목록_크기).isEqualTo(1);
        assertThat(포인트_내역_사용자ID).isEqualTo(4L);
        assertThat(포인트_내역_포인트).isEqualTo(5000L);
        assertThat(포인트_내역_타입).isEqualTo(TransactionType.CHARGE.toString());
    }

}
