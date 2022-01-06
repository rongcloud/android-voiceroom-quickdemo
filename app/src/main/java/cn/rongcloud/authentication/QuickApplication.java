package cn.rongcloud.authentication;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;

import cn.rongcloud.messager.DelegateMode;
import cn.rongcloud.oklib.wrapper.OkHelper;
import cn.rongcloud.oklib.wrapper.interfaces.IHeader;
import cn.rongcloud.quickdemo.uitls.UIKit;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import okhttp3.Headers;

public class QuickApplication extends Application {
    private final static String TAG = "QuickApplication";
    private final static String APP_KEY = "pvxdm17jpw7ar";
    private final static String Business_Token = "";

    @Override
    public void onCreate() {
        super.onCreate();
        initVoiceRoom();
        initOKLibs();
    }

    private void initVoiceRoom() {
        /**
         * 这里使用语聊房 SDK 初始化，所以不再需要 RCCoreClient 初始化融云相关 SDK。
         * appkey 即您申请的 appkey，需要开通音视频直播服务
         * token一般是您在登录自己的业务服务器之后，业务服务器返回给您的，可存在本地。
         */
        String process = UIKit.getCurrentProcessName();
        if (!getPackageName().equals(process)) {
            // 非主进程不初始化 避免过度初始化
            return;
        }
        Log.d(TAG, "initVoiceRoom:process : " + process);
        RCVoiceRoomEngine.getInstance().initWithAppKey(this, APP_KEY, null);
    }

    private static String authorization;

    public static void setAuthorization(String authorization) {
        QuickApplication.authorization = authorization;
    }

    void initOKLibs() {
        OkHelper.get().setHeadCacher(new IHeader() {
            @Override
            public Map<String, String> onAddHeader() {
                // todo 需要申请businiesToken
                if (TextUtils.isEmpty(Business_Token)){
                    throw new IllegalArgumentException("请在官网申请 BusinessToken ");
                }
                Map map = new HashMap<String, String>();
                if (!TextUtils.isEmpty(authorization)) {
                    map.put("Authorization", authorization);
                }
                map.put("BusinessToken", Business_Token);
                return map;
            }

            @Override
            public void onCacheHeader(Headers headers) {

            }
        });
    }
}
