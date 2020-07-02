package cn.lxt6.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author chenzy
 * @since 2020-06-30
 * java8开始的新时间api,旧的日期api可能有线程安全问题
 */
public class TimeUtil {
    public final static String DEFAULT = "yyyy-MM-dd HH:mm:ss";
    //时区，北京时间
    private final static ZoneOffset zone=ZoneOffset.of("+8");
    private TimeUtil(){}

    /**
     * 得到当前日期字符串，默认格式
     * @return
     */
    public static String nowStr(){
       return nowStr(DEFAULT);
    }
    /**
     * 得到当前日期时间戳
     * @return
     */
    public static Long nowLong(){
        return now().toInstant(zone).toEpochMilli();
    }
    public static LocalDateTime now(){
        return LocalDateTime.now(zone);
    }

    /**
     * 得到当前日期字符串，指定格式
     * @param pattern
     * @return
     */
    public static String nowStr(String pattern){
        return DateTimeFormatter.ofPattern(pattern).format(now());
    }

    public static boolean isInTime(LocalDateTime beginTime, LocalDateTime endTime) {
        LocalDateTime time = now();
        return time.isAfter(beginTime)&&time.isBefore(endTime);
    }
    public static boolean isInTime(LocalDateTime beginTime, LocalDateTime endTime, LocalDateTime time) {
        return time.isAfter(beginTime)&&time.isBefore(endTime);
    }

    public static void main(String[] args) {
        System.out.println(nowStr());
        System.out.println(nowLong());
    }
}
