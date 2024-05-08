package com.bidblast.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationToolkit {
    public static boolean isValidEmail(String text) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }

    public static boolean isValidUserPassword() {
        boolean isValidUserPassword = true;

        return isValidUserPassword;
    }
}
