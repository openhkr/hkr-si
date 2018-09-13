package com.reachauto.hkr.common.json;

public class ReturnCode {
    public static final int SUCCESS = 0;
    public static final int NOT_UPGRADE = 1;
    public static final int NO_PERMISSION = 2;
    public static final int EXCEPTION = 3;
    public static final int BIND_ERROR = 4;
    public static final int NOT_FOUND = 5;
    public static final int PARAM_ERROR = 6;
    public static final int REMOTE_CALL_ERROR = 7;
    public static final int BUSINESS_ERROR = 8;
    public static final int NEVER_USED_CODE = -999999;

    public ReturnCode() {
    }
}