package cn.rongcloud.quickdemo.uitls;

import android.text.TextUtils;
import android.util.Log;

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


//"全麦锁定",
//"全麦解锁",
//"全麦静音",
//"取消全麦静音",
//"修改名称",
//"修改麦位数"

    /**
     * 处理房间api调用
     *
     * @param index
     */
    @Override
    public void handleRoomApi(int index, String action) {
        switch (index) {
            case 0://全麦锁定
                lockAll(true);
                break;
            case 1://全麦解锁
                lockAll(false);
                break;
            case 2://全麦静音
                muteAll(true);
                break;
            case 3://取消全麦静音
                muteAll(false);
                break;
            case 4://修改名称
                String name = roomInfo.getRoomName();
                if (name.contains("_")) {
                    String[] arr = name.split("_");
                    String pre = arr[0];
                    String id = arr[1];
                    if ("跟新".equals(pre)){
                        name = "Room_" + id;
                    }else {
                        name = "跟新_" + id;
                    }
                }
                updateRoomName(name, null);
                break;
            case 5://修改麦位数
                int count = roomInfo.getSeatCount() != 9 ? 9 : 5;
                updateSeatCount(count, null);
                break;
        }
    }

// "麦位静音",
// "取消静音",
// "麦位锁定",
// "取消锁定",
// "下麦",
// "上麦",
// "邀请上麦"
// "取消邀请上麦",
//  扩展属性

    /**
     * 处理麦位api调用
     */
    @Override
    public void handleSeatApi(int apiPosition, String action, int seatIndex) {
        switch (apiPosition) {
            case 0://麦位静音
                muteSeat(seatIndex, true, null);
                break;
            case 1://取消静音
                muteSeat(seatIndex, false, null);
                break;
            case 2://麦位锁定
                lockSeat(seatIndex, true, null);
                break;
            case 3://取消锁定
                lockSeat(seatIndex, false, null);
                break;
            case 4://下麦
                leaveSeat(null);
                break;
            case 5://上麦
                enterSeat(seatIndex, null);
                break;
            case 6://邀请上麦
                requestSeat(null);
                break;
            case 7://取消邀请上麦
                cancelRequestSeat(null);
                break;
            case 8://扩展属性
                updateSeatExtra(seatIndex, "扩展属性", null);
                break;
        }
    }

    @Override
    public RCVoiceRoomInfo getRoomInfo() {
        return roomInfo;
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
        RCVoiceRoomEngine.getInstance().lockSeat(index, mute,
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
