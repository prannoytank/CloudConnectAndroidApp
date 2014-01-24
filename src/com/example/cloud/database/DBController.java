package com.example.cloud.database;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.cloud.Util.DropboxDataBean;
import com.example.cloud.Util.SearchResults;

import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBController extends SQLiteOpenHelper 
{
	private static final String LOGCAT = null;

	public DBController(Context applicationcontext) {
		super(applicationcontext, "CloudFile1.db", null, 2);
		Log.d(LOGCAT, "Created");
	}

	 public void onUpgrade1(SQLiteDatabase db)
	 {

	     // If you need to add a column
	     
	         db.execSQL("ALTER TABLE SkydriveMaster ADD COLUMN  INTEGER DEFAULT 0");
	     
	 }
	
	
	
	

	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old,
			int current_version)
	{
		String query;
		query = "DROP TABLE IF EXISTS animals";
		database.execSQL(query);
		onCreate(database);
	}

	

	

	public void add()
	{
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "ALTER TABLE SkydriveMaster ADD COLUMN FileId VARCHAR ";
		database.execSQL(deleteQuery);
		Log.i("added","Coloum added");
	}
	
	public void delete_sky()
	{
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "delete from SkydriveMaster";
		database.execSQL(deleteQuery);
		Log.i("delete","Skydrive Deleted");
		
	}
	
	
	
	
	

	public void insertSkydriveData(int id, String filename, String folderpath,int folderid, String isFolder,String Fileid)
	{
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_id", id);
		values.put("FileFolderName", filename);
		values.put("FolderPath", folderpath);
		values.put("FolderId", folderid);
		values.put("isFolder", isFolder);
		values.put("FileId",Fileid);
	    database.insert("SkydriveMaster", null, values);
		database.close();

	}

	public void insertDropboxData(int id, String filename, int parentId,String folderpath, String isFolder)
	{
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_id", id);
		values.put("ParentFolder", parentId);
		values.put("FileName", filename);
		values.put("isDirectory", isFolder);
		values.put("FolderPath", folderpath);
        database.insert("DropboxMaster", null, values);
		database.close();

	}
	
	public ArrayList<String> getSkydriveFolderData(int sky_id)
	{
		ArrayList<String> sky_folder_List = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId="+sky_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			sky_folder_List.add(cursor.getString(2));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_folder_List;
	}
	
	public ArrayList<String> getSkydriveFolderisFolder(int sky_id)
	{
		ArrayList<String> sky_folder_isfolder = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId="+sky_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			sky_folder_isfolder.add(cursor.getString(4));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_folder_isfolder;
	}
	
	public ArrayList<Integer> getSkydriveId(int sky_id)
	{
		ArrayList<Integer> sky_folder_id = new ArrayList<Integer>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId="+sky_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			sky_folder_id.add(cursor.getInt(1));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_folder_id;
	}
	
	public ArrayList<String> getSkydriveFolderPath(int sky_id)
	{
		ArrayList<String> sky_folder_path = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId="+sky_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			sky_folder_path.add(cursor.getString(3));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_folder_path;
	}
	
	
	
	
	
	
	public ArrayList<String> getDropboxFolderData(int dropbox_id)
	{
		ArrayList<String> dropbox_folder_List = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder="+dropbox_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			dropbox_folder_List.add(cursor.getString(3));
			}
		while(cursor.moveToNext());
		}
		database.close();
		return dropbox_folder_List;
	}
	
	public ArrayList<String> getDropboxFolderisFolder(int dbox_id)
	{
		ArrayList<String> dbox_folder_isfolder = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder="+dbox_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			dbox_folder_isfolder.add(cursor.getString(4));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return dbox_folder_isfolder;
	}
	
	public ArrayList<Integer> getDropboxId(int dbox_id)
	{
		ArrayList<Integer> dbox_folder_id = new ArrayList<Integer>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder="+dbox_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			dbox_folder_id.add(cursor.getInt(1));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return dbox_folder_id;
	}
	
	public ArrayList<String> getDropboxFolderPath(int dbox_id)
	{
		ArrayList<String> dbox_folder_path = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder="+dbox_id;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			dbox_folder_path.add(cursor.getString(0));
			
		}
		while(cursor.moveToNext());
		}
		database.close();
		return dbox_folder_path;
	}
	
	public ArrayList<SearchResults> getSearchDataFromSkydrive(String filename)
	{
		ArrayList<SearchResults> sky_data1=new ArrayList<SearchResults>();
		SQLiteDatabase database = this.getReadableDatabase();
		
		SearchResults rs=null;
		String selectQuery = "SELECT * FROM SkydriveMaster where isFolder='false' and FileFolderName like '"+filename+"%'" ;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			rs=new SearchResults();
			rs.setFileid(cursor.getInt(1));
			rs.setFilename(cursor.getString(2));
			//rs.setPath(cursor.getString(2));
			rs.setService("Skydrive");
			sky_data1.add(rs);
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_data1;
		
		
		
		
		
	}
	
	public ArrayList<SearchResults> getSearchDataFromSkydrive1()
	{
		ArrayList<SearchResults> sky_data1=new ArrayList<SearchResults>();
		SQLiteDatabase database = this.getReadableDatabase();
		
		SearchResults rs=null;
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			rs=new SearchResults();
			rs.setFileid(cursor.getInt(1));
			rs.setFilename(cursor.getString(2));
			rs.setPath(cursor.getString(3));
			rs.setIsFolder(cursor.getString(4));
			//rs.setService("Skydrive");
			sky_data1.add(rs);
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_data1;
		
		
		
		
		
	}
	
	
	
	public ArrayList<SearchResults> getSearchDataFromDropbox(String filename)
	{
		ArrayList<SearchResults> sky_data1=new ArrayList<SearchResults>();
		SQLiteDatabase database = this.getReadableDatabase();
		
		SearchResults rs=null;
		String selectQuery = "SELECT * FROM DropboxMaster where isDirectory='false' and FileName like '"+filename+"%'" ;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			rs=new SearchResults();
			rs.setFileid(cursor.getInt(1));
			rs.setFilename(cursor.getString(3));
			//rs.setPath(cursor.getString(2));
			rs.setService("Dropbox");
			sky_data1.add(rs);
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_data1;
		
		
		
		
		
	}
	public ArrayList<DropboxDataBean> getSearchDataFromDropboxIndividual(String filename)
	{
		ArrayList<DropboxDataBean> sky_data1=new ArrayList<DropboxDataBean>();
		SQLiteDatabase database = this.getReadableDatabase();
		
		DropboxDataBean rs=null;
		String selectQuery = "SELECT * FROM DropboxMaster where isDirectory='false' and FileName like '"+filename+"%'" ;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			rs=new DropboxDataBean();
			rs.setFileid(cursor.getInt(1));
			rs.setFilename(cursor.getString(3));
			//rs.setPath(cursor.getString(2));
			rs.setService("Dropbox");
			sky_data1.add(rs);
		}
		while(cursor.moveToNext());
		}
		database.close();
		return sky_data1;
		
		
		
		
		
	}
	
	
	public String getDropboxFilePath(int dropboxId) 
	{
		String filepath="";
		SQLiteDatabase database = this.getReadableDatabase();
		String n="null";
		String selectQuery = "SELECT FolderPath FROM DropboxMaster where  _id=(Select ParentFolder from DropboxMaster where _id ='"+dropboxId+"')";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			
			filepath=cursor.getString(0);
			System.out.println(filepath);
		}
		while(cursor.moveToNext());
		}
		database.close();
		
		
		return filepath;
		
		
	}
	
	public String getSkydriveFilePath(int skyId) 
	{
		String filepath="";
		SQLiteDatabase database = this.getReadableDatabase();
		//String n="null";
		String selectQuery = "SELECT FileId FROM SkydriveMaster where  _id='"+skyId+"'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
		do
		{
			
			filepath=cursor.getString(0);
			System.out.println(filepath);
		}
		while(cursor.moveToNext());
		}
		database.close();
		
		
		return filepath;
		
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
