package cn.rongcloud.quickdemo.uitls;

import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

/**
 * 语聊房api封装接口
 */
public interface Api {

    interface IResultBack<T> {
        void onResult(T result);
    }

    /**
     * 处理房间api调用
     *
     * @param index
     */
    void handleRoomApi(int index, String action);

    /**
     * 处理麦位api调用
     *
     * @param apiPosition
     * @param action
     * @param seatIndex
     */
    void handleSeatApi(int apiPosition, String action, int seatIndex);

    RCVoiceRoomInfo getRoomInfo();

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
