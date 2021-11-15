package com.easyshare.base;


public interface ValueCallBack<T> {

    void onSuccess(T t);

    default void onFail(String code) {
    }

}