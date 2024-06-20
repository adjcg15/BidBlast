package com.bidblast.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationToolkit {
    public static boolean isValidEmail(String text) {
        boolean isValidEmail = false;

        if (text != null) {
            String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(text);

            isValidEmail = matcher.matches();
        }

        return isValidEmail;
    }

    public static boolean isValidUserPassword(String text) {
        boolean isValidPassword = false;

        if (text != null) {
            isValidPassword = !text.isEmpty();
        }
        return isValidPassword;
    }

    public static boolean areValidKeywords(String keywords){
        boolean areValidKeywords = false;

        if (keywords != null) {
            String keywordsPattern = "^(\\w+\\s*,\\s*){2,}\\w+$";

            Pattern pattern = Pattern.compile(keywordsPattern);
            Matcher matcher = pattern.matcher(keywords);

            areValidKeywords = matcher.matches();

        }

        return  areValidKeywords;
    }

    public static boolean isPositiveFloat(String text) {
        boolean isFloat = false;

        if (text != null) {
            String floatPattern = "^([0-9]+([.][0-9])?|[.][0-9])$";

            Pattern pattern = Pattern.compile(floatPattern);
            Matcher matcher = pattern.matcher(text);

            isFloat = matcher.matches();
        }

        return isFloat;
    }
}
