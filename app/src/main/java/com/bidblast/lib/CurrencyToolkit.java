package com.bidblast.lib;

import java.text.DecimalFormat;

public class CurrencyToolkit {
    public static String parseToMXN(float amount) {
        DecimalFormat format = new DecimalFormat("$#,##0.00MXN");

        return format.format(amount) ;
    }
}
