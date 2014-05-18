package com.fruitmill.grapes;

import java.io.File;

import android.app.Application;

public class Grapes extends Application {
	public static File appRootDir,appVideoDir,appThumbsDir;
	public static String appVideoDirName = "videos";
	public static String appThumbsDirName = "thumbs";
	public int videoDuration = 60;
	public int locationUpdateInterval = 5000; // In milliseconds

	public int getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(int videoDuration) {
		this.videoDuration = videoDuration;
	}

	public int getLocationUpdateInterval() {
		return locationUpdateInterval;
	}

	public void setLocationUpdateInterval(int locationUpdateInterval) {
		this.locationUpdateInterval = locationUpdateInterval;
	}
}
