package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.domain.point.UserPoint;

public class UsePointResponse {

    private long id;
    private long point;
    private long updateMillis;

    public UsePointResponse(final long id, final long point, final long updateMillis) {
        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }

    public static UsePointResponse toDto(final UserPoint userPoint) {
        return new UsePointResponse(
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
