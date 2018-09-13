package com.reachauto.hkr.si.pojo.enu;

public enum FinishBizCallbackEnum {

    UN_CALL_BACK_(1, "未回调"),
    HAS_CALL_BACK(2, "回调成功");

    private final Integer code;
    private final String name;

    FinishBizCallbackEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
}
