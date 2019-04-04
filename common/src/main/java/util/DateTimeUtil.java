package util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @Author: Dunfu Peng
 * @Date: 2019/4/2 22:36
 */
public class DateTimeUtil {

    private static DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDate toLocalDate(Long date){
        return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static long toLong(LocalDateTime localDateTime){
        return Timestamp.valueOf(localDateTime).getTime();
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime){
        return localDateTime.format(dateTimeFormatter);
    }

    public static String formatLocalDateTime(Long dateTime){
        return toLocalDate(dateTime).format(dateTimeFormatter);
    }

}
