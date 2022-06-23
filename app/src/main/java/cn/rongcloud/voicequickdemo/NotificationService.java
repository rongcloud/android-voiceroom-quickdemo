/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voicequickdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.kit.UIKit;
import com.kit.utils.Logger;


/**
 * 以解决通话过程中切入后台麦克风不工作
 */
public class NotificationService extends Service {

    private static final String CHANNEL_ID = "NotificationService";
    private static final String ACTION_KEY = "ACTION";
    private final int notifyId = 20200202;
    private NotificationManager manager;

    public static void bindNotifyService(Activity activity, String action) {
        Intent intent = new Intent(activity, NotificationService.class);
        String act = activity.getPackageName() + "." + action;
        Logger.e("NotificationService", "act = " + act);
        intent.putExtra(ACTION_KEY, act);
        activity.startService(intent);
    }

    public static void unbindNotifyService() {
        UIKit.getContext().stopService(new Intent(UIKit.getContext(), NotificationService.class));
    }

    private void init() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(CHANNEL_ID, "onCreate");
        init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(CHANNEL_ID, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(CHANNEL_ID, "onStartCommand" + intent);
        if (intent != null) {
            String action = intent.getStringExtra(ACTION_KEY);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(action)
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    0);
            Notification.Builder builder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.app_icon)
                            .setContentTitle("QuickDemo—语聊房")
                            .setContentText("正在语聊中...")
                            .setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setCategory(Notification.CATEGORY_EVENT);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }
            Notification notification = builder.build();
            manager.notify(notifyId, notification);
            startForeground(notifyId, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(CHANNEL_ID, "onDestroy");
        manager.cancel(notifyId);
        stopForeground(true);
    }

}
