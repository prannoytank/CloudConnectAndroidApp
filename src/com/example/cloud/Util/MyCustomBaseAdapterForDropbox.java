package com.example.cloud.Util;

import java.util.ArrayList;

import com.example.cloud.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomBaseAdapterForDropbox extends BaseAdapter 
{
	private static ArrayList<DropboxDataBean> searchArrayList;
	
	private LayoutInflater mInflater;

	public MyCustomBaseAdapterForDropbox(Context context, ArrayList<DropboxDataBean> searchResults) 
	{
		searchArrayList = searchResults;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return searchArrayList.size();
	}

	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.custom_row_view, null);
			holder = new ViewHolder();
			holder.txtFileName = (TextView) convertView.findViewById(R.id.filename);
			holder.txtIsFolder = (TextView) convertView.findViewById(R.id.isfolder);
			holder.txtFolderPath = (TextView) convertView.findViewById(R.id.filepath);
			holder.txtFileId=(TextView)convertView.findViewById(R.id.file_id);
			holder.iv=(ImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtFileName.setText(searchArrayList.get(position).getFilename());
		holder.txtIsFolder.setText(searchArrayList.get(position).getIsfolder());
		holder.txtFolderPath.setText(searchArrayList.get(position).getFilepath());
		holder.txtFileId.setId(searchArrayList.get(position).getFileid());
		
		if(searchArrayList.get(position).getIsfolder().equalsIgnoreCase("Folder") )
		{
			
			holder.iv.setVisibility(View.VISIBLE);
            holder.iv.setImageResource(R.drawable.folder_icon);		
		}	
			else
			holder.iv.setImageResource(R.drawable.fileicon);
		
		
		return convertView;
	}

	static class ViewHolder
	{
		TextView txtFileName;
		TextView txtIsFolder;
		TextView txtFolderPath;
		TextView txtFileId;
		ImageView iv;
	}
}
