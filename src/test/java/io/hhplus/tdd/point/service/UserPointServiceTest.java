package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private UserPointService userPointService;

    /**
     * 포인트 충전 기능
     *
     * - 포인트를 충전한다
     * - 포인트가 존재할 경우, 추가 충전 시 금액이 합산
     * - 충전 금액이 음수면 예외 발생
     * - 충전 금액이 0이면 포인트가 변하지 않음
     */
    @Test
    void 포인트를_충전한다() {
        // given
        final long 사용자ID = 1L;
        final long 충전금액 = 1000L;

        given(userPointTable.selectById(사용자ID))
                .willReturn(new UserPoint(사용자ID, 0L, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 충전금액))
            .willReturn(new UserPoint(사용자ID, 충전금액, System.currentTimeMillis()));

        // when
        final UserPoint 사용자_포인트 = userPointService.charge(사용자ID, 충전금액);

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
            .willReturn(new UserPoint(사용자ID, 보유포인트, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 보유포인트 + 추가_충전금액))
            .willReturn(new UserPoint(사용자ID, 보유포인트 + 추가_충전금액, System.currentTimeMillis()));

        // when
        final UserPoint 사용자_포인트 = userPointService.charge(사용자ID, 추가_충전금액);

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
        assertThatThrownBy(() -> userPointService.charge(사용자ID, 충전금액))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid amount.");
    }

    @Test
    void 충전_포인트가_0이면_포인트가_변하지_않음() {
        // given
        final long 사용자ID = 1L;
        final long 보유포인트 = 500L;
        final long 추가_충전금액_0 = 0L;

        given(userPointTable.selectById(사용자ID))
            .willReturn(new UserPoint(사용자ID, 보유포인트, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(사용자ID, 보유포인트 + 추가_충전금액_0))
                .willReturn(new UserPoint(사용자ID, 보유포인트, System.currentTimeMillis()));


        // when
        final UserPoint 사용자_포인트 = userPointService.charge(사용자ID, 추가_충전금액_0);

        // then
        assertThat(사용자_포인트.id()).isEqualTo(1L);
        assertThat(사용자_포인트.point()).isEqualTo(500L);
    }

}
