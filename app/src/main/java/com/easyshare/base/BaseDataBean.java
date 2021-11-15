package com.easyshare.base;

import java.io.Serializable;


public class BaseDataBean<T> implements Serializable {

    private int code;   // 返回码
    private String msg; // 错误信息
    private T data;     // 数据

    public BaseDataBean() {
    }

    public BaseDataBean(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

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


    public static <T> BaseDataBean<T> build(int code) {
        return build(code, null, null);
    }

    public static <T> BaseDataBean<T> build(int code, T data) {
        return build(code, null, data);
    }

    public static <T> BaseDataBean<T> build(int code, String msg) {
        return build(code, msg, null);
    }

    public static <T> BaseDataBean<T> build(int code, String msg, T data) {
        return new BaseDataBean<T>(code, msg, data);
    }

    @Override
    public String toString() {
        return "BaseDataBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
