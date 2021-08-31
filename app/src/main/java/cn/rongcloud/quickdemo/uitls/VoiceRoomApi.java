package cn.rongcloud.quickdemo.uitls;

import android.text.TextUtils;
import android.util.Log;

import cn.rongcloud.quickdemo.ApiFun;
import cn.rongcloud.quickdemo.interfaces.Api;
import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

public class VoiceRoomApi implements Api {
    private final static String TAG = "VoiceRoomApi";
    private final static Api api = new VoiceRoomApi();
    private final RCVoiceRoomInfo roomInfo = new RCVoiceRoomInfo();

    private VoiceRoomApi() {
    }

    public static Api getApi() {
        return api;
    }

    /**
     * 处理房间api调用
     *
     * @param apiFun
     */
    @Override
    public void handleRoomApi(ApiFun apiFun, String userId) {
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
                if (name.contains("_")) {
                    String[] arr = name.split("_");
                    String pre = arr[0];
                    String id = arr[1];
                    if ("跟新".equals(pre)) {
                        name = "Room_" + id;
                    } else {
                        name = "跟新_" + id;
                    }
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
                leaveSeat(null);
                break;
            case seat_enter://上麦
                enterSeat(seatIndex, null);
                break;
            case seat_request://请求上麦
                requestSeat(null);
                break;
            case seat_request_cancel://取消请求上麦
                cancelRequestSeat(null);
                break;
            case seat_extra://扩展属性
                updateSeatExtra(seatIndex, "附加" + seatIndex, null);
                break;
            case seat_pick_out://抱下麦
                RCVoiceRoomEngine.getInstance().kickUserFromSeat(
                        userId, new DefauRoomCallback("invitedIntoSeat", "抱下麦", null));
                break;
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
        RCVoiceRoomEngine.getInstance().createAndJoinRoom(roomId, roomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.showToastWithLag(TAG, "crateAndJoin#onSuccess");
                //房主上麦 index = 0
                VoiceRoomApi.getApi().enterSeat(0, resultBack);
            }

            @Override
            public void onError(int code, String message) {
                String info = "crateAndJoin#onError [" + code + "]:" + message;
                KToast.showToastWithLag(TAG, info);
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
        RCVoiceRoomEngine.getInstance().joinRoom(roomId, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.showToastWithLag(TAG, "joinRoom#onSuccess");
                if (null != resultBack) resultBack.onResult(true);
            }

            @Override
            public void onError(int code, String message) {
                String info = "joinRoom#onError [" + code + "]:" + message;
                KToast.showToastWithLag(TAG, info);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    @Override
    public void leaveRoom(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.showToastWithLag(TAG, "leaveRoom#onSuccess");
                if (null != resultBack) resultBack.onResult(true);
            }

            @Override
            public void onError(int code, String message) {
                String info = "leaveRoom#onError [" + code + "]:" + message;
                KToast.showToastWithLag(TAG, info);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    @Override
    public void lockAll(boolean locked) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(locked);
        KToast.showToastWithLag(TAG, locked ? "全麦锁定成功" : "全麦解锁成功");
    }

    @Override
    public void lockSeat(int index, boolean locked, IResultBack<Boolean> resultBack) {
        String action = locked ? "麦位锁定" : "取消麦位解锁";
        RCVoiceRoomEngine.getInstance().lockSeat(index, locked,
                new DefauRoomCallback("muteSeat", action, resultBack));
    }


    @Override
    public void muteAll(boolean mute) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(mute);
        KToast.showToastWithLag(TAG, mute ? "全麦静音成功" : "全麦取消静音成功");
    }

    @Override
    public void muteSeat(int index, boolean mute, IResultBack<Boolean> resultBack) {
        String action = mute ? "麦位静音" : "取消麦位静音";
        RCVoiceRoomEngine.getInstance().muteSeat(index, mute,
                new DefauRoomCallback("muteSeat", action, resultBack));
    }

    @Override
    public void leaveSeat(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().leaveSeat(new DefauRoomCallback("leaveSeat", "下麦", resultBack));
    }

    @Override
    public void enterSeat(int index, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().enterSeat(index,
                new DefauRoomCallback("enterSeat", "上麦", resultBack));
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
        RCVoiceRoomEngine.getInstance().updateSeatInfo(seatIndex, extra,
                new DefauRoomCallback("updateSeatExtra", "更新扩展属性", resultBack));
    }

    @Override
    public void updateSeatCount(int count, IResultBack<Boolean> resultBack) {
        roomInfo.setSeatCount(count);
        updateRoomInfo(roomInfo, resultBack);
    }

    @Override
    public void updateRoomName(String name, IResultBack<Boolean> resultBack) {
        roomInfo.setRoomName(name);
        updateRoomInfo(roomInfo, resultBack);
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
            if (null != resultBack) resultBack.onResult(true);
            if (!TextUtils.isEmpty(action)) KToast.showToast(action + "成功");
        }

        @Override
        public void onError(int i, String s) {
            if (!TextUtils.isEmpty(action)) KToast.showToast(action + "失败");
            Log.e(TAG, methodName + "#onError [" + i + "]:" + s);
            if (null != resultBack) resultBack.onResult(false);
        }
    }
}
