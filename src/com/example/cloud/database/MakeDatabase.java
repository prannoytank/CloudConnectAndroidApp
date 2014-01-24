package com.example.cloud.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MakeDatabase extends SQLiteOpenHelper{
	//The Android's default system path of your application database.
    String DB_PATH =null;
 
    private static String DB_NAME = "CloudFile1.db";
 
    private SQLiteDatabase myDataBase; 
    
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public MakeDatabase(Context context) {
 
    	super(context,DB_NAME,null,2);
       this.myContext = context;
    	 DB_PATH="/data/data/"+context.getPackageName()+"/"+"databases/";
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException
    {
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist)
    	{
    		Log.i("Database","exist");
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase()
    {
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
    		System.out.println("******************************Db does not exist***************************************");
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    public void copyDataBase() throws IOException
    {
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    	Log.i("Database","Database copied success");
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
    
	
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	
	public void onUpgrade1(MakeDatabase db1) 
	{
		 db1.execSQL("ALTER TABLE SkydriveMaster ADD COLUMN FileId VARCHAR ");
 
	}
 private void execSQL(String string)
 {
		// TODO Auto-generated method stub
		
	}

	//return cursor
	public Cursor query(String table,String[] columns, String selection,String[] selectionArgs,String groupBy,String having,String orderBy){
		return myDataBase.query("EMP_TABLE", null, null, null, null, null, null);
	
	
	}
	public void insertSkydriveData(int id,String filename,String folderId,boolean isFolder)
	{
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_id",id);
		values.put("FileFolderName",filename);
		values.put("FolderId",folderId);
		values.put("isFolder",isFolder);
		
		database.insert("SkydriveMaster", null, values);
		database.close();
		
	}
	
	public void insertDropboxData(int id,String filename,String folderId,boolean isFolder)
	{
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("_id",id);
		values.put("ParentFolder",filename);
		values.put("FileName",folderId);
		values.put("isDirectory",isFolder);
		
		database.insert("DropboxMaster", null, values);
		database.close();
		
	}
	
	
	
	public void delete()
	{
		SQLiteDatabase database = this.getWritableDatabase();	 
		String deleteQuery = "delete  from  SkydriveMaster";
		Log.d("query",deleteQuery);		
		database.execSQL(deleteQuery);
		database.close();
	}
	
	public void dropboxdelete()
	{
		SQLiteDatabase database = this.getWritableDatabase();	 
		String deleteQuery = "delete  from  DropboxMaster";
		Log.d("query",deleteQuery);		
		database.execSQL(deleteQuery);
		database.close();
	}
	
	
	
	public ArrayList<String> getSkydriveList()
	{
		ArrayList<String> wordList = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getString(2));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }
	database.close();
	return wordList;
	}
	
	
	public ArrayList<String> getFilePath()
	{
		ArrayList<String> wordList = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getString(3));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }
	database.close();
	return wordList;
	}
	
	public ArrayList<String> getSkydriveIsFolder()
	{
		ArrayList<String> wordList = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getString(4));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }	
	database.close();
	return wordList;
	}
	
	public ArrayList<Integer>  getSkydriveIsfileid()
	{
		ArrayList<Integer> wordList = new ArrayList<Integer>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM SkydriveMaster where FolderId='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getInt(1));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }			
	database.close();
	return wordList;
	}
	
	public ArrayList<String> getDropboxList()
	{
		ArrayList<String> DropboxList = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	DropboxList.add(cursor.getString(3));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }				 
		database.close();
	return DropboxList;
	}
	
	public ArrayList<String> getDropboxFolderPath()
	{
		ArrayList<String> wordList = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM  DropboxMaster where ParentFolder='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getString(0));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }				 
		database.close();
	return wordList;
	}
	
	public ArrayList<String> getDropboxIsFolder()
	{
		ArrayList<String> wordList = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getString(4));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }		
		database.close();
	return wordList;
	}
	
	public ArrayList<Integer>  getDropboxfileid()
	{
		ArrayList<Integer> wordList = new ArrayList<Integer>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM DropboxMaster where ParentFolder='0'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.add(cursor.getInt(1));
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }
		database.close();
	return wordList;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
}