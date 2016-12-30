package me.rain.android.vrvideo.imageselector.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.databinding.ViewDialogViewBinding;
import me.rain.android.vrvideo.utils.Utils;


/**
 * Created by Administrator on 2016/6/13 0013.
 */
public class DialogView extends RelativeLayout implements View.OnClickListener{
    public static final int BTN_LEFT = 1;
    public static final int BTN_RIGHT = 2;
    public interface ClickListener{
        void click(int btnId);
    }

    private Context mContext;
    private ViewDialogViewBinding mBinding;
    private ClickListener mListener;

    public DialogView(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public DialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    public DialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews();
    }

    public void setClickListener(ClickListener listener){
        mListener = listener;
    }

    public void updateViews(String left, String right, String content){
        mBinding.tvContent.setText(content);
        if (TextUtils.isEmpty(left)){
            mBinding.tvLeft.setVisibility(GONE);
        }else{
            mBinding.tvLeft.setText(left);
            mBinding.tvLeft.setVisibility(VISIBLE);
        }

        if (TextUtils.isEmpty(right)){
            mBinding.tvRight.setVisibility(GONE);
        }else{
            mBinding.tvRight.setText(right);
            mBinding.tvRight.setVisibility(VISIBLE);
        }

        if(TextUtils.isEmpty(left) || TextUtils.isEmpty(right)) {
            mBinding.ivLine.setVisibility(GONE);
        }else {
            mBinding.ivLine.setVisibility(VISIBLE);
        }
    }

    public void updateMessage(String content) {
        mBinding.tvContent.setText(content);
    }

    private void initViews(){
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.view_dialog_view, this ,true);
        int screenW = Utils.getScreenWidth(mContext);
        int margin = (100 * screenW) / 750;

        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) mBinding.ivIcon.getLayoutParams();
        llp.width = (66 * screenW) / 750;
        llp.height = (42 * screenW) / 750;
        llp.topMargin = llp.bottomMargin = margin;

        llp = (LinearLayout.LayoutParams) mBinding.tvContent.getLayoutParams();
        llp.leftMargin = llp.rightMargin = (55 * screenW) / 750;
        llp.bottomMargin = margin;

        llp = (LinearLayout.LayoutParams) mBinding.llBtn.getLayoutParams();
        llp.height = margin;
        mBinding.tvLeft.setOnClickListener(this);
        mBinding.tvRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.tvLeft){
            if (mListener != null){
                mListener.click(BTN_LEFT);
            }
        }else if (v == mBinding.tvRight){
            if (mListener != null){
                mListener.click(BTN_RIGHT);
            }
        }
    }
}
