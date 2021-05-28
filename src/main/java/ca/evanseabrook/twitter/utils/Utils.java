package ca.evanseabrook.twitter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String buildBigQueryDateTime(LocalDateTime dateTime) {
        String dateTimeFormatted = String.format("%d-%d-%d %d:%d:%d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond());
        logger.debug(String.format("DateTime to be used: %s", dateTimeFormatted));
        return dateTimeFormatted;
    }
}
