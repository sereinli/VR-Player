package me.rain.android.vrvideo.imageselector.view;


import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import me.rain.android.vrvideo.R;

public class ListViewPopupWindow extends PopupWindow {
	
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
		mListView.setOnItemClickListener(listener);
	}
	
	private Context mContext;
	private ListView mListView;

	public ListViewPopupWindow(Context context) {
		super(context);
		mContext = context;
		initViews();
	}
	
	private void initViews() {
		View view = View.inflate(mContext, R.layout.listview_popup_window, null);
		setContentView(view);
		
		mListView = (ListView)view.findViewById(R.id.listview);
		
		setFocusable(true);
        setClippingEnabled(true);
        setAnimationStyle(R.style.popupwindow);
	}
	
	public ListView getListView(){
		return mListView;
	}
	
	public void setAdapter(BaseAdapter adapter){
		mListView.setAdapter(adapter);
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}
}