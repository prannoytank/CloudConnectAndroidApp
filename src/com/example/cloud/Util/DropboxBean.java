package com.example.cloud.Util;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public class DropboxBean
{

	private DropboxAPI<AndroidAuthSession> mDBApi;
	private AndroidAuthSession sess;

	public AndroidAuthSession getSess() {
		return sess;
	}

	public void setSess(AndroidAuthSession sess)
	{
		this.sess = sess;
	}

	public DropboxAPI<AndroidAuthSession> getmDBApi()
	{
		return mDBApi;
	}

	public void setmDBApi(DropboxAPI<AndroidAuthSession> mDBApi) 
	{
		this.mDBApi = mDBApi;
	}
	
	
}
