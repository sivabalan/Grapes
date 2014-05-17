package com.fruitmill.grapes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.fruitmill.grapes.adapter.VideoItem;

public class DropBox {
	static final String TAG = "Grapes";

    final static public String APP_KEY = "kuc88ae9y2coj3j";
    final static public String APP_SECRET = "7dk0xsu1o5gay37";

    // You don't need to change these, leave them alone.
    final static public String ACCOUNT_PREFS_NAME = "prefs";
    final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    // Set entry to null if you are sure it is a new file.
    private static String uploadSingleFile(String videoFilePath, boolean mLoggedIn, DropboxAPI<AndroidAuthSession> mApi, String filePath, Entry entry){
    	try {
    		File file = new File(videoFilePath);
    		FileInputStream inputStream = new FileInputStream(file);
    		Entry newEntry = (entry == null) ? mApi.putFile(filePath, inputStream, file.length(), null, null) : mApi.putFile(filePath, inputStream, file.length(), entry.rev, null); 
    		
    		inputStream.close();
    		mApi.share(newEntry.path);
    		return mApi.share(filePath).url;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			return null;
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			return null;
		}
    }
    
    public static String tryUploadSingleFile(String videoFilePath, boolean mLoggedIn, DropboxAPI<AndroidAuthSession> mApi){
		if (mLoggedIn){
			String fileName = Integer.toString(videoFilePath.hashCode());
			String filePath = "/grapes-public/" + fileName + ".mp4";
			try{
				Entry entry = mApi.metadata(filePath, 1, null, false, null);
				if (entry == null || entry.bytes == 0){
					return uploadSingleFile(videoFilePath, mLoggedIn, mApi, filePath, entry);
				}
			}
			catch (DropboxException e){
				return uploadSingleFile(videoFilePath, mLoggedIn, mApi, filePath, null);
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private static String readFully(InputStream inputStream, String encoding)
	        throws IOException {
	    return new String(readFully(inputStream), encoding);
	}    

	private static byte[] readFully(InputStream inputStream)
	        throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int length = 0;
	    while ((length = inputStream.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    
	    return baos.toByteArray();
	}
	
		public static void setLoggedIn(final boolean loggedIn, final DropboxAPI<AndroidAuthSession> mApi, final Cursor videoCursor) {
    	if (loggedIn){
    			Thread t = new Thread( new Runnable() {
					
					@Override
					public void run() {
						try {
							mApi.createFolder("/grapes-public");
						}
						catch (DropboxException e) {
							
							System.out.println("Folder already there.");
						}
						
						StringBuilder sb = new StringBuilder();
						int rows = videoCursor.getCount();
						for (int i =0 ;i< rows; i++){
					    	int videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
						    videoCursor.moveToPosition(i);
					       	String videoFilePath = videoCursor.getString(videoColumnIndex);
					       	String shareLink = tryUploadSingleFile(videoFilePath, loggedIn, mApi);
					       	
					       	if (shareLink != null){
					       		sb.append(shareLink + "\n");
					       		int latColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.LATITUDE);
					       		int longColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.LONGITUDE);
					       		VideoItem video = new VideoItem();
					       		video.setVideoPath(videoFilePath);
					       		video.setVideoURI(Uri.parse(shareLink));
					       		video.setvLat(videoCursor.getDouble(latColumnIndex));
					       		video.setvLon(videoCursor.getDouble(longColumnIndex));
					       		videoCursor.getDouble(latColumnIndex);
							    MainActivity.sendShareToServer(video);
					       	}
					    }
						
						try {
							FileWriter f = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/Download/hopefulout.txt");
							f.write(sb.toString());
							f.close();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
    			t.start();
    	}
    }
}
