package org.fitory.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimeFormatter {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private static final DateTimeFormatter DATE_TIME_SEC = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    /** 방금 전 / N분 전 / N시간 전 / N일 전 / N주 전 / N달 전 / N년 전 */
    public static String relative(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);

        if (seconds < 60)   return "방금 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";

        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24)     return hours + "시간 전";

        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7)       return days + "일 전";
        if (days < 30)      return (days / 7) + "주 전";
        if (days < 365)     return (days / 30) + "달 전";

        return ChronoUnit.YEARS.between(dateTime, now) + "년 전";
    }

    /** 2000.01.01 */
    public static String date(LocalDateTime dateTime) {
        return dateTime.format(DATE);
    }

    /** 2000.01.01 13:30 */
    public static String dateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME);
    }

    /** 2000.01.01 13:30:00 */
    public static String dateTimeSec(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_SEC);
    }
}
