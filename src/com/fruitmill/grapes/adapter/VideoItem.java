package com.fruitmill.grapes.adapter;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoItem {
	
	private Bitmap vThumbnail;
	private String videoPath;
	private double vLon;
	private double vLat;
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

	public double getvLon() {
		return vLon;
	}

	public void setvLon(double vLon) {
		this.vLon = vLon;
	}

	public double getvLat() {
		return vLat;
	}

	public void setvLat(double vLat) {
		this.vLat = vLat;
	}

	public Uri getVideoURI() {
		return videoURI;
	}

	public void setVideoURI(Uri videoURI) {
		this.videoURI = videoURI;
	}
	
	
}
