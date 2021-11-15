package com.easyshare.base;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 为了简化不必要的监听，直接在这里处理掉
 */
public interface SimpleTextWatcher extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after){}

    @Override
    void onTextChanged(CharSequence s, int start, int before, int count);

    @Override
    default void afterTextChanged(Editable s){}
}
