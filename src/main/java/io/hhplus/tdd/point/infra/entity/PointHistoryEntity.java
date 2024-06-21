package io.hhplus.tdd.point.infra.entity;

import io.hhplus.tdd.point.domain.history.TransactionType;
import org.springframework.util.Assert;

public class PointHistoryEntity {

    private long id;
    private long userId;
    private long amount;
    private TransactionType type;
    private long updateMillis;

    public PointHistoryEntity(final long id, final long userId, final long amount, final TransactionType type, final long updateMillis) {
        Assert.isTrue(id > 0, "The id must be positive");
        Assert.isTrue(userId > 0, "The userId must be positive.");
        Assert.isTrue(amount >= 0, "The amount must greater than zero.");
        Assert.notNull(type, "The transaction type must not be null.");
        Assert.isTrue(updateMillis > 0, "The updateMillis must be positive.");

        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.updateMillis = updateMillis;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public long getUpdateMillis() {
        return updateMillis;
    }

}
