package me.rain.android.vrvideo.imageselector.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.rain.android.vrvideo.imageselector.models.Image;
import me.rain.android.vrvideo.imageselector.view.ImageSelectorItemView;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.GridViewHolder>{
	
	private OnItemClickListener mListener;
	public static interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    
	public class GridViewHolder extends RecyclerView.ViewHolder {
		public TextView textView;
		public GridViewHolder(View itemView) {
			super(itemView);
		}
	}
	
	private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private List<Image> mImages;
    private List<Image> mSelectedImages;
	private boolean showCamera = true;
    private boolean showSelectIndicator = true;

	public PictureAdapter(boolean showCamera){
		this.showCamera = showCamera;
		mImages = new ArrayList<Image>();
		mSelectedImages = new ArrayList<Image>();
	}

	public void showSelectIndicator(boolean indicator) {
        showSelectIndicator = indicator;
    }
	
	public boolean isShowCamera(){
        return showCamera;
    }
	
	public void setShowCamera(boolean b){
        if(showCamera == b){
        	return;
        }

        showCamera = b;
//        notifyDataSetChanged();
    }
	
	/**
     * 选择某个图片，改变选择状态
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
//        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size()>0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

	@Override
	public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = new ImageSelectorItemView(parent.getContext());
		return new GridViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final GridViewHolder holder, final int position) {
		if(holder.getItemViewType() == TYPE_CAMERA){
			((ImageSelectorItemView)holder.itemView).update(true, false, false, "");
		}else{
			Image imageItem = getItem(position);
			((ImageSelectorItemView)holder.itemView).update(false, showSelectIndicator, mSelectedImages.contains(imageItem), imageItem.path);
		}
		
		holder.itemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				int position = holder.getPosition();
				if(mListener != null){
					mListener.onItemClick(view, position);
				}
			}
		});
	}

	public boolean isCamera(int position){
		if(getItemViewType(position) == TYPE_CAMERA){
			return true;
		}
		
		return false;
	}
	
	public Image getItem(int i) {
        if(showCamera){
            if(i == 0){
                return null;
            }
            return mImages.get(i - 1);
        }else{
            return mImages.get(i);
        }
    }
	
	@Override
	public int getItemCount() {
		if(mImages == null){
			return showCamera ? 1 : 0;
		}
		
		return showCamera ? mImages.size() + 1 : mImages.size();
	}
	
	@Override
    public int getItemViewType(int position) {
        if(showCamera){
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }
}