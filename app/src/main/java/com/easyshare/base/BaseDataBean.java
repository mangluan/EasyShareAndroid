package com.easyshare.base;

import java.io.Serializable;


public class BaseDataBean<T> implements Serializable {

    private int code;   // 返回码
    private String msg; // 错误信息
    private T data;     // 数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
