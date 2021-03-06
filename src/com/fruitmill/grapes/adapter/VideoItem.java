package com.fruitmill.grapes.adapter;

import com.fruitmill.grapes.MainActivity;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

public class VideoItem {
	
	private Bitmap vThumbnail = null;
	private String videoPath = "";
	private String thumbPath = "";
	private String thumbBase64 = "";
	private double vLon = 0.0;
	private double vLat = 0.0;
	private Uri videoURI = null;
	private int rating = 0;
	private double disFromCurrentLocation = 0;
	private String displayAddress = "";
	private String videoID = "";
	
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
		Location loc = new Location("");
        loc.setLatitude(this.vLat);
        loc.setLongitude(this.vLon);
        return loc.distanceTo(MainActivity.location);
	}

	public void setDisFromCurrentLocation(double disFromCurrentLocation) {
		this.disFromCurrentLocation = disFromCurrentLocation;
	}

	public String getDisplayAddress() {
		return displayAddress;
	}

	public void setDisplayAddress(String displayAddress) {
		this.displayAddress = displayAddress;
	}

	public String getVideoID() {
		return videoID;
	}

	public void setVideoID(String videoID) {
		this.videoID = videoID;
	}
	
	
}
