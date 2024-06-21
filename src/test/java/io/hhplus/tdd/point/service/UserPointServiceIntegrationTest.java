package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.application.UserPointService;
import io.hhplus.tdd.point.domain.history.PointHistory;
import io.hhplus.tdd.point.domain.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserPointServiceIntegrationTest {

    @Autowired
    private UserPointService userPointService;

    @Test
    void 포인트_충전과_사용_요청은_순차적으로_실행된다() throws Exception {
        final long 사용자ID = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(4);

        userPointService.chargeUserPoint(사용자ID, 10000L);

        Future<UserPoint> future1 = executorService.submit(() -> {
            try {
                return userPointService.chargeUserPoint(사용자ID, 1000L);
            } finally {
                latch.countDown();
            }
        });

        Future<UserPoint> future2 = executorService.submit(() -> {
            try {
                return userPointService.useUserPoint(사용자ID, 500L);
            } finally {
                latch.countDown();
            }
        });

        Future<UserPoint> future3 = executorService.submit(() -> {
            try {
                return userPointService.chargeUserPoint(사용자ID, 2000L);
            } finally {
                latch.countDown();
            }
        });

        Future<UserPoint> future4 = executorService.submit(() -> {
            try {
                return userPointService.useUserPoint(사용자ID, 1000L);
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);

        UserPoint finalPoint1 = future1.get();
        UserPoint finalPoint2 = future2.get();
        UserPoint finalPoint3 = future3.get();
        UserPoint finalPoint4 = future4.get();

        // then
        assertThat(finalPoint1.point()).isEqualTo(11000L);
        assertThat(finalPoint2.point()).isEqualTo(10500L);
        assertThat(finalPoint3.point()).isEqualTo(12500L);
        assertThat(finalPoint4.point()).isEqualTo(11500L);

        List<PointHistory> 포인트_내역_목록 = userPointService.getUserPointHistories(사용자ID);

        assertThat(포인트_내역_목록.size()).isEqualTo(5);
        assertThat(포인트_내역_목록.get(0).amount()).isEqualTo(10000L);
        assertThat(포인트_내역_목록.get(1).amount()).isEqualTo(11000L);
        assertThat(포인트_내역_목록.get(2).amount()).isEqualTo(10500L);
        assertThat(포인트_내역_목록.get(3).amount()).isEqualTo(12500L);
        assertThat(포인트_내역_목록.get(4).amount()).isEqualTo(11500L);

        executorService.shutdown();
    }

}
