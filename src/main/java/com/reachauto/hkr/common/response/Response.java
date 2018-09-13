package com.reachauto.hkr.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by haojr on 17/06/08.
 */
@ApiModel
public class Response<T> {

    @ApiModelProperty(position = 1, required = true, value = "返回内部编码:0:成功 / 非0:失败")
    private int code;

    @ApiModelProperty(position = 2, required = true, value = "消息描述")
    private String description = "";

    @ApiModelProperty(position = 3, required = true, value = "最后更新时间")
    private long lastUpdateTime;

    @ApiModelProperty(position = 4, required = true, value = "返回体内容")
    private T payload;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", description='" + description + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                ", payload=" + payload +
                '}';
    }

}
