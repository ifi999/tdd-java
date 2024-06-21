package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.ApiResponse;
import io.hhplus.tdd.point.controller.dto.ChargePointResponse;
import io.hhplus.tdd.point.controller.dto.GetPointHistoryResponse;
import io.hhplus.tdd.point.controller.dto.UsePointResponse;
import io.hhplus.tdd.point.domain.history.PointHistory;
import io.hhplus.tdd.point.domain.point.UserPoint;
import io.hhplus.tdd.point.controller.dto.GetPointResponse;
import io.hhplus.tdd.point.application.UserPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointService userPointService;

    public PointController(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ApiResponse<GetPointResponse> point(
            @PathVariable long id
    ) {
        final UserPoint userPoint = userPointService.getUserPoint(id);

        return ApiResponse.isOk(GetPointResponse.toDto(userPoint));
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ApiResponse<List<GetPointHistoryResponse>> history(
            @PathVariable long id
    ) {
        final List<PointHistory> histories = userPointService.getUserPointHistories(id);

        return ApiResponse.isOk(GetPointHistoryResponse.toDto(histories));
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ApiResponse<ChargePointResponse> charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        final UserPoint userPoint = userPointService.chargeUserPoint(id, amount);

        return ApiResponse.isOk(ChargePointResponse.toDto(userPoint));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ApiResponse<UsePointResponse> use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        final UserPoint userPoint = userPointService.useUserPoint(id, amount);

        return ApiResponse.isOk(UsePointResponse.toDto(userPoint));
    }
}
