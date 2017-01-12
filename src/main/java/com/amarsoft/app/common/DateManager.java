package com.amarsoft.app.common;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ymhe on 2017/1/3.
 * 日期类
 */
public class DateManager {

    /**
     * 获取当前系统时间，默认格式yyyy/MM/dd HH:mm:ss
     * @return
     */
    public static String getCurrentDate(){
        String formatString = "yyyy/MM/dd HH:mm:ss"; //默认
        return formatDate(formatString);
    }

    /**
     * 获取当前系统时间，格式有formatString指定
     * @param formatString 时间格式，如yyyy/MM/dd HH:mm:ss
     * @return
     */
    public static String getCurrentDate(String formatString){
        return formatDate(formatString);
    }

    private static String formatDate(String formatString) {
        String dateString = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        dateString = simpleDateFormat.format(date);

        return dateString;
    }

    /**
     * 获取与指定日期相差num天的日期
     * @param beginDay
     * @param num
     * @return
     */
    public static String getDate(String beginDay, int num) {
        String nextDay = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(beginDay, new ParsePosition(0));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DATE, num);
        Date date = calendar.getTime();
        nextDay = sdf.format(date);
        return nextDay;
    }

    public static void main(String arg[]) {
        System.out.print(getCurrentDate("yyyy-MM-dd"));
    }
}
