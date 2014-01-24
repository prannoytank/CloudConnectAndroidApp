package com.example.cloud;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.example.cloud.Util.DropboxBean;
import com.example.cloud.Util.LiveConnectHandler;
import com.example.cloud.Util.SearchResults;
import com.example.cloud.Util.SkydriveBean;
import com.example.cloud.database.DBController;
import com.example.cloud.database.DataBaseHelper;
import com.example.cloud.database.MakeDatabase;
import com.google.gson.Gson;





import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;


import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;



public class button_selection extends Activity implements View.OnClickListener,LiveAuthListener

{
	Button google, microsoft, dropbox, dropboxlogout,dropboxfolder,refreshlist,skydrivelist,skydrivefolder,centralsearch;
	TextView t, txtgoogle, txtdropbox, txtskydrive;
	//Toast to;
	
	
	/** dropbox variables **/
	DropboxAPI<AndroidAuthSession> mDBApi;
	final static private String APP_KEY = "qwhxbi7bea749s3";
	final static private String APP_SECRET = "8iuupmixtus726i";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	static ArrayList<String> dboxfilelist=new ArrayList<String>();
	static ArrayList<String> dbox_filepath=new ArrayList<String>();
	static ArrayList<String> dbox_isfolder=new ArrayList<String>();
	static ArrayList<Integer> dbox_fileid=new ArrayList<Integer>();
	
	static ArrayList<SearchResults> sr=new ArrayList<SearchResults>();
	
	
	static ArrayList<String> skydrive_folder=new ArrayList<String>();
	static ArrayList<String> skydrive_filepath=new ArrayList<String>();
	static ArrayList<String> skydrive_isfolder=new ArrayList<String>();
	static ArrayList<Integer> skydrive_fileid=new ArrayList<Integer>();
	DropboxBean db;
	boolean isDropboxConnected=false;
	
	private ProgressDialog mInitializeDialog;
	
	LiveSdkSampleApplication app = new LiveSdkSampleApplication();
	
	/** skydrive variables **/
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private Account saveaccount;
	private LiveAuthClient auth;
	private LiveConnectClient client;
	private static final String HOME_FOLDER = "me/skydrive";
	private LiveSdkSampleApplication mApp;
	String mCurrentFolderId;
	SkydriveBean skb;
	
	int j;
	// private LiveConnectSession session1;
	int file_id=0;
	boolean isSkydriveConnected=false;
	boolean isDboxConnected=false;
	int dropbox_file_id=0;
	DBController controller = new DBController(this);
	
	//DataBaseHelper dbh=new DataBaseHelper(this);
	

	public void intializeWidgets() 
	{
		dropbox = (Button) findViewById(R.id.Dropbox);
		dropbox.setTextColor(Color.BLUE);

		refreshlist=(Button)findViewById(R.id.refreshfilelist);
		microsoft = (Button) findViewById(R.id.Skydrive);
		microsoft.setTextColor(Color.BLUE);

		centralsearch=(Button)findViewById(R.id.CentralSearch);

		dropboxlogout = (Button)findViewById(R.id.Logout);
		dropboxlogout.setVisibility(View.GONE);
		// dropboxlogout.setEnabled(false);
		t = (TextView) findViewById(R.id.textView1);
		
		// txtdropbox=(TextView)findViewById(R.id.txtdropbox);
		skydrivelist=(Button)findViewById(R.id.skydrivelist);
		skydrivefolder=(Button)findViewById(R.id.SkydriveFolder);
		//skydrivefolder.setEnabled(false);
		txtskydrive = (TextView) findViewById(R.id.txtskydrive);
		
		dropboxfolder=(Button)findViewById(R.id.DropboxFolder);
	}

	@Override
	protected void onCreate(Bundle button_selection) 
	{
		super.onCreate(button_selection);
		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		
		
		
		MakeDatabase db1=new MakeDatabase(this);
		//db1.onUpgrade1(db1);
		
		//db1.delete();
		try {
			db1.createDataBase();
			//Log.i("Database","Database copied success");
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		db=new DropboxBean();

		setContentView(R.layout.cloud_selection);
		this.auth = new LiveAuthClient(this,Config.CLIENT_ID);
		// session1= new LiveConnectSession(this.auth);

		intializeWidgets();
		
		
		
		
		dropbox.setOnClickListener(this);
		microsoft.setOnClickListener(this);
		dropboxlogout.setOnClickListener(this);
		dropboxfolder.setOnClickListener(this);
		refreshlist.setOnClickListener(this);
		skydrivelist.setOnClickListener(this);
		skydrivefolder.setOnClickListener(this);
		centralsearch.setOnClickListener(this);
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		if(isDropboxConnected)
			return;
		else
		{
		
		AndroidAuthSession session = buildSession();
		db.setSess(session);
		if (session.authenticationSuccessful()) 
		{
			try {
				
				

				// MANDATORY call to complete auth.
				// Sets the access token on the session
				session.finishAuthentication();
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				
				db.setmDBApi(mDBApi);
				
				//txtdropbox.setText("Connected");
				dropboxlogout.setVisibility(View.VISIBLE);
				//Toast.makeText(button_selection.this, dboxfilelist.size(), Toast.LENGTH_LONG).show();
				// dropbox.setEnabled(true);
				isDropboxConnected=true;
				
				File dboxfolder=new File(Environment.getExternalStorageDirectory().getPath(),"Dropbox");
				if(dboxfolder.exists())
				{
					
				}
				else
				dboxfolder.mkdir();
				

			} 
			catch (Exception e) 
			{
				System.out.print(e);
				Log.i("button_selection", "Error authenticating", e);
			}
		}
	}
		// ...
	}

	private void listFilesForFolder(Entry list,int fileid) throws DropboxException
	{
		
		final int parentId =fileid;
		for (Entry ent : list.contents) 
		{
			if (ent.isDir) 
			{
				
				Entry list1 = mDBApi.metadata(ent.path,1000, null,true,null);
				controller.insertDropboxData(++dropbox_file_id,ent.fileName(), parentId,ent.path,"true");
				listFilesForFolder(list1,dropbox_file_id);
				
			}
			else 
			{
				controller.insertDropboxData(++dropbox_file_id,ent.fileName(), parentId, "null","false");
				Log.i("List", ent.fileName());
			}
		}

	}

	/* storing dropbox keys in shared preferences */
	public void storeKeys(String key, String secret) 
	{
		SharedPreferences prefs1 = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs1.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	// * Creating new session or getting already stored keys and secret *//
	private AndroidAuthSession buildSession()
	{
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null)
		{
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,accessToken);
		}
		else 
		{
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	/* getting dropbox stored keys from shared preferences */
	private String[] getKeys()
	{
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null)
		{
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		}
		else
		{
			return null;
		}
	}
	
	private class MyAsyncTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			//dboxfilelist.clear();
			Entry list = null;
			try {
				
				list = mDBApi.metadata("/",1000, null, true, null);
				listFilesForFolder(list,dropbox_file_id);
				
				
				
			} 
			catch (DropboxException e) 
			{
				
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	public class skydrive_list extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			skydrive_folder.clear();
			getFolder(HOME_FOLDER,0);
			return null;
		}
		
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.CentralSearch:
			Intent search=new Intent("com.example.cloud.CentralizedSearch");
			search.putExtra("isSkydriveConnectd",isSkydriveConnected);
			search.putExtra("isDropboxConnected",isDropboxConnected);
			startActivity(search);
			
			break;
		
		case R.id.SkydriveFolder:
			
			MakeDatabase db1=new MakeDatabase(this);
			skydrive_folder=db1.getSkydriveList();
			skydrive_filepath=db1.getFilePath();
			skydrive_isfolder=db1.getSkydriveIsFolder();
			skydrive_fileid=db1.getSkydriveIsfileid();
			//sr=controller.getSearchDataFromSkydrive1();
			Intent i=new Intent("com.example.cloud.SkydriveFolderList");
		//	i.putExtra("skyObject",skb);
			i.putStringArrayListExtra("FileList",skydrive_folder);
			i.putStringArrayListExtra("FilePath",skydrive_filepath);
			i.putStringArrayListExtra("isFolder",skydrive_isfolder);
			i.putIntegerArrayListExtra("FileId",skydrive_fileid);
			i.putExtra("isSkydriveConnected",isSkydriveConnected);
			i.putExtra("SkyDriveList",sr);
			//i.putStringArrayListExtra("FileList",skydrive_folder);
			startActivity(i);
			
			break;
		case R.id.skydrivelist:
			if(!isSkydriveConnected)
				Toast.makeText(getApplicationContext(), "Please First Connect to Skydrive",Toast.LENGTH_LONG).show();
			else
			{
			AlertDialog.Builder skydrivelist = new AlertDialog.Builder(button_selection.this);
			skydrivelist.setTitle("Skydrive File list");
			skydrivelist.setMessage("Click YES To Refresh Skydrive List! Warning:All the previous data will be deleted").setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener()
			{
					public void onClick(DialogInterface dialog,int id) 
					{
						
						new skydrive_list().execute();
						
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) 
					{
						
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog skydriveDialog = skydrivelist.create();
 
				// show it
				skydriveDialog.show();
			}
			break;
		
		
		case R.id.refreshfilelist:
			if(!isDropboxConnected)
				Toast.makeText(getApplicationContext(), "Please First Connect to Dropbox",Toast.LENGTH_LONG).show();
			else
			{
			AlertDialog.Builder dropboxlist = new AlertDialog.Builder(button_selection.this);
			dropboxlist.setTitle("Dropbox File list");
			dropboxlist.setMessage("Click YES To Refresh the Dropbox List! Warning:All the previous data will be deleted").setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener()
			{
					public void onClick(DialogInterface dialog,int id) 
					{
						new MyAsyncTask().execute();
						
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) 
					{
						
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog dropboxDialog = dropboxlist.create();
 
				// show it
				dropboxDialog.show();
			}
			break;
		
		case R.id.DropboxFolder:
			
			//dboxfilelist.clear();
			MakeDatabase db2=new MakeDatabase(this);
			dboxfilelist=db2.getDropboxList();
			dbox_filepath=db2.getDropboxFolderPath();
			dbox_isfolder=db2.getDropboxIsFolder();
			dbox_fileid=db2.getDropboxfileid();
			
		Intent dropbox=new Intent("com.example.cloud.DropboxFolderList");
		dropbox.putExtra("isDropboxConnected",isDropboxConnected);	
		dropbox.putStringArrayListExtra("DropboxList",dboxfilelist);
		dropbox.putStringArrayListExtra("DropboxPath",dbox_filepath);
		dropbox.putStringArrayListExtra("DropboxIsFolder",dbox_isfolder);
		dropbox.putIntegerArrayListExtra("DropboxFileId",dbox_fileid);
		
		startActivity(dropbox);
		
		break;
		case R.id.Logout:
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(button_selection.this);
	 
				// set title
				alertDialogBuilder.setTitle("Dropbox Logout");
	 
				// set dialog message
				alertDialogBuilder.setMessage("Click YES to Logout!").setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener()
				{
						public void onClick(DialogInterface dialog,int id) 
						{
							
							logOut();
							dropboxlogout.setVisibility(View.GONE);
							dboxfilelist.clear();
							isDropboxConnected=false;
						}
					  })
					.setNegativeButton("No",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) 
						{
							
							dialog.cancel();
						}
					});
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
				break;

		case R.id.Dropbox:
			mDBApi.getSession().startAuthentication(button_selection.this);

			break;
		case R.id.Skydrive:

			
			auth.login(button_selection.this, Arrays.asList(Config.SCOPES), this);
			
			break;
		
		}

	}

	

	
	@Override
	public void onAuthComplete(LiveStatus status, LiveConnectSession session,Object userState)
	{
		if (status == LiveStatus.CONNECTED)
		{

			client = new LiveConnectClient(session);
			
			LiveConnectHandler.client=client;
			
			
			
			Log.i("message", client.toString());
			
			txtskydrive.setText("Connected");
			skydrivefolder.setEnabled(true);
			isSkydriveConnected=true;
			
			File skydrivefolder=new File(Environment.getExternalStorageDirectory().getPath(),"Skydrive");
			if(skydrivefolder.exists())
			{
				
			}
			else
			skydrivefolder.mkdir();
		} 
		else
		{
			txtskydrive.setText("Not Connected");
			Toast.makeText(getApplicationContext(), "Not Signed In",Toast.LENGTH_LONG).show();
			// this.resultTextView.setText("Not signed in.");
			client = null;
		}
	}

	

	@Override
	public void onAuthError(LiveAuthException exception, Object userState) 
	{
		Log.i("Error", exception.getMessage());
		
	}

	
	private void logOut()
	{
		// Remove keys from the session
		mDBApi.getSession().unlink();
		db.setmDBApi(null);

		// Clear our stored keys
		clearKeys();
		
	}

	private void clearKeys()
	{
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}
	
	
	public void getFolder(String folderId,final int id)
	{
		 	assert folderId != null;
	        mCurrentFolderId = folderId;
	        folderId=folderId+"/files";
	        
	        final int parent_id =id;
	       
	        
		client.getAsync(folderId,new LiveOperationListener()
        {
			int i;
			@Override
			public void onComplete(LiveOperation operation)
			{
				org.json.JSONObject result = operation.getResult();
				
				JSONArray data = result.optJSONArray("data");
				
				
				
				i=0;
				
				for(i=0;i<data.length();i++)
				{
					try {
						
						org.json.JSONObject data1=data.optJSONObject(i);
						Log.i("Type:",data1.getString("type"));
						if(data1.getString("type").equalsIgnoreCase("folder"))
						{
							controller.insertSkydriveData(++file_id,data1.getString("name"),data1.getString("id"),parent_id,"true",data1.getString("id"));
							
							
						
							Log.i("ParentIdForFolder",""+parent_id);
							getFolder(data1.getString("id"),file_id);
							
							
							
						}
						else
						{
							
							
							Log.i("ParentIdForFile",""+parent_id);
							
							controller.insertSkydriveData(++file_id,data1.getString("name"),"null",parent_id,"false",data1.getString("id"));
						
						}
					}
					catch (JSONException e) 
					
					{
						
						Log.i("Json Error",e.getMessage());
						e.printStackTrace();
					}
				}
				
			}

			@Override
			public void onError(LiveOperationException exception,
					LiveOperation operation) 
			{
				Log.i("Skydrive Folder Error",exception.getMessage());
				
			}
        });
	}

	
}
