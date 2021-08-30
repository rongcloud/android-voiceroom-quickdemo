package cn.rongcloud.quickdemo;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.Api;
import cn.rongcloud.quickdemo.uitls.GsonUtil;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.VoiceRoomApi;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.api.IRCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
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
    private WeakReference<Activity> activity;
    private List<RCVoiceSeatInfo> mSeatInfos;
    private List<String> mAudienceIds;
    private RCVoiceRoomInfo roomInfo;
    private QuickEventListener() {
    }

    public static QuickEventListener get() {
        return listener;
    }

    public boolean isInialized() {
        return null != reference && null != reference.get();
    }

    public QuickEventListener setVoiceRoomEngine(Activity activity, IRCVoiceRoomEngine engine) {
        this.activity = new WeakReference<>(activity);
        reference = new WeakReference<>(engine);
        engine.setVoiceRoomEventListener(this);
        mSeatInfos = new ArrayList<>();
        mAudienceIds = new ArrayList<>();
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

    public List<String> getAudienceIds() {
        return mAudienceIds;
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
    public void onRoomInfoUpdate(RCVoiceRoomInfo room) {
        this.roomInfo = room;
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
    }

    @Override
    public void onSeatLock(int index, boolean locked) {
        Log.d(TAG, "onSeatLock: index = " + index + " locked = " + locked);
    }

    @Override
    public void onAudienceEnter(String userId) {
        Log.d(TAG, "onAudienceEnter: userId = " + userId);
        //添加关注id
        if (null != mAudienceIds && !mAudienceIds.contains(userId)) {
            mAudienceIds.add(userId);
        }
    }

    @Override
    public void onAudienceExit(String userId) {
        Log.d(TAG, "onAudienceExit: userId = " + userId);
        // 移除观众id
        if (null != mAudienceIds) mAudienceIds.remove(userId);
    }

    @Override
    public void onSpeakingStateChanged(int index, boolean speaking) {
//        Log.d(TAG, "onSpeakingStateChanged: index = " + index + " speaking = " + speaking);
    }

    @Override
    public void onMessageReceived(Message message) {
//        Log.v(TAG, "onMessageReceived: " + GsonUtil.obj2Json(message));
    }

    /**
     * 房间通知回调
     *
     * @param name
     * @param content
     */
    @Override
    public void onRoomNotificationReceived(String name, String content) {
//        Log.d(TAG, "onRoomNotificationReceived: name = " + name + " content = " + content);
    }

    /**
     * 被抱上麦回调
     *
     * @param userId 邀请人的id
     */
    @Override
    public void onPickSeatReceivedFrom(String userId) {
        Log.d(TAG, "onPickSeatReceivedFrom: userId = " + userId);
        if (null != activity && null != activity.get()) {
            String name = AccoutManager.getAccoutName(userId);
            ApiFunDialogHelper.helper().showTipDialog(activity.get(), "邀请提示", "'" + name + "'邀请您上麦，是否接收", new Api.IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        //同意
                        RCVoiceRoomEngine.getInstance()
                                .notifyVoiceRoom(Api.EVENT_AGREE_PICK, AccoutManager.getCurrentId());
                        //获取可用麦位索引
                        int availableIndex = -1;
                        for (int i = 0; i < mSeatInfos.size(); i++) {
                            RCVoiceSeatInfo seat = mSeatInfos.get(i);
                            if (RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty == seat.getStatus()) {
                                availableIndex = i;
                                break;
                            }
                        }
                        if (availableIndex > -1) {
                            VoiceRoomApi.getApi().enterSeat(availableIndex, null);
                        } else {
                            KToast.showToast("当前没有空余的麦位");
                        }
                    } else {//拒绝
                        RCVoiceRoomEngine.getInstance()
                                .notifyVoiceRoom(Api.EVENT_REJECT_PICK, AccoutManager.getCurrentId());
                    }

                }
            });
        }
    }

    /**
     * 被踢下麦回调
     *
     * @param index 麦位索引
     */
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