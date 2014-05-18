package com.fruitmill.grapes.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Utils {
    
    public static String imageFileToString(String imgFilePath) throws IOException {
    	Bitmap bm = BitmapFactory.decodeFile(imgFilePath);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
    	byte[] b = baos.toByteArray();
    	
    	return Base64.encodeToString(b, Base64.DEFAULT);
    }
    
    public static Bitmap stringToImageFile(String b64ImageString) {
    	byte[] decodedString = Base64.decode(b64ImageString, Base64.DEFAULT);
    	Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    	return decodedImage; 
    }
}
