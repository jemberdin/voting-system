package com.jemberdin.votingsystem.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtil {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final LocalTime FINISHING_UPDATE_VOTE_TIME = LocalTime.of(11, 00);
    public static final LocalDateTime DATE_TIME_FOR_TEST_AFTER = LocalDateTime.now().withHour(11).withMinute(00);

    public static LocalDate parseLocalDate(String str) {
        return StringUtils.isEmpty(str) ? null : LocalDate.parse(str);
    }
}
