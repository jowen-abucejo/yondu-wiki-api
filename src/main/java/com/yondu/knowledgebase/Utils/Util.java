package com.yondu.knowledgebase.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class Util {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");

    public static boolean isNullOrWhiteSpace(String str){
        if(str == null)
            return true;

        return str.isEmpty();
    }

    public static String convertDate(Date date) {
        return sdf.format(date);
    }

    public static boolean isEmailValid(String email) {
        Pattern emailRegex = Pattern.compile("^[A-Za-z0-9._%+-]+@yondu\\.com");
        return emailRegex.matcher(email).matches();
    }

    public static LocalDateTime convertCalendarToLDT(Calendar calendar) {
        return LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,  // Add 1 to the month value since LocalDateTime uses 1-based months
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
        );
    }
}
