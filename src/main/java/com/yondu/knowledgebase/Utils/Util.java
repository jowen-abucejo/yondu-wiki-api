package com.yondu.knowledgebase.Utils;

import java.text.SimpleDateFormat;
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
}
