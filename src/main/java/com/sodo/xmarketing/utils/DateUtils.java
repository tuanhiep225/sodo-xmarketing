package com.sodo.xmarketing.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by tahi1990 on 23/06/2017.
 */
public class DateUtils {

  private static final String DATE_FORMAT = "dd-MM-yyyy";
  private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

  public static LocalDate formatDate(String date) throws Exception {
    return LocalDate.parse(date, DATE_FORMATTER);
  }

  public static LocalDateTime formatDateTime(String date) throws Exception {
    return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
  }

  public static LocalDate formatDate(String date, String format) throws Exception {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(date, dateTimeFormatter);
  }

  public static boolean isNullOrEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }
}
