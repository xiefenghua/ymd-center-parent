package com.ymd.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 时间工具类
 *
 */
public class DateUtil
{
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyy_MM_ddHH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
    public static final String yyyy_MM_ddHH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String yyyy_MM_ddHH_mm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_ddTHH_mm_ssZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String yyyy_MM = "yyyy-MM";
    public static final String yyyyMM = "yyyyMM";

    /**
     * @param second 秒
     * @description: 秒转换为时分秒 HH:mm:ss 格式 仅当小时数大于0时 展示HH
     * @return: {@link String}
     * @author: pzzhao
     * @date: 2022-05-08 13:55:17
     */
    public static String second2Time(Long second) {
        if (second == null || second < 0) {
            return "0秒(立即)";
        }
        long nd = 24 * 60 * 60;// 一天的秒数
        // 计算差多少天
        long day = second / nd;
        long h = second / 3600;
        long m = (second % 3600) / 60;
        long s = second % 60;
        String str = "";
        if(day>0) {
            str+= day + "天" ;
        }
        if (h > 0) {
            str = (h < 10 ? ("0" + h) : h) + "时";
        }
        if (m > 0) {
            str += (m < 10 ? ("0" + m) : m) + "分";
        }
        if (s > 0) {
            str += (s < 10 ? ("0" + s) : s) + "秒";
        }
        return str;
    }
    public static Date getDate() {
        return new Date();
    }
    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.yyyy_MM_ddHH_mm_ss);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(new Date());
    }

    public static String format(Date date, String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(date);
    }
    public static String getTimeStamp() {
        Date date = new Date();
        long ts = date.getTime() / 1000;
        return String.valueOf(ts);
    }
    public static String format(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_ddHH_mm_ss);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(date);
    }
    public static String format(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_ddHH_mm_ss);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(timestamp);
    }
    public static String format(long timestamp,String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(timestamp);
    }
    public static String format(String oldTime,String pattern){
        SimpleDateFormat new_format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = new_format.parse(oldTime);
        } catch (ParseException e) {
            try {
                date = new_format.parse((Long.valueOf(oldTime)*1000)+"");
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        String new_str = new_format.format(date);
        return new_str;
    }
    public static long getTimeStampFormat(String time, String format) {
        long reTime ;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(time);
            reTime = d.getTime()/1000;
        } catch (ParseException e) {
            reTime = Long.parseLong(time.substring(0, 10));
        }
        return reTime;
    }
    /**
     * 将时间格式long转换成yyyy-mm-dd
     */
    public static  String convertTimeLongToStr(String time){
        long startDate=Long.parseLong(time)*1000;
        Date date=new Date();
        date.setTime(startDate);
        SimpleDateFormat format = new SimpleDateFormat(yyyy_MM_dd);
        return format.format(date);
    }
    public static  String convertTimeLongToStr(String time, String pattern){
        long startDate=Long.parseLong(time)*1000;
        Date date=new Date();
        date.setTime(startDate);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    /**
     * 转换成某年某月某日某时某分某秒
     * @return
     */
    public static String formatChina(Date dateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minutes=calendar.get(Calendar.MINUTE);
        int seconds=calendar.get(Calendar.SECOND);
        String resultDate=year+"年"+month+"月"+date+"日"+hour+"时"+minutes+"分"+seconds+"秒";
        return resultDate;
    }
    /**
     * 转换成某年某月某日
     * @return
     */
    public static String formatChinaToMonth(Date dateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        int month=calendar.get(Calendar.MONTH)+1;
        int date=calendar.get(Calendar.DATE);
        String resultDate=month+"月"+date+"日";
        return resultDate;
    }
    /**
     * 转换时间异常
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try
        {
            return sdf.parse(date);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
    public static Date formatIntDate(long unixSecond) {
        Date date = new Date();
        if (unixSecond != 0) {
            date.setTime(unixSecond * 1000L);
        }
        return date;
    }
    public static Date getAfterDaysToday(Date date,int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, days);

        return calendar.getTime();
    }
    public static Date getBeforeDaysToday(int days) {
        return getAfterDaysToday(new Date(),days) ;
    }
    public static Date getBeforeMonthsToday(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -months);
        return calendar.getTime();
    }
    public static Date getAfterMonthsToday(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }
    public static String getAfterYear(int num){
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.yyyy_MM_ddHH_mm_ss);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, num);
        Date y = c.getTime();
        return format.format(y);
    }
    /**
     * 计算相差毫秒数
     * @param startTime
     * @param endTime
     * @return
     */
    public static Long between_days(String startTime, String endTime) {
        Calendar calendar_a = Calendar.getInstance();// 获取日历对象
        Calendar calendar_b = Calendar.getInstance();
        Date date_a = parse(startTime,yyyy_MM_ddHH_mm_ss);//字符串转Date
        Date date_b = parse(endTime,yyyy_MM_ddHH_mm_ss);
        calendar_a.setTime(date_a);// 设置日历
        calendar_b.setTime(date_b);
        long time_a = calendar_a.getTimeInMillis();
        long time_b = calendar_b.getTimeInMillis();
        long between_days = (time_b - time_a);//计算相差毫秒数
        return between_days;
    }
    public static Long curr_between_days(String endTime) {
        return between_days(format(new Date()), endTime);
    }
    public static Long between_times(Date startTime, Date endTime) {
        long time_a = startTime.getTime();
        long time_b = endTime.getTime();
        long between_times = (time_b - time_a);//计算相差毫秒数
        return between_times;
    }

    //符合YYYY-MM-DD hh:mm:ss格式
    public static boolean checkTimeFormateYYYYMMDDHHmmss(String time){
        String timeRegex = "^(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01]) (0?\\d|1\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
        return Pattern.matches(timeRegex,time);
    }

    //符合YYYY-MM-DD hh:mm:ss格式
    public static boolean checkTimeFormateYYYYMMDDHHmm(String time){
        String timeRegex = "^(\\d{4})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01]) (0?\\d|1\\d|2[0-3]):([0-5]\\d)$";
        return Pattern.matches(timeRegex,time);
    }

    /**
     * 获取当前时间后延指定毫秒数的时间戳
     * @param delayMs 后延时间
     * @return 延后的时间戳
     */
    public static Long delayTime(long delayMs) {
        return delayTime(new Date(), delayMs);
    }

    /**
     * 获取指定时间后延指定毫秒数的时间戳
     * @param delayMs 后延时间
     * @return 延后的时间戳
     */
    public static Long delayTime(Date date, long delayMs) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null!");
        }
        return date.getTime() + delayMs;
    }

    /**
     * 获取指定时间后延指定毫秒数的时间戳
     * @param timestamp 时间戳
     * @param delayMs 后延时间
     * @return 延后的时间戳
     */
    public static Long delayTime(long timestamp, long delayMs) {
        return timestamp + delayMs;
    }
}
