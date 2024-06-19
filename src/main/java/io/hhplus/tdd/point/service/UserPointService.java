package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

@Service
public class UserPointService {

    private final UserPointTable userPointTable;

    public UserPointService(final UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    public UserPoint charge(final long id, final long amount) {
        if (amount < 0) throw new IllegalArgumentException("Invalid amount.");

        final UserPoint existingUserPoint = userPointTable.selectById(id);
        final long newAmount = existingUserPoint.point() + amount;

        final UserPoint userPoint = userPointTable.insertOrUpdate(id, newAmount);

        return userPoint;
    }

}
