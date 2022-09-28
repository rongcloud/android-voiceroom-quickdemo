package cn.rongcloud.voicequickdemo;

import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

import com.kit.utils.KToast;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.voicequickdemo.interfaces.Api;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.IError;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

public class VoiceRoomApi implements Api {
    private final static String TAG = "VoiceRoomApi";
    private final static Api api = new VoiceRoomApi();
    private final static RCVoiceRoomInfo roomInfo = new RCVoiceRoomInfo();

    private VoiceRoomApi() {
    }

    public static Api getApi() {
        return api;
    }

    boolean mutePkFlag = false;

    /**
     * 处理房间api调用
     *
     * @param apiFun
     */
    @Override
    public void handleRoomApi(ApiFun apiFun, String userId, String roomId) {
        switch (apiFun) {
            case room_all_lock://全麦锁定
                lockAll(true);
                break;
            case room_all_lock_nu://全麦解锁
                lockAll(false);
                break;
            case room_all_mute://全麦静音
                muteAll(true);
                break;
            case room_all_mute_un://取消全麦静音
                muteAll(false);
                break;
            case room_update_name://修改名称
                String name = roomInfo.getRoomName();
                if (TextUtils.isEmpty(name) || !name.startsWith("更新_")) {
                    name = "更新_" + name;
                } else {
                    name = name.replace("更新_", "");
                }
                updateRoomName(name, null);
                break;
            case room_update_count://修改麦位数
                int count = roomInfo.getSeatCount() != 9 ? 9 : 5;
                updateSeatCount(count, null);
                break;
            case invite_seat://邀请上麦
                if (!TextUtils.isEmpty(userId)) {
                    invitedEnterSeat(userId, null);
                }
                break;
            case room_free://自由模式
                roomInfo.setFreeEnterSeat(true);
                updateRoomInfo(roomInfo, null);
                break;
            case room_free_un://申请模式
                roomInfo.setFreeEnterSeat(false);
                updateRoomInfo(roomInfo, null);
                break;
            case invite_pk://邀请PK
                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(roomId)) {
                    RCVoiceRoomEngine.getInstance().sendPKInvitation(roomId, userId, new RCVoiceRoomCallback() {
                        @Override
                        public void onSuccess() {
                            KToast.show("邀请pk成功！");
                        }

                        @Override
                        public void onError(int code, String message) {
                            KToast.show("邀请pk失败：【" + code + "】 msg = " + message);
                        }
                    });
                }
                break;
            case invite_pk_cancel://取消邀请PK
                if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(roomId)) {
                    RCVoiceRoomEngine.getInstance().cancelPKInvitation(roomId, userId, new RCVoiceRoomCallback() {
                        @Override
                        public void onSuccess() {
                            KToast.show("取消PK邀请成功！");
                        }

                        @Override
                        public void onError(int code, String message) {
                            KToast.show("取消PK邀请失败：【" + code + "】 msg = " + message);
                        }
                    });
                }
                break;
            case invite_pk_mute://静音pk者
                mutePkFlag = !mutePkFlag;
                RCVoiceRoomEngine.getInstance().mutePKUser(mutePkFlag, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.show(mutePkFlag ? "屏蔽音频成功！" : "取消屏蔽音频成功");
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (mutePkFlag) {
                            KToast.show("屏蔽音频失败：【" + code + "】 msg = " + message);
                        } else {
                            KToast.show("取消屏蔽音频成功：【" + code + "】 msg = " + message);
                        }
                    }
                });
                break;
            case invite_quit_pk://退出pk
                RCVoiceRoomEngine.getInstance().quitPK(new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.show("退出pk成功！");
                    }

                    @Override
                    public void onError(int code, String message) {
                        KToast.show("退出pk失败：【" + code + "】 msg = " + message);
                    }
                });
                break;
        }
    }

    /**
     * 处理麦位api调用
     */
    @Override
    public void handleSeatApi(ApiFun apiFun, int seatIndex, String userId) {
        switch (apiFun) {
            case seat_mute://麦位静音
                muteSeat(seatIndex, true, null);
                break;
            case seat_mute_un://取消静音
                muteSeat(seatIndex, false, null);
                break;
            case seat_lock://麦位锁定
                lockSeat(seatIndex, true, null);
                break;
            case seat_lock_un://取消锁定
                lockSeat(seatIndex, false, null);
                break;
            case seat_left://下麦
                leaveSeat(false, null);
                break;
            case seat_left_plugin:
                leaveSeat(true, null);
                break;
            case seat_enter://上麦
                enterSeat(seatIndex, false, null);
                break;
            case seat_enter_plugin:
                enterSeat(seatIndex, true, null);
                break;
            case seat_request://请求上麦
                requestSeat(null);
                break;
            case seat_request_cancel://取消请求上麦
                cancelRequestSeat(null);
                break;
            case seat_pick_out://抱下麦
                RCVoiceRoomEngine.getInstance().kickUserFromSeat(
                        userId, new DefauRoomCallback("invitedIntoSeat", "抱下麦", null));
                break;
            case seat_close_mic://关闭本地麦克风
                RCVoiceRoomEngine.getInstance().disableAudioRecording(true);
                break;
            case seat_open_mic://打开本地麦克风
                RCVoiceRoomEngine.getInstance().disableAudioRecording(false);
                break;
            case seat_switch:
                jumpTo(seatIndex, false, null);
                break;
            case seat_switch_plugin:
                jumpTo(seatIndex, true, null);
                break;
            case seat_update:
                updateSeatInfo(seatIndex, false, null);
                break;
            case seat_update_plugin:
                updateSeatInfo(seatIndex, true, null);
                break;
        }
    }

    public void updateSeatInfo(int seatIndex, boolean plugin, IResultBack<Boolean> resultBack) {
        if (plugin) {
            RCVoiceSeatInfo seat = new RCVoiceSeatInfo();
            seat.setMute(true);
            seat.setExtra("");
            RCVoiceRoomEngine.getPlugin().updateSeatInfo(seatIndex, seat, new DefauRoomCallback("updateSeatInfo", "修改扩展信息", resultBack));
        } else {
            RCVoiceRoomEngine.getInstance().updateSeatInfo(seatIndex, "", new DefauRoomCallback("updateSeatInfo", "修改扩展信息", resultBack));
        }
    }

    public void jumpTo(int seatIndex, boolean plugin, IResultBack<Boolean> resultBack) {
        if (plugin) {
            RCVoiceRoomEngine.getPlugin().switchSeatTo(seatIndex, true, true, new DefauRoomCallback("jumpTo", "跳麦", resultBack));
        } else {
            RCVoiceRoomEngine.getInstance().switchSeatTo(seatIndex, new DefauRoomCallback("jumpTo", "跳麦", resultBack));
        }
    }

    // 邀请上麦
    public void invitedEnterSeat(String userId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().pickUserToSeat(
                userId, new DefauRoomCallback("invitedIntoSeat", "邀请上麦", resultBack));
    }

    @Override
    public RCVoiceRoomInfo getRoomInfo() {
        return roomInfo;
    }

    @Override
    public void createAndJoin(String roomId, RCVoiceRoomInfo roomInfo, IResultBack<Boolean> resultBack) {
        if (TextUtils.isEmpty(roomId)) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        if (null == roomInfo || TextUtils.isEmpty(roomInfo.getRoomName()) || roomInfo.getSeatCount() < 1) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }

        RCRTCConfig config = RCRTCConfig
                .Builder
                .create()
                .enableHardwareDecoder(true)
                .enableHardwareEncoder(true)
                .setAudioSource(MediaRecorder.AudioSource.MIC).build();
        RCVoiceRoomEngine.getInstance().createAndJoinRoom(config, roomId, roomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                if (null != resultBack) resultBack.onResult(true);
                KToast.show("crateAndJoin#onSuccess");
            }

            @Override
            public void onError(int code, String message) {
                String info = "crateAndJoin#onError [" + code + "]:" + message;
                KToast.show(info);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    @Override
    public void joinRoom(String roomId, IResultBack<Boolean> resultBack) {
        if (TextUtils.isEmpty(roomId)) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        RCRTCConfig config = RCRTCConfig
                .Builder
                .create().build();
        RCVoiceRoomEngine.getInstance().joinRoom(config, roomId, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.show("joinRoom#onSuccess");
                if (null != resultBack) resultBack.onResult(true);
            }

            @Override
            public void onError(int code, String message) {
                String info = "joinRoom#onError [" + code + "]:" + message;
                Logger.e(TAG, info);
                KToast.show(info);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    @Override
    public void leaveRoom(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.show("leaveRoom#onSuccess");
                if (null != resultBack) resultBack.onResult(true);
            }

            @Override
            public void onError(int code, String message) {
                String info = "leaveRoom#onError [" + code + "]:" + message;
                KToast.show(info);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    @Override
    public void lockAll(boolean locked) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(locked, null);
        KToast.show(locked ? "全麦锁定成功" : "全麦解锁成功");
    }

    @Override
    public void lockSeat(int index, boolean locked, IResultBack<Boolean> resultBack) {
        String action = locked ? "麦位锁定" : "取消麦位解锁";
        RCVoiceRoomEngine.getInstance().lockSeat(index, locked,
                new DefauRoomCallback("muteSeat", action, resultBack));
    }


    @Override
    public void muteAll(boolean mute) {
        String action = mute ? "全麦静音" : "全麦取消静音";
        RCVoiceRoomEngine.getInstance().muteOtherSeats(mute, new DefauRoomCallback("muteAll", action, null));
    }

    @Override
    public void muteSeat(int index, boolean mute, IResultBack<Boolean> resultBack) {
        String action = mute ? "麦位静音" : "取消麦位静音";
        RCVoiceRoomEngine.getInstance().muteSeat(index, mute,
                new DefauRoomCallback("muteSeat", action, resultBack));
    }

    @Override
    public void leaveSeat(boolean plugin, IResultBack<Boolean> resultBack) {
        if (plugin) {
            RCVoiceRoomEngine.getPlugin().leaveSeat(true, true, new DefauRoomCallback("leaveSeat", "下麦", resultBack));
        } else {
            RCVoiceRoomEngine.getInstance().leaveSeat(new DefauRoomCallback("leaveSeat", "下麦", resultBack));
        }
    }

    @Override
    public void enterSeat(int index, boolean plugin, IResultBack<Boolean> resultBack) {
        if (plugin) {
            RCVoiceSeatInfo seat = new RCVoiceSeatInfo();
            // 默认为false，如不修改该属性，会将默认值false赋值给目标麦位
            seat.setMute(true);
            seat.setExtra("麦上啦");
            RCVoiceRoomEngine.getPlugin().enterSeat(index, seat, new DefauRoomCallback("enterSeat", "上麦", resultBack));
        } else {
            RCVoiceRoomEngine.getInstance().enterSeat(index, new DefauRoomCallback("enterSeat", "上麦", resultBack));
        }
    }

    @Override
    public void requestSeat(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().requestSeat(
                new DefauRoomCallback("requestSeat", "请求排麦", resultBack));
    }

    @Override
    public void cancelRequestSeat(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().cancelRequestSeat(
                new DefauRoomCallback("cancelRequestSeat", "取消排麦", resultBack));
    }

    @Override
    public void acceptRequestSeat(String userId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().acceptRequestSeat(userId,
                new DefauRoomCallback("acceptRequestSeat", "同意排麦请求", resultBack));
    }

    @Override
    public void rejectRequestSeat(String userId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().rejectRequestSeat(userId,
                new DefauRoomCallback("rejectRequestSeat", "拒绝排麦请求", resultBack));
    }

    @Override
    public void updateSeatExtra(int seatIndex, String extra, IResultBack<Boolean> resultBack) {
        Logger.e("_QuickEventListener", "updateSeatExtra: index= " + seatIndex + " extra = " + extra);
        RCVoiceRoomEngine.getInstance().updateSeatInfo(seatIndex, extra,
                new DefauRoomCallback("updateSeatExtra", "更新扩展属性", resultBack));
    }

    @Override
    public void updateSeatCount(int count, IResultBack<Boolean> resultBack) {
        roomInfo.setSeatCount(count);
        updateRoomInfo(roomInfo.clone(), resultBack);
    }

    @Override
    public void updateRoomName(String name, IResultBack<Boolean> resultBack) {
        roomInfo.setRoomName(name);
        updateRoomInfo(roomInfo.clone(), resultBack);
    }

    /**
     * 为避免重置未修改属性，建议跟新和创建时传入相同的对象，
     *
     * @param roomInfo
     * @param resultBack
     */
    private void updateRoomInfo(RCVoiceRoomInfo roomInfo, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().setRoomInfo(roomInfo, new DefauRoomCallback("updateRoomInfo", resultBack));
    }


    /**
     * 默认回调
     */
    private static class DefauRoomCallback implements RCVoiceRoomCallback {
        private IResultBack<Boolean> resultBack;
        private String methodName;
        private String action;

        DefauRoomCallback(String methodName, IResultBack<Boolean> resultBack) {
            this(methodName, "", resultBack);
        }

        DefauRoomCallback(String methodName, String action, IResultBack<Boolean> resultBack) {
            this.resultBack = resultBack;
            this.action = action;
            this.methodName = TextUtils.isEmpty(methodName) ? "DefauRoomCallback" : methodName;
        }

        @Override
        public void onSuccess() {
            Logger.d("QuickEventListener", action + "成功");
            if (null != resultBack) resultBack.onResult(true);
            if (!TextUtils.isEmpty(action)) KToast.show(action + "成功", false);
        }

        @Override
        public void onError(int i, String s) {
            Logger.e("QuickEventListener", action + "失败");
            if (!TextUtils.isEmpty(action)) KToast.show(action + "失败", false);
            Log.e(TAG, methodName + "#onError [" + i + "]:" + s);
            if (null != resultBack) resultBack.onResult(false);
        }

        @Override
        public void onError(int code, IError error) {
            RCVoiceRoomCallback.super.onError(code, error);
        }
    }
}
