package com.reachauto.hkr.si.pojo.parameter;

public class TaskComplex {

    private String url;
    private String method;

    private Integer count;

    private String sysMark;

    private String interval;

    private Integer type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getSysMark() {
        return sysMark;
    }

    public void setSysMark(String sysMark) {
        this.sysMark = sysMark;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TaskComplex{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", count=" + count +
                ", sysMark='" + sysMark + '\'' +
                ", interval='" + interval + '\'' +
                ", type=" + type +
                '}';
    }
}
