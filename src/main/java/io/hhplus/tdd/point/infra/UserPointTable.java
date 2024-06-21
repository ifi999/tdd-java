package io.hhplus.tdd.point.infra;

import io.hhplus.tdd.point.infra.entity.UserPointEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
public class UserPointTable {

    private final Map<Long, UserPointEntity> table = new ConcurrentHashMap<>();

    public UserPointEntity selectById(Long id) {
        throttle(200);
        return table.getOrDefault(id, UserPointEntity.empty(id));
    }

    public UserPointEntity insertOrUpdate(long id, long amount) {
        throttle(300);

        final UserPointEntity userPointEntity = table.getOrDefault(id, UserPointEntity.empty(id));
        final UserPointEntity changedUserPointEntity = userPointEntity.changePoint(amount);

        table.put(id, changedUserPointEntity);

        return changedUserPointEntity;
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}
