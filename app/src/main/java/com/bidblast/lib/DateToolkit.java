package com.bidblast.lib;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateToolkit {
    public static Date parseDateFromIS8601(String date) {
        return Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(date)));
    }

    public static String parseToFullDateWithHour(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(
            "dd 'de' MMMM 'de' yyyy 'a las' HH:mm",
            new Locale("es", "ES")
        );
        return format.format(date);
    }

    public static String parseToFullDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(
                "dd 'de' MMMM 'de' yyyy",
                new Locale("es", "ES")
        );
        return format.format(date);
    }

    public static String parseISO8601FromLocalDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        return date.format(formatter);
    }
}
