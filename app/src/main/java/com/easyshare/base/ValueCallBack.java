package com.easyshare.base;


public interface ValueCallBack<T> {

    void onSuccess(T t);

    void onFail(String code);

}