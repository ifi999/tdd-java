package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.domain.point.UserPoint;

public class GetPointResponse {

    private long id;
    private long point;

    public GetPointResponse(final long id, final long point) {
        this.id = id;
        this.point = point;
    }

    public static GetPointResponse toDto(final UserPoint userPoint) {
        return new GetPointResponse(
            userPoint.id(),
            userPoint.point()
        );
    }

    public long getId() {
        return id;
    }

    public long getPoint() {
        return point;
    }

}
