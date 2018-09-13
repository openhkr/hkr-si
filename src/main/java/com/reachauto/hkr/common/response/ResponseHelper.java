package com.reachauto.hkr.common.response;

/**
 * Created by puras on 16/5/15.
 */
public class ResponseHelper {

    public static final int SUCCESS = 0;

    public static final int NO_PERMISSION = 2; // 用户无使用权限

    public static final int NOT_FOUND = 5; // 未找到指定数据

    private ResponseHelper() {
        // 私有化构造器
    }

    public static <T> Response<T> createSuccessResponse() {
        return createResponse(SUCCESS, null);
    }

    public static <T> Response<T> createSuccessResponse(T payload) {
        Response<T> response = createResponse(SUCCESS, null);
        response.setPayload(payload);
        return response;
    }

    public static <T> Response<T> createResponse(int code, String description) {

        Response<T> response = new Response<>();
        response.setCode(code);
        if (description != null) {
            response.setDescription(description);
        }

        return response;

    }

    public static <T> Response<T> createNotFoundResponse(T payload) {
        Response<T> response = createResponse(NOT_FOUND, "Data not found!");
        response.setPayload(payload);
        return response;
    }

    public static <T> Response<T> createNotFoundResponse() {
        return createResponse(NOT_FOUND, "Data not found!");
    }

    public static <T> Response<T> createNoPermissionResponse() {
        return createResponse(NO_PERMISSION, "No Permission!");
    }

    public static <T> Response<T> createNoPermissionResponse(String msg) {
        return createResponse(NO_PERMISSION, "No Permission!".concat(msg));
    }

}
