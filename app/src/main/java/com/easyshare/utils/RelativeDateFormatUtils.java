package com.easyshare.utils;


import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RelativeDateFormatUtils {

    private static final long ONE_MINUTE = 60_000L;
    private static final long ONE_HOUR = 3_600_000L;
    private static final long ONE_DAY = 86_400_000L;
    private static final long ONE_WEEK = 604_800_000L;

    private static final String ONE_SECOND_AGO = "刚刚";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    /**
     * 日期几秒前，几分钟前，几小时前，几天前，几月前，几年前格式化工具 格式化时间戳
     */
    public static String relativeDateFormat(Date date) {
        if (null == date) {
            return null;
        }
        long delta = new Date().getTime() - date.getTime();
        if (delta < ONE_MINUTE) {
            return ONE_SECOND_AGO;
        }
        if (delta < 60L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 12L;
    }


    private static SimpleDateFormat aFormat = new SimpleDateFormat("a hh:mm", Locale.getDefault());
    private static SimpleDateFormat fFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private static SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
    private static SimpleDateFormat yFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    /**
     * 上下午，昨天，星期几，xxxx/xx/xx，xx-xx  格式化格式化聊天记录时间戳
     */
    public static String chatDateFormat(Date date) {
        if (null == date) {
            return null;
        }
        if (DateUtils.isToday(date.getTime())) {  // 今天
            return aFormat.format(date);
        }
        if (DateUtils.isToday(date.getTime() + ONE_DAY)) {  // 昨天
            return "昨天 " + aFormat.format(date);
        }
        for (int i = 0; i < 7; i++) // 一周
            if (DateUtils.isToday(date.getTime() + i * ONE_DAY))
                return fFormat.format(date) + " " + aFormat.format(date);
        if (date.getYear() == new Date().getYear())   // 今年
            return mFormat.format(date) + " " + aFormat.format(date);
        return yFormat.format(date) + " " + aFormat.format(date);   // 其他
    }

}
