package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.infra.PointHistoryTable;
import io.hhplus.tdd.point.infra.UserPointTable;
import io.hhplus.tdd.point.domain.history.PointHistory;
import io.hhplus.tdd.point.domain.history.TransactionType;
import io.hhplus.tdd.point.domain.point.UserPoint;
import io.hhplus.tdd.point.application.UserPointService;
import io.hhplus.tdd.point.infra.entity.PointHistoryEntity;
import io.hhplus.tdd.point.infra.entity.UserPointEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private UserPointService userPointService;

    /**
     * 1. 포인트 충전 기능
     * - 포인트를 충전
     * - 포인트가 존재할 경우, 추가 충전 시 금액이 합산
     * - 충전 금액이 음수면 예외 발생
     * - 충전 금액이 0이면 예외 발생
     *
     * 2. 포인트 사용 기능
     * - 포인트를 사용
     * - 잔액보다 큰 금액을 사용하면 예외 발생
     * - 음수 포인트를 사용하면 예외 발생
     * - 0 포인트를 사용하면 예외 발생
     *
     * 3. 포인트 조회 기능
     * - 보유 포인트가 있는 사용자의 포인트 조회
     * - 보유 포인트가 없는 사용자의 포인트 조회
     *
     * 4. 포인트 내역 조회 기능
     * - 포인트 내역을 조회
     * - 포인트 충전 시 포인트 내역이 생성
     * - 포인트 사용 시 포인트 내역이 생성
     */
    @Test
    void 포인트를_충전한다() {
        // given
        final long 사용자ID = 1L;
        final long 충전금액 = 1000L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPointEntity(사용자ID, 0L, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 충전금액))
            .willReturn(new UserPointEntity(사용자ID, 충전금액, System.currentTimeMillis()));

        // when
        final UserPoint 사용자_포인트 = userPointService.chargeUserPoint(사용자ID, 충전금액);

        // then
        assertThat(사용자_포인트.id()).isEqualTo(1L);
        assertThat(사용자_포인트.point()).isEqualTo(1000L);
    }

    @Test
    void 포인트가_존재할_경우_추가_충전_시_금액이_합산() {
        // given
        final long 사용자ID = 1L;
        final long 보유포인트 = 1000L;
        final long 추가_충전금액 = 2000L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 보유포인트 + 추가_충전금액))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트 + 추가_충전금액, System.currentTimeMillis()));

        // when
        final UserPoint 사용자_포인트 = userPointService.chargeUserPoint(사용자ID, 추가_충전금액);

        // then
        assertThat(사용자_포인트.id()).isEqualTo(1L);
        assertThat(사용자_포인트.point()).isEqualTo(3000L);
    }

    @Test
    void 충전_포인트가_음수면_예외가_발생() {
        // given
        final long 사용자ID = 1L;
        final long 충전금액 = -1000L;

        // when

        // then
        assertThatThrownBy(() -> userPointService.chargeUserPoint(사용자ID, 충전금액))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid amount: -1000. The amount must not be a positive number.");
    }

    @Test
    void 충전_포인트가_0이면_예외가_발생() {
        // given
        final long 사용자ID = 1L;
        final long 충전금액_0 = 0L;

        // when

        // then
        assertThatThrownBy(() -> userPointService.chargeUserPoint(사용자ID, 충전금액_0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid amount: 0. The amount must not be a positive number.");
    }

    @Test
    void 포인트를_사용한다() {
        // given
        final long 사용자ID = 1L;
        final long 보유포인트 = 1000L;
        final long 사용포인트 = 300L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 보유포인트 - 사용포인트))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트 - 사용포인트, System.currentTimeMillis()));

        // when
        final UserPoint 사용자_포인트 = userPointService.useUserPoint(사용자ID, 사용포인트);

        // then
        assertThat(사용자_포인트.id()).isEqualTo(1L);
        assertThat(사용자_포인트.point()).isEqualTo(700L);
    }

    @Test
    void 잔액보다_큰_금액을_사용하면_예외_발생() {
        // given
        final long 사용자ID = 1L;
        final long 보유포인트 = 1000L;
        final long 사용포인트 = 1300L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트, System.currentTimeMillis()));

        // when

        // then
        assertThatThrownBy(() -> userPointService.useUserPoint(사용자ID, 사용포인트))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Insufficient points. Tried to use 1300 points, but only 1000 points are available.");
    }

    @Test
    void 음수_포인트를_사용하면_예외_발생() {
        // given
        final long 사용자ID = 1L;
        final long 사용포인트 = -1300L;

        // when

        // then
        assertThatThrownBy(() -> userPointService.useUserPoint(사용자ID, 사용포인트))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid amount: -1300. The amount must not be a positive number.");
    }

    @Test
    void 포인트를_0만큼_사용하면_예외_발생() {
        // given
        final long 사용자ID = 1L;
        final long 사용포인트 = 0L;

        // when

        // then
        assertThatThrownBy(() -> userPointService.useUserPoint(사용자ID, 사용포인트))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid amount: 0. The amount must not be a positive number.");
    }

    @Test
    void 보유_포인트가_있는_사용자의_포인트_조회() {
        // given
        final long 사용자ID = 1L;
        final long 보유포인트 = 3000L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트, System.currentTimeMillis()));

        // when
        final UserPoint 사용자_포인트 = userPointService.getUserPoint(사용자ID);

        // then
        assertThat(사용자_포인트.id()).isEqualTo(1L);
        assertThat(사용자_포인트.point()).isEqualTo(3000L);
    }

    @Test
    void 포인트_내역을_조회한다() {
        // given
        final long 사용자ID = 1L;
        given(pointHistoryTable.selectAllByUserId(사용자ID))
            .willReturn(List.of(new PointHistoryEntity(
                1L,
                1L,
                10000L,
                TransactionType.CHARGE,
                System.currentTimeMillis()
            )));

        // when
        final List<PointHistory> histories = userPointService.getUserPointHistories(사용자ID);

        // then
        assertThat(histories.size()).isEqualTo(1);
        assertThat(histories.get(0).userId()).isEqualTo(1L);
        assertThat(histories.get(0).amount()).isEqualTo(10000L);
        assertThat(histories.get(0).type()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    void 포인트_충전_시_포인트_내역이_생성된다() {
        // given
        final long 사용자ID = 1L;
        final long 충전포인트 = 1000L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(UserPointEntity.empty(사용자ID));
        given(userPointTable.insertOrUpdate(사용자ID, 충전포인트))
            .willReturn(new UserPointEntity(사용자ID, 충전포인트, System.currentTimeMillis()));

        // when
        userPointService.chargeUserPoint(사용자ID, 충전포인트);

        // then
        ArgumentCaptor<Long> 사용자ID_캡처 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> 충전포인트_캡처 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<TransactionType> 타입_캡처 = ArgumentCaptor.forClass(TransactionType.class);
        ArgumentCaptor<Long> 충전시간_캡처 = ArgumentCaptor.forClass(Long.class);

        verify(pointHistoryTable).insert(사용자ID_캡처.capture(), 충전포인트_캡처.capture(), 타입_캡처.capture(), 충전시간_캡처.capture());

        assertThat(사용자ID_캡처.getValue()).isEqualTo(1L);
        assertThat(충전포인트_캡처.getValue()).isEqualTo(1000L);
        assertThat(타입_캡처.getValue()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    void 포인트_사용_시_포인트_내역이_생성된다() {
        // given
        final long 사용자ID = 1L;
        final long 보유포인트 = 6000L;
        final long 사용포인트 = 4000L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 보유포인트 - 사용포인트))
            .willReturn(new UserPointEntity(사용자ID, 보유포인트 - 사용포인트, System.currentTimeMillis()));

        // when
        userPointService.useUserPoint(사용자ID, 사용포인트);

        // then
        ArgumentCaptor<Long> 사용자ID_캡처 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> 사용포인트_캡처 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<TransactionType> 타입_캡처 = ArgumentCaptor.forClass(TransactionType.class);
        ArgumentCaptor<Long> 사용시간_캡처 = ArgumentCaptor.forClass(Long.class);

        verify(pointHistoryTable).insert(사용자ID_캡처.capture(), 사용포인트_캡처.capture(), 타입_캡처.capture(), 사용시간_캡처.capture());

        assertThat(사용자ID_캡처.getValue()).isEqualTo(1L);
        assertThat(사용포인트_캡처.getValue()).isEqualTo(2000L);
        assertThat(타입_캡처.getValue()).isEqualTo(TransactionType.USE);
    }

}
