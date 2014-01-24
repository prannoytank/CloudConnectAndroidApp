package com.example.cloud.Util;

public class DropboxDataBean 
{
	private int fileid;
	private String filename="";
	private String filepath="";
	private String isfolder="";
	private String service="";
	
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public int getFileid() {
		return fileid;
	}
	public void setFileid(int fileid) {
		this.fileid = fileid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getIsfolder() {
		return isfolder;
	}
	public void setIsfolder(String isfolder) {
		this.isfolder = isfolder;
	}

}
