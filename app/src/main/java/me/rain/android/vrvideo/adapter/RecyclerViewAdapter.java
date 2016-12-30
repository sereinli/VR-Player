package me.rain.android.vrvideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.rain.android.vrvideo.decoration.ItemTouchHelperInterface;

/**
 * Created by CBS on 2016/5/6.
 */
public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter.BaseViewHolder> implements ItemTouchHelperInterface.ItemTouchHelperAdapter {
    public static final int VIEW_TYPE_HEADER = 0X01;
    public static final int VIEW_TYPE_ITEM = 0X02;
    public static final int VIEW_TYPE_FOOTER = 0X03;
    public static final int VIEW_TYPE_EMPTY = 0X04;
    public static final int VIEW_TYPE_AD = 0X05;

    //Item项点击处理
    protected OnItemClickListener mItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    //列表滑动监听（上拉更多）
    protected OnListMoveListener mListMoveListener;
    public interface OnListMoveListener {
        void onLoadMore();
    }
    public void setOnListMoveListener(OnListMoveListener listener) {
        this.mListMoveListener = listener;
    }

    protected Context mContext;
    protected List<T> mData;

    public RecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mData = new ArrayList<T>();
    }

    public RecyclerViewAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
    }

    public List<T> getData() {
        return mData;
    }

    public T getItem(int position) {
        if(mData == null || position >= getItemCount()){
            return null;
        }
        return mData.get(position);
    }

    public void setData(List<T> data){
        mData = data;
        notifyDataSetChanged();
    }

    public void addAll(List<T> data){
        int pos = mData.size();
        mData.addAll(data);
        //notifyItemRangeInserted(pos, data.size());
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mData.remove(position);
        notifyDataSetChanged();

    }

    public void remove(T object) {
        mData.remove(object);
        notifyDataSetChanged();
    }

    public void add(int position, T item) {
        mData.add(position, item);
        notifyDataSetChanged();
    }

    public void add(T item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public boolean hasHeader() {
        return false;
    }

    public boolean hasFooter() {
        return false;
    }


    @Override
    public int getItemCount() {
        if(mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if(hasHeader() && (fromPosition == 0 || toPosition == 0)) {
            //item 不能和 header互换位置
            return;
        }

        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        if(position == 0 && hasHeader()) {
            //header 不能删除
            return;
        }
        mData.remove(hasHeader() ? position - 1 : position);
        notifyItemChanged(position);
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }
}
