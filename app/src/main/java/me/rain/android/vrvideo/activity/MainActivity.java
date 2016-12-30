package me.rain.android.vrvideo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;
import java.util.List;

import me.rain.android.vrvideo.R;
import me.rain.android.vrvideo.databinding.ActivityMainBinding;
import me.rain.android.vrvideo.imageselector.Constant;
import me.rain.android.vrvideo.imageselector.MultiImageSelectorActivity;
import me.rain.android.vrvideo.view.NewToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mBinding;
    private String TAG = "VRVideo";
    private final int REQUEST_CODE_VR_VIDEO = 1000;

    private String mVideoPath;

    private boolean isMuted;
    private boolean isPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mVideoPath = "";
        mBinding.btnSelect.setOnClickListener(this);

        VrVideoView.Options options = new VrVideoView.Options();
        options.inputType = VrVideoView.Options.TYPE_MONO;

        mBinding.vrVideoView.setEventListener(new VrVideoEventListener() {

            @Override
            public void onCompletion() {
                super.onCompletion();
                mBinding.vrVideoView.seekTo(0);
                setIsPlay(false);
            }

            @Override
            public void onNewFrame() {
                super.onNewFrame();
                mBinding.seekBar.setProgress((int) mBinding.vrVideoView.getCurrentPosition());
            }

            @Override
            public void onClick() {
                super.onClick();
            }

            @Override
            public void onLoadError(String errorMessage) {
                super.onLoadError(errorMessage);
                NewToast.show(MainActivity.this, errorMessage, Toast.LENGTH_SHORT, NewToast.FAIL);
            }

            @Override
            public void onLoadSuccess() {
                super.onLoadSuccess();
                mBinding.seekBar.setMax((int) mBinding.vrVideoView.getDuration());
            }

            /**
             * 显示模式改变回调
             * 1.默认
             * 2.全屏模式
             * 3.VR观看模式，即横屏分屏模式
             * @param newDisplayMode 模式
             */
            @Override
            public void onDisplayModeChanged(int newDisplayMode) {
                super.onDisplayModeChanged(newDisplayMode);
            }
        });
        try {
            mBinding.vrVideoView.loadVideoFromAsset("2016120501.mp4", options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBinding.volumeToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setIsMuted(!isMuted);
            }
        });

        mBinding.playToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setIsPlay(!isPlay);
            }
        });

        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mBinding.vrVideoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
        mBinding.volumeToggle.setImageResource(isMuted ? R.mipmap.icon_mute_on : R.mipmap.icon_mute_off);
        mBinding.vrVideoView.setVolume(isMuted ? 0.0f : 1.0f);
    }

    private void setIsPlay(boolean isPlay) {
        if(TextUtils.isEmpty(mVideoPath)) {
            NewToast.show(this, R.string.empty_video_error, Toast.LENGTH_SHORT, NewToast.FAIL);
            return;
        }

        this.isPlay = isPlay;
        mBinding.playToggle.setImageResource(isPlay ? R.mipmap.icon_playbutton:  R.mipmap.icon_pause_button );
        if(isPlay){
            mBinding.vrVideoView.playVideo();
        }else{
            mBinding.vrVideoView.pauseVideo();
        }
    }

    @Override
    protected void onDestroy() {
        mBinding.vrVideoView.pauseVideo();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        if(view == mBinding.btnSelect) {
            Intent intent = new Intent(this, MultiImageSelectorActivity.class);
            intent.putExtra(Constant.EXTRA_SHOW_CAMERA, false);// 是否显示拍摄图片
            intent.putExtra(Constant.EXTRA_SELECT_MODE, Constant.MODE_SINGLE);// 选择模式
            startActivityForResult(intent, REQUEST_CODE_VR_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VR_VIDEO) { // 发送本地图片
                if (data != null) {

                    List<String> list = data.getStringArrayListExtra(Constant.EXTRA_RESULT);
                    if(isPlay) {
                        mBinding.vrVideoView.pauseVideo();
                    }
                    VrVideoView.Options options = new VrVideoView.Options();
                    options.inputType = VrVideoView.Options.TYPE_MONO;
                    try {
                        mBinding.vrVideoView.loadVideo(Uri.parse(list.get(0)), options);
                        mBinding.vrVideoView.seekTo(0);
                        setIsPlay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
