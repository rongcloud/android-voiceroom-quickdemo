package cn.rongcloud.quickdemo;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.quickdemo.uitls.GsonUtil;
import cn.rongcloud.voiceroom.api.IRCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.model.Message;

public class QuickEventListener implements RCVoiceRoomEventListener {

    public interface SeatListObserver {
        void onSeatList(List<RCVoiceSeatInfo> seatInfos);
    }

    public interface RoomInforObserver {
        void onRoomInfo(RCVoiceRoomInfo roomInfo);
    }

    public interface SeatObserver {
        void onSeat(int index, RCVoiceSeatInfo info);
    }

    private SeatListObserver seatListObserver;
    private RoomInforObserver roomInforObserver;
    private SeatObserver seatObserver;
    private static String TAG = "QuickEventListener";
    private static QuickEventListener listener = new QuickEventListener();
    //设置监听标识
    private WeakReference<IRCVoiceRoomEngine> reference;
    private List<RCVoiceSeatInfo> mSeatInfos;

    private QuickEventListener() {
    }

    public static QuickEventListener get() {
        return listener;
    }

    public boolean isInialized() {
        return null != reference && null != reference.get();
    }

    public QuickEventListener setVoiceRoomEngine(IRCVoiceRoomEngine engine) {
        if (null != engine) {
            reference = new WeakReference<>(engine);
            engine.setVoiceRoomEventListener(this);
        }
        mSeatInfos = new ArrayList<>();
        return this;
    }

    public void observeSeatList(SeatListObserver observer) {
        this.seatListObserver = observer;
    }

    public void observeSeat(SeatObserver observer) {
        this.seatObserver = observer;
    }


    public void observeRoomInfo(RoomInforObserver observer) {
        this.roomInforObserver = observer;
    }

    private RCVoiceSeatInfo getSeatInfo(int index) {
        int count = mSeatInfos.size();
        if (index > -1 && index < count) {
            return mSeatInfos.get(index);
        }
        return null;
    }

    @Override
    public void onRoomKVReady() {
        Log.d(TAG, "onRoomKVReady");
    }

    @Override
    public void onRoomInfoUpdate(RCVoiceRoomInfo roomInfo) {
        Log.d(TAG, "onRoomInfoUpdate:" + GsonUtil.obj2Json(roomInfo));
        if (null != roomInforObserver) roomInforObserver.onRoomInfo(roomInfo);
    }

    @Override
    public void onSeatInfoUpdate(List<RCVoiceSeatInfo> list) {
        int count = list.size();
        Log.d(TAG, "onSeatInfoUpdate: count = " + count);
        for (int i = 0; i < count; i++) {
            RCVoiceSeatInfo info = list.get(i);
            Log.d(TAG, "index = " + i + "  " + GsonUtil.obj2Json(info));
        }
        mSeatInfos.clear();
        mSeatInfos.addAll(list);
        if (null != seatListObserver) seatListObserver.onSeatList(list);
    }

    @Override
    public void onUserEnterSeat(int index, String userId) {
        Log.d(TAG, "onUserEnterSeat: index = " + index + " userId = " + userId);
    }

    @Override
    public void onUserLeaveSeat(int index, String userId) {
        Log.d(TAG, "onUserLeaveSeat: index = " + index + " userId = " + userId);
    }

    @Override
    public void onSeatMute(int index, boolean mute) {
        Log.d(TAG, "onSeatMute: index = " + index + " mute = " + mute);
//        RCVoiceSeatInfo seatInfo = getSeatInfo(index);
//        if (null != seatInfo) {
//            seatInfo.setMute(mute);
//        }
//        if (null != seatListObserver) seatListObserver.onSeatList(mSeatInfos);
    }

    @Override
    public void onSeatLock(int index, boolean locked) {
        Log.d(TAG, "onSeatLock: index = " + index + " locked = " + locked);
        RCVoiceSeatInfo seatInfo = getSeatInfo(index);
//        if (null != seatInfo) {
//            seatInfo.setStatus(
//                    locked ? RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking
//                            : RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty);
//        }
//        if (null != seatListObserver) seatListObserver.onSeatList(mSeatInfos);
    }

    @Override
    public void onAudienceEnter(String userId) {
        Log.d(TAG, "onAudienceEnter: userId = " + userId);
    }

    @Override
    public void onAudienceExit(String userId) {
        Log.d(TAG, "onAudienceExit: userId = " + userId);
    }

    @Override
    public void onSpeakingStateChanged(int index, boolean speaking) {
//        Log.d(TAG, "onSpeakingStateChanged: index = " + index + " speaking = " + speaking);
    }

    @Override
    public void onMessageReceived(Message message) {
        Log.v(TAG, "onMessageReceived: " + GsonUtil.obj2Json(message));
    }

    @Override
    public void onRoomNotificationReceived(String name, String content) {
        Log.d(TAG, "onRoomNotificationReceived: name = " + name + " content = " + content);
    }

    @Override
    public void onPickSeatReceivedFrom(String userId) {
        Log.d(TAG, "onPickSeatReceivedFrom: userId = " + userId);
    }

    @Override
    public void onKickSeatReceived(int index) {
        Log.d(TAG, "onPickSeatReceivedFrom: index = " + index);
    }

    /**
     * 房主或管理员 接受同意 用户的排麦申请
     */
    @Override
    public void onRequestSeatAccepted() {

    }

    /**
     * 发送的排麦请求被房主或管理员拒绝
     */
    @Override
    public void onRequestSeatRejected() {

    }

    /**
     * 排麦列表发生变化
     */
    @Override
    public void onRequestSeatListChanged() {

    }

    /**
     * 收到上麦邀请
     *
     * @param invitationId 邀请标识 Id
     * @param userId       发送邀请用户的标识
     * @param content      邀请内容 （用户可以自定义）
     */
    @Override
    public void onInvitationReceived(String invitationId, String userId, String content) {

    }

    /**
     * 邀请被接受通知
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationAccepted(String invitationId) {

    }

    /**
     * 邀请被拒绝回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationRejected(String invitationId) {

    }

    /**
     * 邀请被取消回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationCancelled(String invitationId) {

    }

    /**
     * 被踢出房间回调
     *
     * @param targetId 被踢用户的标识
     * @param userId   发起踢人用户的标识
     */
    @Override
    public void onUserReceiveKickOutRoom(String targetId, String userId) {
        Log.d(TAG, "onUserReceiveKickOutRoom: targetId = " + targetId);
    }
}