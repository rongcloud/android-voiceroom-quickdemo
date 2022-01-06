package cn.rongcloud.quickdemo.interfaces;

import cn.rongcloud.quickdemo.ApiFun;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

/**
 * 语聊房api封装接口
 */
public interface Api {
    String EVENT_REJECT_PICK = "VoiceRoom_RejectManagePick";// 拒绝上麦
    String EVENT_AGREE_PICK = "VoiceRoom_AgreeManagePick";// 同意上麦

    /**
     * 处理房间api调用
     *
     * @param apiFun
     * @param userId
     */
    void handleRoomApi(ApiFun apiFun, String userId,String roomId);

    /**
     * 处理麦位api调用
     *
     * @param apiFun
     * @param seatIndex
     * @param userId
     */
    void handleSeatApi(ApiFun apiFun, int seatIndex,String userId);

    RCVoiceRoomInfo getRoomInfo();

    void createAndJoin(String roomId, RCVoiceRoomInfo roomInfo, IResultBack<Boolean> resultBack);

    void joinRoom(String roomId, IResultBack<Boolean> resultBack);

    void leaveRoom(IResultBack<Boolean> resultBack);

    /**
     * 全麦锁定
     */
    void lockAll(boolean locked);

    void lockSeat(int index, boolean locked, IResultBack<Boolean> resultBack);

    /**
     * 全麦静音
     */
    void muteAll(boolean mute);


    void muteSeat(int index, boolean mute, IResultBack<Boolean> resultBack);

    void leaveSeat(IResultBack<Boolean> resultBack);

    void enterSeat(int index, IResultBack<Boolean> resultBack);

    void requestSeat(IResultBack<Boolean> resultBack);

    void cancelRequestSeat(IResultBack<Boolean> resultBack);

    void acceptRequestSeat(String userId, IResultBack<Boolean> resultBack);

    void rejectRequestSeat(String userId, IResultBack<Boolean> resultBack);

    void updateSeatExtra(int seatIndex, String extra, IResultBack<Boolean> resultBack);

    /**
     * 跟新麦位count
     *
     * @param count
     * @param resultBack
     */
    void updateSeatCount(int count, IResultBack<Boolean> resultBack);

    /**
     * 跟新房间名称
     *
     * @param name
     * @param resultBack
     */
    void updateRoomName(String name, IResultBack<Boolean> resultBack);

}
