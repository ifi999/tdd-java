package io.hhplus.tdd.point.domain.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public static UserPoint changePoint(final long id, final long point) {
        return new UserPoint(
            id,
            point,
            System.currentTimeMillis()
        );
    }

}
