package cn.rongcloud.authentication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kit.UIKit;
import com.kit.utils.KToast;
import com.kit.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.oklib.wrapper.OkHelper;
import cn.rongcloud.oklib.wrapper.interfaces.IHeader;
import cn.rongcloud.voiceroom.utils.VMLog;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.RongIMClient;
import okhttp3.Headers;

public class QuickApplication extends Application {
    private final static String TAG = "QuickApplication";
    private final static String APP_KEY = "";
    private final static String Business_Token = "";
    private final static List<Activity> tasks = new ArrayList<>(4);

    @Override
    public void onCreate() {
        super.onCreate();
        initVoiceRoom();
        initOKLibs();
        initConnectListener();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                tasks.add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                tasks.remove(activity);
            }
        });
    }

    private void initVoiceRoom() {
        /**
         * 这里使用语聊房 SDK 初始化，所以不再需要 RCCoreClient 初始化融云相关 SDK。
         * appkey 即您申请的 appkey，需要开通音视频直播服务
         * token一般是您在登录自己的业务服务器之后，业务服务器返回给您的，可存在本地。
         */
        String process = getCurrentProcessName();
        if (!getPackageName().equals(process)) {
            // 非主进程不初始化 避免过度初始化
            return;
        }
        Log.d(TAG, "initVoiceRoom:process : " + process);
        if (TextUtils.isEmpty(APP_KEY)) {
            throw new IllegalArgumentException("AppKey不能为空");
        }
        RongCoreClient.init(this, APP_KEY, false);
//        VMLog.setCustomerPreTag("quickdemo_");
//        VMLog.setDetailLevel(DetailLevel.msg);
        VMLog.setDebug(true);
        VMLog.setOpenInvokeRecord(true);
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
                if (TextUtils.isEmpty(Business_Token)) {
                    throw new IllegalArgumentException("请申请 BusinessToken，申请地址：https://rcrtc-api.rongcloud.net/code/ ");
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

    public String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    protected void initConnectListener() {
        RongIMClient.getInstance().setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
            @Override
            public void onChanged(ConnectionStatus connectionStatus) {
                Logger.e(TAG, "connectionStatus = " + connectionStatus);
                if (connectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                    KToast.show("当前账号已在其他设备登录，请重新连接");
                    int count = tasks.size();
                    for (int i = 0; i < count - 1; i++) {
                        tasks.get(i).finish();
                    }
                    Activity top = tasks.get(count - 1);
                    UIKit.startActivity(top, LoginActivity.class);
                    top.finish();
                }
            }
        });
    }
}
