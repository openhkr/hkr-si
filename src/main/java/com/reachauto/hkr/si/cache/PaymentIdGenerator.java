package com.reachauto.hkr.si.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhang.shuo-neu
 * @create 2018-03-13 19:38
 */
@Slf4j
@Component
public class PaymentIdGenerator {

    public static final long INCREMENT_STEP = 1L;

    public static final String HKR_SI_INSTANCE_KEY = "hkr.si.instance.key";

    public static int instanceNum = -1;

    public static AtomicInteger num = new AtomicInteger(1000);

    @Autowired
    private RedisTemplate redisTemplate;

    public String getAPaymentId(){
        synchronized (PaymentIdGenerator.class){
            if(instanceNum == -1){
                Long val = redisTemplate.opsForValue().increment(HKR_SI_INSTANCE_KEY, INCREMENT_STEP);
                instanceNum = (int) val.longValue();
                instanceNum = instanceNum % 10;
                log.info("PaymentIdGenerator instanceNum = {}", instanceNum);
            }
            // 编号到达9999后初始化
            num.compareAndSet(9999, 1000);
            String now = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
            return now + instanceNum + num.getAndIncrement();
        }
    }
}
