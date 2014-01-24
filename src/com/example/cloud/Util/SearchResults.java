package com.example.cloud.Util;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResults implements Parcelable 
{
	private String filename = "";
	private String isFolder = "";
	private String path = "";
	private int fileid;
	private String service="";
	private String image="";
	
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getService()
	{
		return service;
	}
	public void setService(String service)
	{
		this.service = service;
	}
	public int getFileid()
	{
		return fileid;
	}
	public void setFileid(int fileid) {
		this.fileid = fileid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) 
	{
		this.filename = filename;
	}
	public String getIsFolder()
	{
		return isFolder;
	}
	public void setIsFolder(String isFolder)
	{
		this.isFolder = isFolder;
	}
	public String getPath()
	{
		return path;
	}
	public void setPath(String path) 
	{
		this.path = path;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

	

	