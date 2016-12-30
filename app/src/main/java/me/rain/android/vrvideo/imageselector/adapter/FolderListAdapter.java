package me.rain.android.vrvideo.imageselector.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import me.rain.android.vrvideo.imageselector.models.Folder;
import me.rain.android.vrvideo.imageselector.view.FolderListItemView;

public class FolderListAdapter extends BaseAdapter {
	private Context mContext;
    private List<Folder> mFolders;
    int lastSelected = 0;
    
    public FolderListAdapter(Context context){
    	mContext = context;
    	mFolders = new ArrayList<Folder>();
    }

    public void setData(List<Folder> folders) {
        if(folders != null && folders.size()>0){
            mFolders = folders;
        }else{
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
    	if(mFolders == null){
    		return 0;
    	}
        return mFolders.size();
    }

    @Override
    public Folder getItem(int i) {
        if(mFolders == null || mFolders.size() == 0){
        	return null;
        }
        return mFolders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = new FolderListItemView(mContext);
        }
        
//        if(i == 0){
//        	String name = mContext.getResources().getString(R.string.all_pictures);
//        	String path = "";
//        	if(mFolders.size() > 0){
//        		path = mFolders.get(0).cover.path;
//        	}
//        	((FolderListItemView)view).update(name, getTotalImageSize(), path, lastSelected == i);
//        }else{
        	((FolderListItemView)view).update(getItem(i).name, getItem(i).images.size(), getItem(i).cover.path, lastSelected == i);
//        }
        
        return view;
    }

    private int getTotalImageSize(){
        int result = 0;
        if(mFolders != null && mFolders.size() > 0){
            for (Folder f: mFolders){
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if(lastSelected == i){
        	return;
        }

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex(){
        return lastSelected;
    }
}
