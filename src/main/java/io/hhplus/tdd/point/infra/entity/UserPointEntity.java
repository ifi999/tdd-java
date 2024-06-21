package io.hhplus.tdd.point.infra.entity;

import org.springframework.util.Assert;

public class UserPointEntity {

    private long id;
    private long point;
    private long updateMillis;

    public UserPointEntity(final long id, final long point, final long updateMillis) {
        Assert.isTrue(id > 0, "The id must be positive");
        Assert.isTrue(point >= 0, "The point must greater than zero.");
        Assert.isTrue(updateMillis > 0, "The updateMillis must be positive.");

        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }

    public static UserPointEntity empty(long id) {
        return new UserPointEntity(id, 0L, System.currentTimeMillis());
    }

    public UserPointEntity changePoint(final long point) {
        return new UserPointEntity(this.id, point, System.currentTimeMillis());
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
