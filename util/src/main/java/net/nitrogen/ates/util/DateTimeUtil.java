package net.nitrogen.ates.util;

import org.joda.time.DateTime;

import java.sql.Timestamp;

public class DateTimeUtil {
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static DateTime fromSqlTimestamp(Timestamp t) {
        return new DateTime(t.getTime());
    }

    public static String toStringWithDefaultFormat(DateTime dt) {
        return dt.toString(DEFAULT_DATE_TIME_FORMAT);
    }
}
