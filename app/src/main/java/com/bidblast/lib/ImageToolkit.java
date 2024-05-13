package com.bidblast.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageToolkit {
    public static Bitmap parseBitmapFromBase64(String base64Content) {
        byte[] decodedString = Base64.decode(base64Content, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
