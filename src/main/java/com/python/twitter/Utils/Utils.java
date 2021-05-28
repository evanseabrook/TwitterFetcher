package com.python.twitter.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);

    public static String buildBigQueryDateTime(LocalDateTime dateTime) {
        String dateTimeFormatted = String.format("%d-%d-%d %d:%d:%d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond());
        logger.info(String.format("DateTime to be used: %s", dateTimeFormatted));
        return dateTimeFormatted;
    }
}
