package me.rain.android.vrvideo.imageselector.view;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import me.rain.android.vrvideo.utils.Utils;

/**
 * Created by Administrator on 2016/6/13 0013.
 */
public class ProgressDialog extends Dialog {
    private String mLeftStr;
    private String mRightStr;
    private String mContentStr;


    private onDialogClickListener mListener;
    public interface onDialogClickListener{
        void onButtonClick(int btnId);
    }

    public void setDialogClickListener(onDialogClickListener listener) {
        mListener = listener;
    }

    public ProgressDialog(Context context) {
        super(context);
        init();
    }

    public ProgressDialog(Context context, int theme, String left, String right, String content, onDialogClickListener listener){
        super(context, theme);
        mLeftStr = left;
        mRightStr = right;
        mContentStr = content;
        mListener = listener;
        init();
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init(){
        DialogView view = new DialogView(getContext());
        view.setClickListener(new DialogView.ClickListener() {
            @Override
            public void click(int btnId) {
                if(mListener != null) {
                    mListener.onButtonClick(btnId);
                }
                dismiss();
            }
        });
        view.updateViews(mLeftStr, mRightStr, mContentStr);

        setContentView(view);

        int screenW = Utils.getScreenWidth(getContext());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (550 * screenW) / 750; // 高度设置为屏幕的0.6
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }
}
