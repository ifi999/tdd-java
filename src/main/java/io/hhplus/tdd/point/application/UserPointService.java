package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.history.PointHistory;
import io.hhplus.tdd.point.domain.history.TransactionType;
import io.hhplus.tdd.point.domain.point.UserPoint;
import io.hhplus.tdd.point.infra.PointHistoryTable;
import io.hhplus.tdd.point.infra.UserPointTable;
import io.hhplus.tdd.point.infra.entity.PointHistoryEntity;
import io.hhplus.tdd.point.infra.entity.UserPointEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserPointService {

    private final Map<Long, Lock> lockMap = new ConcurrentHashMap<>();

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPointService(final UserPointTable userPointTable, final PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint chargeUserPoint(final long id, final long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount: " + amount + ". The amount must not be a positive number.");

        final Lock lock = lockMap.computeIfAbsent(id, o -> new ReentrantLock());
        lock.lock();

        try {
            final UserPointEntity existingUserPoint = userPointTable.selectById(id);
            final UserPointEntity userPointEntity = changeUserPoint(
                id,
                existingUserPoint.getPoint() + amount,
                TransactionType.CHARGE
            );

            return new UserPoint(
                userPointEntity.getId(),
                userPointEntity.getPoint(),
                userPointEntity.getUpdateMillis()
            );
        } finally {
            lock.unlock();
        }

    }

    public UserPoint useUserPoint(final long id, final long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount: " + amount + ". The amount must not be a positive number.");

        final Lock lock = lockMap.computeIfAbsent(id, o -> new ReentrantLock());
        lock.lock();

        try {
            final UserPointEntity existingUserPoint = userPointTable.selectById(id);
            final long balance = calculateBalance(existingUserPoint.getPoint(), amount);
            final UserPointEntity userPointEntity = changeUserPoint(id, balance, TransactionType.USE);

            return new UserPoint(
                userPointEntity.getId(),
                userPointEntity.getPoint(),
                userPointEntity.getUpdateMillis()
            );
        } finally {
            lock.unlock();
        }
    }

    public UserPoint getUserPoint(final long id) {
        final UserPointEntity userPointEntity = userPointTable.selectById(id);

        return new UserPoint(
            userPointEntity.getId(),
            userPointEntity.getPoint(),
            userPointEntity.getUpdateMillis()
        );
    }

    public List<PointHistory> getUserPointHistories(final long id) {
        final List<PointHistoryEntity> histories = pointHistoryTable.selectAllByUserId(id);

        return histories.stream()
            .map(pointHistoryEntity -> new PointHistory(
                pointHistoryEntity.getId(),
                pointHistoryEntity.getUserId(),
                pointHistoryEntity.getAmount(),
                pointHistoryEntity.getType(),
                pointHistoryEntity.getUpdateMillis()
            ))
            .toList();
    }

    private long calculateBalance(final long remainingPoint, final long amount) {
        final long balance = remainingPoint - amount;
        if (balance < 0) {
            throw new IllegalArgumentException("Insufficient points. Tried to use " + amount + " points, but only " + remainingPoint + " points are available.");
        }

        return balance;
    }

    private UserPointEntity changeUserPoint(final long id, final long addedAmount, final TransactionType transactionType) {
        final UserPointEntity userPointEntity = userPointTable.insertOrUpdate(id, addedAmount);
        insertPointHistory(userPointEntity, transactionType);

        return userPointEntity;
    }

    private PointHistoryEntity insertPointHistory(final UserPointEntity userPointEntity, final TransactionType transactionType) {
        return pointHistoryTable.insert(
            userPointEntity.getId(),
            userPointEntity.getPoint(),
            transactionType,
            userPointEntity.getUpdateMillis());
    }

}
