package com.reachauto.hkr.si.pojo.enu;

import java.util.Objects;

/**
 * Created by Administrator on 2018/4/10.
 */
public enum EnvironmentFlagEnum {
    PROD("1", "prod"),
    RC("2", "rc");

    private final String code;
    private final String name;

    EnvironmentFlagEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static EnvironmentFlagEnum getType(String code) {
        for (EnvironmentFlagEnum status : EnvironmentFlagEnum.values()) {
            if (Objects.equals(code, status.code)) {
                return status;
            }
        }
        return EnvironmentFlagEnum.PROD;
    }
}
