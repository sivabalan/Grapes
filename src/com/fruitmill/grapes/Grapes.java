package com.fruitmill.grapes;

import java.io.File;

import android.app.Application;

public class Grapes extends Application {
	public File appRootDir,appVideoDir;
	public String appVideoDirName = "videos";
	public int videoDuration = 60;

	public int getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(int videoDuration) {
		this.videoDuration = videoDuration;
	}
}
