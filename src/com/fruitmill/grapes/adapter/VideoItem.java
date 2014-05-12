package com.fruitmill.grapes.adapter;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoItem {
	
	private Bitmap vThumbnail;
	private String videoPath;
	private String vLon;
	private String vLat;
	private Uri videoURI;
	
	public VideoItem() {
		super();
		vThumbnail = null;
	}

	public Bitmap getvThumbnail() {
		return vThumbnail;
	}

	public void setvThumbnail(Bitmap vThumbnail) {
		this.vThumbnail = vThumbnail;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getvLon() {
		return vLon;
	}

	public void setvLon(String vLon) {
		this.vLon = vLon;
	}

	public String getvLat() {
		return vLat;
	}

	public void setvLat(String vLat) {
		this.vLat = vLat;
	}

	public Uri getVideoURI() {
		return videoURI;
	}

	public void setVideoURI(Uri videoURI) {
		this.videoURI = videoURI;
	}
	
	
}
