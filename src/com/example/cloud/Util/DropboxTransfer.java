package com.example.cloud.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class DropboxTransfer 
{
	public void DropboxUpload(String path ,DropboxAPI<AndroidAuthSession> mDBApi)
	{
		FileInputStream inputStream = null;
		try {
		    File file=new File(path);
		    inputStream = new FileInputStream(file);
		    Entry newEntry = mDBApi.putFile("/"+file.getName(),inputStream,file.length(), null, null);
		    Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
		   
			}
		catch (DropboxUnlinkedException e)
		{
		    // User has unlinked, ask them to link again here.
		    Log.e("DbExampleLog", "User has unlinked.");
		}
		catch (DropboxException e)
		{
		    Log.e("DbExampleLog", "Something went wrong while uploading.");
		}
		catch (FileNotFoundException e)
		{
		    Log.e("DbExampleLog", "File not found.");
		}
		finally 
		{
		    if (inputStream != null)
		    {
		        try {
		            inputStream.close();
		        }
		        catch (IOException e) {}
		    }
		}
	}
		
	
	
	
	public void DownloadFromDropbox(String DropboxFolderStorage,String path,DropboxAPI<AndroidAuthSession> mDBApi,String service) throws IOException
	{
		
		
		FileOutputStream outputStream = null;
		File file=null;
		String fullpath;
		try {
			if(DropboxFolderStorage.equalsIgnoreCase("/Dropbox"))
		     file = new File(Environment.getExternalStorageDirectory().getPath()+DropboxFolderStorage,service);
			else
				file=new File(Environment.getExternalStorageDirectory().getPath(),service);
		 // if(file.exists())
			//  return;
		  //else
		//  {
			file.createNewFile();
		    outputStream = new FileOutputStream(file);
		    if(path.equalsIgnoreCase("null"))
		    	fullpath="/"+service;
		    else
		    	fullpath=path+"/"+service;
		    
		    
		    DropboxAPI<AndroidAuthSession> mdpai=mDBApi;
		    DropboxFileInfo info = mdpai.getFile(fullpath, null, outputStream, null);
		   
		    Log.i("File",""+info.getContentLength());
		    
		 // }
		 
		} 
		catch (DropboxException e)
		{
		e.printStackTrace();
		}
		
		finally
		{
		    if (outputStream != null)
		    {
		        try 
		        {
		            outputStream.close();
		        } 
		        catch (IOException e) {}
		    }
		}
		
		
		}
	
	public void UploadToDropbox(String filename,DropboxAPI<AndroidAuthSession> mDBApi)
	{
		FileInputStream inputStream = null;
		try {
		    File file = new File(Environment.getExternalStorageDirectory().getPath(),filename);
		    inputStream = new FileInputStream(file);
		    Entry newEntry = mDBApi.putFile("/"+filename,inputStream,file.length(), null, null);
		    Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
		    file.delete();
			}
		catch (DropboxUnlinkedException e)
		{
		    // User has unlinked, ask them to link again here.
		    Log.e("DbExampleLog", "User has unlinked.");
		}
		catch (DropboxException e)
		{
		    Log.e("DbExampleLog", "Something went wrong while uploading.");
		}
		catch (FileNotFoundException e)
		{
		    Log.e("DbExampleLog", "File not found.");
		}
		finally 
		{
		    if (inputStream != null)
		    {
		        try {
		            inputStream.close();
		        }
		        catch (IOException e) {}
		    }
		}
	}
	

}
