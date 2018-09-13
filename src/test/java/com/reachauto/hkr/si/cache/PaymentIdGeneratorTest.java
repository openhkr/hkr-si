package com.reachauto.hkr.si.cache;

import com.reachauto.hkr.si.mockclass.HkrValueOperations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhang.shuo-neu
 * @create 2018-03-13 20:07
 */

public class PaymentIdGeneratorTest {

    @InjectMocks
    private PaymentIdGenerator paymentIdGenerator = new PaymentIdGenerator();

    @Mock
    private RedisTemplate redisTemplate;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        HkrValueOperations hkrValueOperations = new HkrValueOperations();
        Mockito.doReturn(hkrValueOperations).when(redisTemplate).opsForValue();
    }

    @Test
    public void generator_success(){

        Set<String> set = new HashSet<>();
        AtomicInteger repeatnum = new AtomicInteger(0);

        for(int i=0; i<1000; i++){
            String id = paymentIdGenerator.getAPaymentId();
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!set.add(id)){
                System.out.println(id + ":" + repeatnum.getAndIncrement());
            }
        }
    }
}
