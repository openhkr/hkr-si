package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.si.service.ScheduleService;
import com.reachauto.hkr.si.utils.ThreadPoolTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleController {

    @Value("${env.tag}")
    private Integer envTag;

    @Autowired
    private ScheduleService scheduleService;

    @Scheduled(cron="0 0/10 * * * ?")
    public Response syncPayResult() {
        ThreadPoolTool.execute(() -> scheduleService.syncPayResult(envTag));
        ThreadPoolTool.execute(() -> scheduleService.syncRefundResult(envTag));
        ThreadPoolTool.execute(() -> scheduleService.retryCallback(envTag));
        return ResponseHelper.createSuccessResponse();
    }
}
