package me.rain.android.vrvideo.imageselector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.activity.BaseFragmentActivity;

public class MultiImageSelectorActivity extends BaseFragmentActivity implements MultiImageSelectorFragment.Callback{
    private ArrayList<String> resultList = new ArrayList<String>();
    private int mDefaultCount;
    private int mMode = Constant.MODE_MULTI;

    private final int REQUEST_CODE_CUT_PICTURE = 1002;	//图片裁剪功能

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_image_selector);
        
        Window window = this.getWindow();
		window.setWindowAnimations(R.style.DialogAnimation);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(Constant.EXTRA_SELECT_COUNT, 6);
        mMode = intent.getIntExtra(Constant.EXTRA_SELECT_MODE, Constant.MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(Constant.EXTRA_SHOW_CAMERA, true);
        if(mMode == Constant.MODE_MULTI && intent.hasExtra(Constant.EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(Constant.EXTRA_DEFAULT_SELECTED_LIST);
        }
		if (resultList == null){
			resultList = new ArrayList<String>();
		}
		Bundle bundle = new Bundle();
        bundle.putInt(Constant.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(Constant.EXTRA_SELECT_MODE, mMode);
        bundle.putBoolean(Constant.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(Constant.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

    }

    
    @Override
    public void onSingleImageSelected(String path) {
    	if(!TextUtils.isEmpty(path)){
            Intent intent = new Intent();
            resultList.add(path);
            intent.putStringArrayListExtra(Constant.EXTRA_RESULT, resultList);
            setResult(RESULT_OK, intent);
            finish();
		}
    }

    @Override
    public void onImageSelected(String path) {
        if(resultList != null && !resultList.contains(path)) {
            resultList.add(path);
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList != null && resultList.contains(path)){
            resultList.remove(path);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {
        	if(mMode == Constant.MODE_MULTI){
        		//多图模式下，拍照后不需要裁切
        		Intent data = new Intent();
        		resultList.add(imageFile.getAbsolutePath());
        		data.putStringArrayListExtra(Constant.EXTRA_RESULT, resultList);
        		setResult(RESULT_OK, data);
        		finish();
        		return;
        	}


        }
    }

	@Override
	public void onComplete() {
		if(resultList != null && resultList.size() >0){
            // 返回已选择的图片数据
            Intent data = new Intent();
            data.putStringArrayListExtra(Constant.EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
	}

	@Override
	public void onCancel() {
		setResult(RESULT_CANCELED);
        finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}
}