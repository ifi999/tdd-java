package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.domain.history.PointHistory;
import io.hhplus.tdd.point.domain.history.TransactionType;

import java.util.List;
import java.util.stream.Collectors;

public class GetPointHistoryResponse {

    private long id;
    private long userId;
    private long amount;
    private TransactionType type;
    private long updateMillis;

    public GetPointHistoryResponse(final long id, final long userId, final long amount, final TransactionType type, final long updateMillis) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.updateMillis = updateMillis;
    }

    public static List<GetPointHistoryResponse> toDto(final List<PointHistory> pointHistories) {
        return pointHistories.stream()
            .map(pointHistory -> new GetPointHistoryResponse(
                pointHistory.id(),
                pointHistory.userId(),
                pointHistory.amount(),
                pointHistory.type(),
                pointHistory.updateMillis()
            ))
            .toList();
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
