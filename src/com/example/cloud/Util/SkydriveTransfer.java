package com.example.cloud.Util;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Environment;
import android.util.Log;

import com.example.cloud.CentralizedSearch;
import com.example.cloud.LiveSdkSampleApplication;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveDownloadOperation;
import com.microsoft.live.LiveDownloadOperationListener;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveUploadOperationListener;

public class SkydriveTransfer 
{
	
	
	public void downloadFromSkydrive(String storagepath,String folderpath,String filename,LiveConnectClient client) throws IOException
	{
		File file=null;
		try
		
		{
		//LiveSdkSampleApplication app =new LiveSdkSampleApplication(); 
	    //LiveConnectClient mClient = app.getConnectClient();
	   // Log.i("ClientId",mClient.toString());
		if(storagepath.equalsIgnoreCase("/Skydrive"))
		file=new File(Environment.getExternalStorageDirectory().getPath()+storagepath,filename);
		else
		file=new File(Environment.getExternalStorageDirectory().getPath(),filename);	
		file.createNewFile();
		//folderpath=folderpath+"/"+filename;
		Log.i("FilePath",""+folderpath);
		
		client.downloadAsync(folderpath+"/content",file,new LiveDownloadOperationListener()
		{
        public void onDownloadProgress(int totalBytes,int bytesRemaining,LiveDownloadOperation operation)
        {
        int percentCompleted =computePrecentCompleted(totalBytes, bytesRemaining);

       // progressDialog.setProgress(percentCompleted);
    }

        public void onDownloadFailed(LiveOperationException exception,LiveDownloadOperation operation) {
      //  progressDialog.dismiss();
      //showToast(exception.getMessage());
   }

    public void onDownloadCompleted(LiveDownloadOperation operation)
    {
    	Log.i("Download","Compelete");
     //progressDialog.dismiss();
    // showToast("File downloaded.");
     }
    });
		}
		catch(Exception e)
		{
			e.getMessage();
			e.printStackTrace();
		}
		
		
	}
	
	public void SkydriveUpload(final String path,final LiveConnectClient client)
	{
		 
		        	/*final ProgressDialog uploadProgressDialog =new ProgressDialog(CentralizedSearch.this);
	                uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	                uploadProgressDialog.setMessage("Uploading...");
	                uploadProgressDialog.setCancelable(true);
	                uploadProgressDialog.show();*/
		        	
		        	
		        	final File f=new File(path);
		        	client.uploadAsync("me/skydrive",f.getName(),f,new LiveUploadOperationListener()
		        	{

						@Override
						public void onUploadCompleted(LiveOperation operation) 
						{
							Log.i("Upload","Successful");
							f.delete();
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onUploadFailed(LiveOperationException exception,LiveOperation operation) 
						{
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onUploadProgress(int totalBytes,int bytesRemaining, LiveOperation operation) 
						{
							int percentCompleted = computePrecentCompleted(totalBytes, bytesRemaining);
							//uploadProgressDialog.setProgress(percentCompleted);
						}
		        		
		        	});
		        	
		        }
		        
		
	
	
	
	



	
	
	
	public void UploadToSkydrive(final String service,final LiveConnectClient client)
	{
		 
		        	/*final ProgressDialog uploadProgressDialog =new ProgressDialog(CentralizedSearch.this);
	                uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	                uploadProgressDialog.setMessage("Uploading...");
	                uploadProgressDialog.setCancelable(true);
	                uploadProgressDialog.show();*/
		        	
		        	
		        	final File f=new File(Environment.getExternalStorageDirectory().getPath(),service);
		        	client.uploadAsync("me/skydrive",service,f,new LiveUploadOperationListener()
		        	{

						@Override
						public void onUploadCompleted(LiveOperation operation) 
						{
							Log.i("Upload","Successful");
							f.delete();
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onUploadFailed(LiveOperationException exception,LiveOperation operation) 
						{
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onUploadProgress(int totalBytes,int bytesRemaining, LiveOperation operation) 
						{
							int percentCompleted = computePrecentCompleted(totalBytes, bytesRemaining);
							//uploadProgressDialog.setProgress(percentCompleted);
						}
		        		
		        	});
		        	
		        }
		        
		
	
	
	
	private int computePrecentCompleted(int totalBytes, int bytesRemaining) 
	{
        return (int) (((float)(totalBytes - bytesRemaining)) / totalBytes * 100);
    }
	

}
