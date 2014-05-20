package com.fruitmill.grapes.adapter;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoItem {
	
	private Bitmap vThumbnail = null;
	private String videoPath = "";
	private String thumbPath = "";
	private String thumbBase64 = "";
	private double vLon = 0.0;
	private double vLat = 0.0;
	private Uri videoURI;
	private int rating = 0;
	private double disFromCurrentLocation;
	
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

	public String getThumbBase64() {
		return thumbBase64;
	}

	public void setThumbBase64(String thumbBase64) {
		this.thumbBase64 = thumbBase64;
	}

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public double getDisFromCurrentLocation() {
		return disFromCurrentLocation;
	}

	public void setDisFromCurrentLocation(double disFromCurrentLocation) {
		this.disFromCurrentLocation = disFromCurrentLocation;
	}
	
	
}
