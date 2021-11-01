package cn.rongcloud.quickdemo;

import android.text.TextUtils;
import android.view.View;

import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

/**
 * 演示创将房间activity
 * 1.创建并加入房间
 * 2.上麦 麦位index = 0
 */
public class CreaterActivity extends BaseApiActivity implements View.OnClickListener, ApiFunDialogHelper.OnApiClickListener {

    private final static int DEF_SEAT_COUNT = 5;
    private final static String CREAT = "Create And Join";

    @Override
    protected void initCustomer() {
        create_and_join.setText(CREAT);
    }

    @Override
    boolean showRoomApiAction() {
        return true;
    }

    @Override
    void handleJoinOrCreateAndJoin() {
        if (CREAT.equals(create_and_join.getText().toString().trim())) {
            ApiFunDialogHelper.helper().showEditorDialog(this, "新建房间ID", new IResultBack<String>() {
                @Override
                public void onResult(String result) {
                    createRoom(result);
                }
            });
        } else {
            VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) create_and_join.setText(CREAT);
                    resetAnable();
                    finish();
                }
            });
        }
    }

    private void createRoom(String roomId) {
        if (TextUtils.isEmpty(roomId)) {
            roomId = TEST_ROOM_ID;
        }
        String roomName = "Room_" + roomId;
        RCVoiceRoomInfo roomInfo = VoiceRoomApi.getApi().getRoomInfo();
        roomInfo.setRoomName(roomName);
        roomInfo.setSeatCount(DEF_SEAT_COUNT);
        roomInfo.setFreeEnterSeat(false);
        roomInfo.setLockAll(false);
        roomInfo.setMuteAll(false);
        VoiceRoomApi.getApi().createAndJoin(roomId, roomInfo, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                setTitle(roomName);
                if (result) create_and_join.setText(LEFT_ROOM);
            }
        });
    }
}