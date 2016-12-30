package me.rain.android.vrvideo.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import me.rain.android.vrvideo.BuildConfig;

public class Utils {
    public static final String TAG = "VRVideo";

	public static void logd(CharSequence msg) {
		if (!TextUtils.isEmpty(msg) && BuildConfig.DEBUG_MODE) {
			Log.i(TAG, msg.toString());
		}
	}

	public static void loge(CharSequence msg) {
		if (!TextUtils.isEmpty(msg) && BuildConfig.DEBUG_MODE) {
			Log.e(TAG, msg.toString());
		}
	}

	public static void loge(Throwable e) {
		if (e != null && BuildConfig.DEBUG_MODE) {
			Log.e(TAG, "", e);
		}
	}
	
	public static void log_d(String tag, String msg){
		if (BuildConfig.DEBUG_MODE){
			Log.d(tag, msg);
		}
	}

    public static void getScreenRect(Context ctx_, Rect outrect_) {
        if (ctx_ == null || outrect_ == null){
            return;
        }
        Display screenSize = ((WindowManager) ctx_
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        outrect_.set(0, 0, screenSize.getWidth(), screenSize.getHeight());
    }

    public static int getScreenWidth(Context context){
        Rect rect = new Rect();
        getScreenRect(context, rect);
        return rect.right - rect.left;
    }

	private static int getScreenHeight(Context context) {
	    int heightPixels;  
	    Display d = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    d.getMetrics(metrics);  
	    // since SDK_INT = 1;  
	    heightPixels = metrics.heightPixels;  
	    // includes window decorations (statusbar bar/navigation bar)  
	    if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
	        try {  
	            heightPixels = (Integer) Display.class
	                    .getMethod("getRawHeight").invoke(d);  
	        } catch (Exception ignored) {
	    }  
	    // includes window decorations (statusbar bar/navigation bar)  
	    else if (Build.VERSION.SDK_INT >= 17)
	        try {  
	            android.graphics.Point realSize = new android.graphics.Point();  
	            Display.class.getMethod("getRealSize",
	                    android.graphics.Point.class).invoke(d, realSize);  
	            heightPixels = realSize.y;  
	        } catch (Exception ignored) {
	    }  
//	    Log.v("sereinli", "h:"+heightPixels);  
	    return heightPixels;  
	}
}
