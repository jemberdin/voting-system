package com.jemberdin.votingsystem.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeUtil {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final LocalTime FINISHING_UPDATE_VOTE_TIME = LocalTime.of(11, 00);

    public static LocalDate parseLocalDate(String str) {
        return StringUtils.isEmpty(str) ? null : LocalDate.parse(str);
    }
}
