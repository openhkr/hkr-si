package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.pojo.dto.PayParaDTO;
import com.reachauto.hkr.si.pojo.vo.PayVO;

/**
 * Created by Administrator on 2018/1/16.
 */
public interface PayService {

    PayVO pay(PayParaDTO payParaDTO);
}
