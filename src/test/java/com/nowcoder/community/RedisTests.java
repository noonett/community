package com.nowcoder.community;

import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import com.nowcoder.community.util.RedisLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisLock redisLock;

    @Test
    public void testStrings() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "张三");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
    }

    // 编程式事务
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:testnull";
                redisOperations.multi();

                redisOperations.opsForSet().size(redisKey);

                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }

    @Test
    void testNUll() {
        String redisKey = RedisKeyUtil.getUserLike(1000);
        System.out.println(redisTemplate.opsForValue().size(redisKey));
    }

    // 统计20万个重复数据的独立总数, 可以用在对签到或者UV的统计
    @Test
    public void testHyperLogLog() {
        String redisKey = "test:hll:01";

        for (int i = 1; i <= 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        //重复
        for (int i = 1; i <= 100000; i++) {
            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }
        long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    // 将3组数据合并，再合并后的重复数据的独立总数。
    @Test
    public void testHyperLogLogUnion() {
        String rediskey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(rediskey2, i);
        }

        String rediskey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(rediskey3, i);
        }

        String rediskey4 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(rediskey4, i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, rediskey2, rediskey3, rediskey4);

        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    // 统计一组数据的布尔值
    @Test
    public void testBitMap() {
        String redisKey = "test:bm:01";

        // 记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

        // 统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(obj);
    }

    // 统计3组数据的布尔值，并对这三组数据做OR运算
    @Test
    public void testBitMapOperation() {
        String rediskey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(rediskey2, 0, true);
        redisTemplate.opsForValue().setBit(rediskey2, 1, true);
        redisTemplate.opsForValue().setBit(rediskey2, 2, true);

        String rediskey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(rediskey3, 2, true);
        redisTemplate.opsForValue().setBit(rediskey3, 3, true);
        redisTemplate.opsForValue().setBit(rediskey3, 4, true);

        String rediskey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(rediskey4, 4, true);
        redisTemplate.opsForValue().setBit(rediskey4, 5, true);
        redisTemplate.opsForValue().setBit(rediskey4, 6, true);

        String redisKey = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),
                        rediskey2.getBytes(), rediskey3.getBytes(), rediskey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
    }

    @Test
    public void testRedisLock() throws InterruptedException {
        String serviceName = "test";
        String key = RedisKeyUtil.getLockKey(serviceName);
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            Thread.sleep((long) Math.random() * 1000);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String token = CommunityUtil.generateUUID();
                    System.out.println(Thread.currentThread() + "--线程开始尝试锁！");
                    try {
                        if (redisLock.lock(key, token, 3)) {
                            System.out.println(Thread.currentThread() + "--线程获取锁成功！");
                            Thread.sleep(2000);
                            if (redisLock.unLock(key, token)) {
                                System.out.println(Thread.currentThread() + "--线程释放锁！");
                            } else {
                                System.out.println(Thread.currentThread() + "--锁已超时释放！");
                            }

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Thread.sleep(10000);
    }

    @Test
    void testUnLock() {
        String serviceName = "test";
        String key = RedisKeyUtil.getLockKey(serviceName);
        redisLock.unLock(key, CommunityUtil.generateUUID());
    }
}
