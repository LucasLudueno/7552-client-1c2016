package taller2.match_client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/* Base64Converter convert base64 strings in Bitmap and vice versa */
public class Base64Converter {

    /* Convert Bitmap in base 64 string */
    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /* Convert base 64 string in bitmap */
    public Bitmap Base64ToBitmap(String base64) {
        byte[] imageAsBytes = Base64.decode(base64.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
