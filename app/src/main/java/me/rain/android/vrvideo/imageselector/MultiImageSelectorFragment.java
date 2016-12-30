package me.rain.android.vrvideo.imageselector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.activity.BaseActivity;
import me.rain.android.vrvideo.imageselector.adapter.FolderListAdapter;
import me.rain.android.vrvideo.imageselector.adapter.PictureAdapter;
import me.rain.android.vrvideo.imageselector.models.Folder;
import me.rain.android.vrvideo.imageselector.models.Image;
import me.rain.android.vrvideo.imageselector.view.ListViewPopupWindow;
import me.rain.android.vrvideo.utils.FileUtils;
import me.rain.android.vrvideo.utils.Utils;
import me.rain.android.vrvideo.view.NewToast;

public class MultiImageSelectorFragment extends Fragment implements OnClickListener {
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;

    private static final String CAMERA_FOLDER = Environment.getExternalStorageDirectory().getPath()
            + "/DCIM/";// 手机相册所在文件夹
    // 为了尽量找准相册文件夹
    private static final String CAMERA_FOLDER_CAMERA = "Camera";// 手机相册所在文件夹
    private static final String CAMERA_FOLDER_100MEDIA = "100MEDIA";// 手机相册所在文件夹
    private static final String CAMERA_FOLDER_100ANDRO = "100ANDRO";// 手机相册所在文件夹

    // 结果数据
    private ArrayList<String> resultList = new ArrayList<String>();
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<Folder>();

    private Callback mCallback;

    private PictureAdapter mImageAdapter;
    private FolderListAdapter mFolderAdapter;

    private ListViewPopupWindow mFolderPopupWindow;
    private TextView mPopTitleCancel;
    private TextView mPopTitle;
    private RelativeLayout mPopTitleLayout;
    private RelativeLayout mSingleTitleLayout;
    private RelativeLayout mMultiTitleLayout;
    private TextView mSingleTitle;
    
    private TextView mMultiTitle;
    private TextView mImageCount;
    private TextView mTextCommit;
    private RelativeLayout mCommitBtn;
    
    private ImageView mArrowDown;
    private ImageView mArrowUp;
    
    private ImageView mDivider;

    // 类别
    private TextView mCategoryText;

    private int mDesireImageCount;

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;

    private File mTmpFile;


    private int mGroup = Constant.NO_GROUP;
    private RelativeLayout mTitleBar;
//    private Button mSubmitButton;
    private int mMode = Constant.MODE_MULTI; 

    private RecyclerView mRecyclerView;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 选择图片数量
        mDesireImageCount = getArguments().getInt(Constant.EXTRA_SELECT_COUNT);

        // 图片选择模式
        mMode = getArguments().getInt(Constant.EXTRA_SELECT_MODE);

        if(mMode == Constant.MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(Constant.EXTRA_DEFAULT_SELECTED_LIST);
            if(tmp != null && tmp.size()>0) {
                resultList = tmp;
            }
        }

        // 是否显示照相机
        mIsShowCamera = getArguments().getBoolean(Constant.EXTRA_SHOW_CAMERA, true);
        mImageAdapter = new PictureAdapter(mIsShowCamera);
        // 是否显示选择指示器
        mImageAdapter.showSelectIndicator(mMode == Constant.MODE_MULTI);
        
        mGroup = getArguments().getInt(Constant.EXTRA_DEFAULT_GROUP);

        int screenW = Utils.getScreenWidth(getActivity());
        int padding = (30 * screenW) / 750;
        
        mTitleBar = (RelativeLayout)view.findViewById(R.id.title_layout);
        LayoutParams rparam = (LayoutParams)mTitleBar.getLayoutParams();
        rparam.height = (88 * screenW) / 750;

        mDivider = (ImageView)view.findViewById(R.id.divider);

        //单图模式Title
        mSingleTitleLayout = (RelativeLayout)view.findViewById(R.id.singel_title_layout);
        mSingleTitle = (TextView)view.findViewById(R.id.single_title);
        //多图模式Title
        mMultiTitleLayout = (RelativeLayout)view.findViewById(R.id.multi_title_layout);
        mMultiTitle = (TextView)view.findViewById(R.id.multi_title);
        view.findViewById(R.id.rl_title).setOnClickListener(this);

        mImageCount = (TextView)view.findViewById(R.id.tv_count);
        rparam = (LayoutParams)mImageCount.getLayoutParams();
        rparam.width = (40 * screenW) / 750;
        rparam.height = (40 * screenW) / 750;

        mTextCommit = (TextView)view.findViewById(R.id.multi_commit_btn);
        mCommitBtn = (RelativeLayout)view.findViewById(R.id.ll_commit);
        mCommitBtn.setPadding(padding, 0, padding, 0);
        mCommitBtn.setOnClickListener(this);
        view.findViewById(R.id.multi_tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.multi_tv_cancel).setPadding(padding, 0, padding, 0);

        mArrowDown = (ImageView)view.findViewById(R.id.iv_arrow_down);
        rparam = (LayoutParams)mArrowDown.getLayoutParams();
        rparam.width = (36 * screenW) / 750;
        rparam.height = (20 * screenW) / 750;

        mArrowUp = (ImageView)view.findViewById(R.id.iv_pop_arrow_up);
        rparam = (LayoutParams)mArrowUp.getLayoutParams();
        rparam.width = (36 * screenW) / 750;
        rparam.height = (20 * screenW) / 750;
        
        //切换相册目录时popupwindow的Title
        mPopTitleLayout = (RelativeLayout)view.findViewById(R.id.pop_title_layout);
        
        mPopTitleLayout.setVisibility(View.GONE);
        if(mMode == Constant.MODE_MULTI) {
        	mSingleTitleLayout.setVisibility(View.GONE);
        	mMultiTitleLayout.setVisibility(View.VISIBLE);
        	mArrowUp.setVisibility(View.VISIBLE);
        }else{
        	mSingleTitleLayout.setVisibility(View.VISIBLE);
        	mMultiTitleLayout.setVisibility(View.GONE);
        	mArrowUp.setVisibility(View.GONE);
        }
        
        // 返回按钮
        view.findViewById(R.id.single_tv_cancel).setPadding(padding, padding / 2, padding, padding / 2);
        view.findViewById(R.id.single_tv_cancel).setOnClickListener(this);

        //多图模式更新title
        onCheckChanged();

        mCategoryText = (TextView) view.findViewById(R.id.single_category_btn);
        mCategoryText.setPadding(padding, padding / 2, padding, padding / 2);
        mCategoryText.setOnClickListener(this);

        //list view popupwindow
        mPopTitleCancel = (TextView)view.findViewById(R.id.pop_list_cancel);
        mPopTitleCancel.setPadding(padding, padding / 2, padding, padding / 2);
        mPopTitleCancel.setOnClickListener(this);
        
        mPopTitle = (TextView)view.findViewById(R.id.pop_list_title);

        if(mGroup != Constant.NO_GROUP){
        	mSingleTitle.setTextColor(getResources().getColor(R.color.white));
        	mCategoryText.setTextColor(getResources().getColor(R.color.white));
        	((TextView)view.findViewById(R.id.single_tv_cancel)).setTextColor(getResources().getColor(R.color.white));
        	
        	mPopTitle.setTextColor(getResources().getColor(R.color.white));
        	mPopTitleCancel.setTextColor(getResources().getColor(R.color.white));
        }

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);

	    final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
	    mRecyclerView.setLayoutManager(manager);

	    mImageAdapter.setOnItemClickListener(new PictureAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
                if(mImageAdapter.isShowCamera() && position == 0){
                    // 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
                	if(mDesireImageCount == resultList.size()){
                        NewToast.show(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT, NewToast.FAIL);
                        return;
                    }
                    ((MultiImageSelectorActivity)getActivity()).
                            checkPermission(new BaseActivity.PmListener() {
                                                @Override
                                                public void onGranted() {
                                                    showCameraAction();
                                                }

                                                @Override
                                                public void onDenied() {
                                                }
                                            },
                                    getString(R.string.camera_permission_setting),
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }else{
                    // 正常操作
                    Image image = (Image) mImageAdapter.getItem(position);
                    if (!FileUtils.isFileExist(image.path)) {
                        // 文件已经被删除，数据库未更新
                        NewToast.show(getActivity(), R.string.picture_deleted, Toast.LENGTH_SHORT, NewToast.FAIL);
                        return;
                    }
                    selectImageFromGrid(image, mMode);
                    mImageAdapter.notifyItemChanged(position);
                }
            }
		});
	    mRecyclerView.setAdapter(mImageAdapter);

        mFolderAdapter = new FolderListAdapter(getActivity());
    }

    private void onCheckChanged(){
    	if(mMode != Constant.MODE_MULTI) {
    		return;
    	}
    	
    	if(resultList == null || resultList.size() <= 0){
        	mTextCommit.setTextColor(getResources().getColor(R.color.color_normal_gray_color));
            mCommitBtn.setEnabled(false);
            mImageCount.setVisibility(View.GONE);
        }else{
        	mImageCount.setVisibility(View.VISIBLE);
        	mImageCount.setText("" + resultList.size());
            mTextCommit.setTextColor(getResources().getColor(R.color.color_normal_black_color));
            mCommitBtn.setEnabled(true);
        }
    }
    
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.pop_list_cancel){
			changeTitle(false);
			if(mFolderPopupWindow != null && mFolderPopupWindow.isShowing()){
				mFolderPopupWindow.dismiss();
			}
		}else if(view.getId() == R.id.single_tv_cancel
				|| view.getId() == R.id.multi_tv_cancel){
			if(mCallback != null){
            	mCallback.onCancel();
            }
		}else if(view.getId() == R.id.single_category_btn
				|| view.getId() == R.id.rl_title){
			if(mFolderPopupWindow == null){
                createPopupFolderList();
            }

            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
                changeTitle(false);
            } else {
                mFolderPopupWindow.showAsDropDown(mDivider);
                int index = mFolderAdapter.getSelectIndex();
//                mFolderPopupWindow.getListView().setSelection(index);
            	changeTitle(true);
            }
		}else if(view.getId() == R.id.ll_commit){
			if(mCallback != null){
            	mCallback.onComplete();
            }
		}
	}
    
	private void changeTitle(final boolean showpopup){
		if(mMode == Constant.MODE_MULTI){
			Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
			animation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation arg0) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation arg0) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation arg0) {
					if(showpopup){
						mPopTitleLayout.setVisibility(View.VISIBLE);
			        	mMultiTitleLayout.setVisibility(View.GONE);
					}else{
				        mMultiTitleLayout.setVisibility(View.VISIBLE);
						mPopTitleLayout.setVisibility(View.GONE);
					}
				}
			});
			if(showpopup){
				mArrowDown.startAnimation(animation);
			}else{
				mArrowUp.startAnimation(animation);
			}
		}else{
			if(showpopup){
				mPopTitleLayout.setVisibility(View.VISIBLE);
	        	mSingleTitleLayout.setVisibility(View.GONE);
			}else{
				mPopTitleLayout.setVisibility(View.GONE);
		        mSingleTitleLayout.setVisibility(View.VISIBLE);
			}
		}
	}
	
    private void createPopupFolderList() {
        mFolderPopupWindow = new ListViewPopupWindow(getActivity());
        mFolderPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				changeTitle(false);
			}
		});
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
        mFolderPopupWindow.setHeight(LayoutParams.MATCH_PARENT);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mFolderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	changeTitle(false);
                        mFolderPopupWindow.dismiss();

//                        if (index == 0) {
//                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
//                            mCategoryText.setText(R.string.photo_album);
//                            mPopTitle.setText(R.string.photo_album);
//                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.setData(folder.images);
                                if(mMode == Constant.MODE_SINGLE){
                                	mCategoryText.setText(folder.name);
                                }else if(mMode == Constant.MODE_MULTI){
                                	mMultiTitle.setText(folder.name);
                                }
                                mPopTitle.setText(folder.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    mImageAdapter.setDefaultSelected(resultList);
                                }
                            }
//                        }
                        if (mIsShowCamera) {
                            mImageAdapter.setShowCamera(true);
                        } else {
                            mImageAdapter.setShowCamera(false);
                        }
                        // 滑动到最初始位置
                        mRecyclerView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile);
                    }
                }
            }else{
                if(mTmpFile != null && mTmpFile.exists()){
                    mTmpFile.delete();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(mFolderPopupWindow != null){
            if(mFolderPopupWindow.isShowing()){
                mFolderPopupWindow.dismiss();
            }
        }

        super.onConfigurationChanged(newConfig);

    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = createTmpFile(getActivity());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }else{
            NewToast.show(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT, NewToast.FAIL);
        }
    }

    /**
     * 选择图片操作
     * @param image
     */
    private void selectImageFromGrid(Image image, int mode) {
        if(image != null) {
            // 多选模式
            if(mode == Constant.MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);

                    if (mCallback != null) {
                        mCallback.onImageUnselected(image.path);
                    }
                } else {
                    // 判断选择数量问题
                    if(mDesireImageCount == resultList.size()){
                        NewToast.show(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT, NewToast.FAIL);
                        return;
                    }

                    resultList.add(image.path);

                    if (mCallback != null) {
                        mCallback.onImageSelected(image.path);
                    }
                }
                //Title显示状态改变
                onCheckChanged();
                
                mImageAdapter.select(image);
            }else if(mode == Constant.MODE_SINGLE){
                // 单选模式
                if(mCallback != null){
                    mCallback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media._ID };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }else if(id == LOADER_CATEGORY){
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0]+" like '%"+args.getString("path")+"%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> images = new ArrayList<Image>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do{
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);
                        Log.v("VRVideo","path:"+path+" name:"+name);
                        if( !hasFolderGened ) {
                            // 获取文件夹名称
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.cover = image;
                            if (!mResultFolder.contains(folder)) {
                                List<Image> imageList = new ArrayList<Image>();
                                imageList.add(image);
                                folder.images = imageList;
                                mResultFolder.add(folder);
                            } else {
                                // 更新
                                Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    }while(data.moveToNext());

                    sortBucketList();
                    if(mResultFolder != null && mResultFolder.size() > 0){
                    	mImageAdapter.setData(mResultFolder.get(0).images);
                    }

                    // 设定默认选择
                    if(resultList != null && resultList.size()>0){
                        mImageAdapter.setDefaultSelected(resultList);
                    }

                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * 相机路径排第一位,找不到没办法
     */
    private void sortBucketList() {
        if (mResultFolder == null) {
            return;
        }
        int size = mResultFolder.size();
        Folder folder = null;
        for (int i = 0; i < size; i++) {
            String path = mResultFolder.get(i).path;
            if ((CAMERA_FOLDER + CAMERA_FOLDER_CAMERA).equals(path)
                    || (CAMERA_FOLDER + CAMERA_FOLDER_100ANDRO).equals(path)
                    || (CAMERA_FOLDER + CAMERA_FOLDER_100MEDIA).equals(path)) {
            	folder = mResultFolder.get(i);
            	folder.name = getString(R.string.album);	//显示名改为“相册”
            	mResultFolder.remove(i);
                break;
            }
        }
        if (folder != null) {
        	mResultFolder.add(0, folder);
        }
    }
    
    private File createTmpFile(Context context){

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            // 已挂载
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_"+timeStamp+"";
            File tmpFile = new File(pic, fileName+".jpg");
            return tmpFile;
        }else{
            File cacheDir = context.getCacheDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_"+timeStamp+"";
            File tmpFile = new File(cacheDir, fileName+".jpg");
            return tmpFile;
        }
    }
    
    /**
     * 回调接口
     */
    public interface Callback{
        void onSingleImageSelected(String path);
        void onImageSelected(String path);
        void onImageUnselected(String path);
        void onCameraShot(File imageFile);
        void onComplete();
        void onCancel();
    }
}
