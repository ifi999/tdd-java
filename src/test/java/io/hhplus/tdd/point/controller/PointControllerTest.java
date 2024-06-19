package io.hhplus.tdd.point.controller;

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
        final long 포인트_ID = 1L;
        final long 충전금액 = 10000L;

        // when
        final JsonPath 포인트_충전_응답 =
            given()
                .log().all()
                .body(충전금액)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .patch("/point/{id}/charge", 포인트_ID)
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

}
