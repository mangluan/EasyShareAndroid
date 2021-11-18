package com.easyshare.base;

import com.easyshare.R;
import com.hjq.toast.ToastUtils;

import io.reactivex.functions.Consumer;

public interface RxjavaThrowable extends Consumer<Throwable> {

    @Override
    default void accept(Throwable throwable) throws Exception {
        // 提示语
        if (throwable instanceof BaseException) {
            ToastUtils.show(throwable.getMessage());
        } else {
            ToastUtils.show(R.string.error_network);
        }
        throwable(throwable);
        throwable.printStackTrace();
    }

    void throwable(Throwable throwable) throws Exception;

}
