package com.reachauto.hkr.si.service;

/**
 * @author zhangshuo
 */
public interface ScheduleService {

    void syncPayResult(Integer envTag);

    void syncRefundResult(Integer envTag);

    void retryCallback(Integer envTag);
}
