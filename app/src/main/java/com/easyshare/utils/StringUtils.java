package com.easyshare.utils;

import android.text.TextUtils;
import android.util.Log;

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

    /**
     * 获取URL域名
     */
    public static String getDomainHost(String url) {
        String pattern = "^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)";
        Pattern p = Pattern.compile(pattern);
        String line = url;
        Matcher m = p.matcher(line);
        if (m.find()) {
            //匹配结果
            return m.group().replace("https", "")
                    .replace("http","").replace("/", "");
        }
        Log.e("getDomainHost", "未找到的URL主域名   原始url is " + url);
        return null;
    }

}
