package com.reachauto.hkr.si.mockclass;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhang.shuo-neu
 * @create 2018-03-14 10:50
 */

public class HkrValueOperations implements ValueOperations<String, Long> {

    @Override
    public void set(String s, Long aLong) {

    }

    @Override
    public void set(String s, Long aLong, long l, TimeUnit timeUnit) {

    }

    @Override
    public Boolean setIfAbsent(String s, Long aLong) {
        return null;
    }

    @Override
    public void multiSet(Map<? extends String, ? extends Long> map) {

    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends String, ? extends Long> map) {
        return null;
    }

    @Override
    public Long get(Object o) {
        return null;
    }

    @Override
    public Long getAndSet(String s, Long aLong) {
        return null;
    }

    @Override
    public List<Long> multiGet(Collection<String> collection) {
        return null;
    }

    @Override
    public Long increment(String s, long l) {
        return 1L;
    }

    @Override
    public Double increment(String s, double v) {
        return null;
    }

    @Override
    public Integer append(String s, String s2) {
        return null;
    }

    @Override
    public String get(String s, long l, long l1) {
        return null;
    }

    @Override
    public void set(String s, Long aLong, long l) {

    }

    @Override
    public Long size(String s) {
        return null;
    }

    @Override
    public Boolean setBit(String s, long l, boolean b) {
        return null;
    }

    @Override
    public Boolean getBit(String s, long l) {
        return null;
    }

    @Override
    public RedisOperations<String, Long> getOperations() {
        return null;
    }
}
