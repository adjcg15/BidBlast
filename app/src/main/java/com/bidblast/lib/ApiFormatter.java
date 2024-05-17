package com.bidblast.lib;

import java.util.List;

public class ApiFormatter {
    public static String parseToPlainMultiValueParam(List<Integer> values) {
        String plainParameter = "";

        if(values != null && !values.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < values.size(); i++) {
                builder.append(values.get(i));
                if (i < values.size() - 1) {
                    builder.append(",");
                }
            }

            plainParameter = builder.toString();
        }

        return plainParameter;
    }
}
