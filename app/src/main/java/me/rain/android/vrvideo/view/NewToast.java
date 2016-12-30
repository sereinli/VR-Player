package me.rain.android.vrvideo.view;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.utils.Utils;


public abstract class NewToast {
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    private static android.widget.Toast toast;
    private static Handler handler = new Handler();
    private static TextView mTextView;
    private static LinearLayout mToastLayout;
    private static int mAlertType = FAIL;
 
    private static Runnable run = new Runnable() {
        public void run() {
            toast.cancel();
        }
    };
 
    private static void toast(Context ctx, CharSequence msg, int duration) {
        handler.removeCallbacks(run);
        // handler的duration不能直接对应Toast的常量时长，在此针对Toast的常量相应定义时长
        switch (duration) {
        case LENGTH_SHORT:// Toast.LENGTH_SHORT值为0，对应的持续时间大概为1s
            duration = 1000;
            break;
        case LENGTH_LONG:// Toast.LENGTH_LONG值为1，对应的持续时间大概为3s
            duration = 3000;
            break;
        default:
            break;
        }
        if (null != toast) {
            mTextView.setText(msg);
        } else {
            toast = new Toast(ctx);
            View v = View.inflate(ctx, R.layout.view_toast_view, null);

            mToastLayout = (LinearLayout)v.findViewById(R.id.toast_layout);

            int w = Utils.getScreenWidth(ctx);
            int h = (80 * w) / 750;
            mTextView = (TextView) v.findViewById(R.id.text);
            mTextView.getLayoutParams().height = h;
            mTextView.setText(msg);
            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setView(v);
        }

        if(mAlertType == SUCCESS) {
            mToastLayout.setBackgroundColor(ctx.getResources().getColor(R.color.color_normal_green_color));
        }else {
            mToastLayout.setBackgroundColor(ctx.getResources().getColor(R.color.color_normal_red_color));
        }

        handler.postDelayed(run, duration);
        toast.show();
    }
 
    /**
     * 弹出Toast
     * 
     * @param ctx
     *            弹出Toast的上下文
     * @param msg
     *            弹出Toast的内容
     * @param duration
     *            弹出Toast的持续时间
     */
    private static void show(Context ctx, CharSequence msg, int duration)
            throws NullPointerException {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        if (0 > duration) {
            duration = LENGTH_SHORT;
        }
        toast(ctx, msg, duration);
    }

    public static void show(Context ctx, CharSequence msg, int duration, int type)
            throws NullPointerException {
        mAlertType = type;
        show(ctx, msg, duration);
    }
 
    /**
     * 弹出Toast
     * 
     * @param ctx
     *            弹出Toast的上下文
     * @param resId
     *            弹出Toast的内容的资源ID
     * @param duration
     *            弹出Toast的持续时间
     */
    private static void show(Context ctx, int resId, int duration)
            throws NullPointerException {
        if (null == ctx) {
            throw new NullPointerException("The ctx is null!");
        }
        if (0 > duration) {
            duration = LENGTH_SHORT;
        }
        toast(ctx, ctx.getResources().getString(resId), duration);
    }

    public static void show(Context ctx, int resId, int duration, int type)
            throws NullPointerException {
        mAlertType = type;
        show(ctx, resId, duration);
    }
}