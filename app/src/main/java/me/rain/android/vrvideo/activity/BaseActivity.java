package me.rain.android.vrvideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.imageselector.view.DialogView;
import me.rain.android.vrvideo.imageselector.view.ProgressDialog;

/**
 * Created by CBS on 2016/3/16.
 */
public class BaseActivity extends Activity {
    public static final int REQUEST_PERMISSION = 1001;
    public interface PmListener{
        void onGranted();
        void onDenied();
    }


    private PmListener mPmListener;
    private ProgressDialog dialog;
    private String dialog_message;
    protected void checkPermission(PmListener listener, String message, String... permissions){
        if (permissions == null){
            return;
        }
        mPmListener = listener;
        dialog_message = message;
        boolean needRequest = false;
        List<String> permissionList = new ArrayList<String>();
        for(String permission : permissions){
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                needRequest = true;
                permissionList.add(permission);
            }
        }
        if (needRequest){
            String[] permissionArray = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissionArray, REQUEST_PERMISSION);
        }else {
            if (mPmListener != null){
                mPmListener.onGranted();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mPmListener != null){
                    mPmListener.onGranted();
                }
            } else {
                dialog = new ProgressDialog(this, R.style.NoTitleDialog,
                        getString(R.string.cancel),
                        getString(R.string.goto_setting),
                        dialog_message,
                        new ProgressDialog.onDialogClickListener() {

                            @Override
                            public void onButtonClick(int btnId) {
                                dialog.dismiss();
                                if (btnId == DialogView.BTN_LEFT) {
                                } else {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", "com.imoyo.streetsnap", null);
                                    intent.setData(uri);
                                    try {
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                dialog.show();
                if (mPmListener != null){
                    mPmListener.onDenied();
                }
            }
        }
    }
}
