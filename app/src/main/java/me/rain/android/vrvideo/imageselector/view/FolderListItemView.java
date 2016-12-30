package me.rain.android.vrvideo.imageselector.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.utils.Utils;

public class FolderListItemView extends RelativeLayout {
	private ImageView mCheckMark;
	private ImageView mRightArrow;
	private String mImageUrl;
	private ImageView mImageView;
	private TextView mFolderName;
	private TextView mImagesNum;

	public FolderListItemView(Context context) {
		super(context);
		initViews();
	}
	
	public FolderListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	public FolderListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	private void initViews(){
		inflate(getContext(), R.layout.image_selector_folder_list_item_view, this);

		mCheckMark = (ImageView)findViewById(R.id.iv_indicator);
		mRightArrow = (ImageView)findViewById(R.id.iv_right_arrow);
		mFolderName = (TextView)findViewById(R.id.folder_name);
		mImagesNum = (TextView)findViewById(R.id.image_num);
		mImageView = (ImageView)findViewById(R.id.image);
		
		int screenW = Utils.getScreenWidth(getContext());
		LayoutParams rparam = (LayoutParams)mImageView.getLayoutParams();
		rparam.width = (120 * screenW) / 750;
		rparam.height = rparam.width;

		rparam = (LayoutParams)mCheckMark.getLayoutParams();
		rparam.width = (26 * screenW) / 750;
		rparam.height = (20 * screenW) / 750;

		rparam = (LayoutParams)mRightArrow.getLayoutParams();
		rparam.width = (16 * screenW) / 750;
		rparam.height = (26 * screenW) / 750;
	}

	public void update(String title, int num, String imagePath, boolean marked){
		if(marked){
			mCheckMark.setVisibility(View.VISIBLE);
		}else{
			mCheckMark.setVisibility(View.GONE);
		}
		
		mFolderName.setText(title);
		mImagesNum.setText(num + "");
		if(num > 0){
			mRightArrow.setVisibility(View.VISIBLE);
		}else{
			mRightArrow.setVisibility(View.GONE);
		}
		
		if(TextUtils.isEmpty(imagePath)){
			return;
		}
		
		mImageUrl = imagePath;
		Glide.with(getContext())
				.load(mImageUrl)
				.into(mImageView);
	}
}
