package com.easyshare.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 首行缩进
     *
     * @param text 代缩减的文章
     * @return 每段添加缩进后的文章
     */
    public static String textIndent(String text) {
        String[] split = text.split("\\n");
        StringBuilder sb = new StringBuilder();
        for (String paragraph : split) {
            sb.append("\u2063\u2063\u2063\u2063　").append("\u2063\u2063\u2063\u2063　").append(paragraph).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * 判断是否是邮箱
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Matcher m = Pattern.compile(regEx1).matcher(email);
        return m.matches();
    }

}
