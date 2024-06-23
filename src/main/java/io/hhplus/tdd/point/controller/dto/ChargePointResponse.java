package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.domain.point.UserPoint;

public class ChargePointResponse {

    private long id;
    private long point;
    private long updateMillis;

    public ChargePointResponse(final long id, final long point, final long updateMillis) {
        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }

    public static ChargePointResponse toDto(final UserPoint userPoint) {
        return new ChargePointResponse(
            userPoint.id(),
            userPoint.point(),
            userPoint.updateMillis()
        );
    }

    public long getId() {
        return id;
    }

    public long getPoint() {
        return point;
    }

    public long getUpdateMillis() {
        return updateMillis;
    }

}
