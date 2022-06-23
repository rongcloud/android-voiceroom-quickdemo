package cn.rongcloud.example;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.stream.RCRTCAudioInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;

/**
 * 扩展api
 */
public class ExtendApi {
    static String TAG = "ExtendApi";

    /**
     * 静音指定id的远端用户
     *
     * @param userId 用户id
     * @param mute   是否静音
     */
    public static void muteUser(String userId, boolean mute) {
        RCRTCRoom room = RCRTCEngine.getInstance().getRoom();
        if (null == room) {
            Log.e(TAG, "muteUser: Not join rtc room");
            return;
        }
        List<RCRTCRemoteUser> users = room.getRemoteUsers();
        int count = null == users ? 0 : users.size();
        if (count < 1) {
            Log.e(TAG, "muteUser: Not user in rtc room ");
            return;
        }
        RCRTCRemoteUser muteUser = null;
        for (RCRTCRemoteUser user : users) {
            if (TextUtils.equals(userId, user.getUserId())) {
                muteUser = user;
                break;
            }
        }
        if (null == muteUser) {
            Log.e(TAG, "muteUser: Not find user for id = " + userId);
            return;
        }
        List<RCRTCInputStream> streams = muteUser.getStreams();
        RCRTCAudioInputStream audioInputStream = null;
        if (null != streams) {
            for (RCRTCInputStream stream : streams) {
                if (stream instanceof RCRTCAudioInputStream) {
                    audioInputStream = (RCRTCAudioInputStream) stream;
                    break;
                }
            }
        }
        if (null == audioInputStream) {
            Log.e(TAG, "muteUser: Not find audio stream for id = " + userId);
            return;
        }
        audioInputStream.mute(mute);
    }
}
