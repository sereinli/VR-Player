package me.rain.android.vrvideo.imageselector.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.utils.Utils;

public class ImageSelectorItemView extends RelativeLayout {
	private ImageView mCamera;
	private ImageView mCheckMark;
	private String mImageUrl;
	private ImageView mImageView;

	public ImageSelectorItemView(Context context) {
		super(context);
		initViews();
	}
	
	public ImageSelectorItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	public ImageSelectorItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	private void initViews(){
		inflate(getContext(), R.layout.image_selector_item_view, this);
		//相机
		mCamera = (ImageView)findViewById(R.id.camera);
		//本地图片
		int screenW = Utils.getScreenWidth(getContext());
		mCheckMark = (ImageView)findViewById(R.id.checkmark);
		LayoutParams rparam = (LayoutParams)mCheckMark.getLayoutParams();
		rparam.width = (40 * screenW) / 750;
		rparam.height = rparam.width;
		mImageView = (ImageView)findViewById(R.id.image_item);
	}

	public void update(boolean isCamera, boolean showIndicator, boolean checked, String imageUrl){
		if(isCamera){
			mCamera.setVisibility(View.VISIBLE);
			return;
		}
		
		if(showIndicator){
			mCheckMark.setVisibility(View.VISIBLE);
		}else{
			mCheckMark.setVisibility(View.GONE);
		}
		
		if(checked){
			mCheckMark.setImageResource(R.mipmap.image_selector_checked);
		}else{
			mCheckMark.setImageResource(R.mipmap.image_selector_unchecked);
		}
		
		if(TextUtils.isEmpty(imageUrl)){
			return;
		}
		
		mImageUrl = imageUrl;
		Glide.with(getContext())
				.load(mImageUrl)
				.crossFade()
				.into(mImageView);
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
