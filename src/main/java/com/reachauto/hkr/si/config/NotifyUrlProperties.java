package com.reachauto.hkr.si.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Administrator on 2016/8/22.
 */
@Data
@ConfigurationProperties(prefix = "notify.url")
public class NotifyUrlProperties {

    private String alipay;

    private String wechat;
}
