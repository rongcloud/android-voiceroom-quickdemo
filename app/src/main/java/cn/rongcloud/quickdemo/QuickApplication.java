package cn.rongcloud.quickdemo;

import android.app.Application;
import android.util.Log;

import cn.rongcloud.quickdemo.uitls.UIKit;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;

public class QuickApplication extends Application {
    private final static String TAG = "QuickApplication";
    private final static String APP_KEY = "pvxdm17jpw7ar";

    @Override
    public void onCreate() {
        super.onCreate();
        initVoiceRoom();
    }

    private void initVoiceRoom() {
        String process = UIKit.getCurrentProcessName();
        if (!getPackageName().equals(process)) {
            // 非主进程不初始化 避免过度初始化
            return;
        }
        Log.d(TAG, "initVoiceRoom:process : " + process);
        RCVoiceRoomEngine.getInstance().initWithAppKey(this, APP_KEY);
    }

}
