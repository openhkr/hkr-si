package com.reachauto.hkr.si.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-28 14:32
 */

public class ScheduleControllerTest {

    @InjectMocks
    private ScheduleController scheduleController = new ScheduleController();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void syncPayResult(){
        scheduleController.syncPayResult();
    }
}
