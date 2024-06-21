package io.hhplus.tdd.point.infra;


import io.hhplus.tdd.point.domain.history.TransactionType;
import io.hhplus.tdd.point.infra.entity.PointHistoryEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
public class PointHistoryTable {
    private final List<PointHistoryEntity> table = new ArrayList<>();
    private long cursor = 1;

    public PointHistoryEntity insert(long userId, long amount, TransactionType type, long updateMillis) {
        throttle(300L);

        PointHistoryEntity pointHistory = new PointHistoryEntity(cursor++, userId, amount, type, updateMillis);
        table.add(pointHistory);

        return pointHistory;
    }

    public List<PointHistoryEntity> selectAllByUserId(long userId) {
        return table.stream()
            .filter(pointHistory -> pointHistory.getUserId() == userId)
            .toList();
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}
