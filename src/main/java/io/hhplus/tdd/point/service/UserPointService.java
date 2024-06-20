package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPointService(final UserPointTable userPointTable, final PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint charge(final long id, final long amount) {
        if (amount < 0) throw new IllegalArgumentException("Invalid amount.");

        final UserPoint existingUserPoint = userPointTable.selectById(id);
        final long newAmount = existingUserPoint.point() + amount;

        final UserPoint userPoint = userPointTable.insertOrUpdate(id, newAmount);
        pointHistoryTable.insert(id, existingUserPoint.point() + amount, TransactionType.CHARGE, System.currentTimeMillis());

        return userPoint;
    }

    public UserPoint use(final long id, final long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount.");

        final UserPoint existingUserPoint = userPointTable.selectById(id);
        if (existingUserPoint.point() - amount < 0) throw new IllegalArgumentException("Invalid amount.");

        final long newAmount = existingUserPoint.point() - amount;
        final UserPoint userPoint = userPointTable.insertOrUpdate(id, newAmount);
        pointHistoryTable.insert(id, newAmount, TransactionType.USE, System.currentTimeMillis());

        return userPoint;
    }

    public UserPoint point(final long id) {
        final UserPoint existingUserPoint = userPointTable.selectById(id);

        return existingUserPoint;
    }

    public List<PointHistory> history(final long id) {
        final List<PointHistory> histories = pointHistoryTable.selectAllByUserId(id);

        return histories;
    }

}
