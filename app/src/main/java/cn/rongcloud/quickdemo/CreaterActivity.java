package cn.rongcloud.quickdemo;

import android.view.View;

import cn.rongcloud.quickdemo.uitls.Api;
import cn.rongcloud.quickdemo.uitls.VoiceRoomApi;
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
    private final static String CLOSE = "Close";

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
//            String roomId = "" + System.currentTimeMillis();
            String roomId = "10010R";
            String roomName = "Room_" + roomId;
            RCVoiceRoomInfo roomInfo = VoiceRoomApi.getApi().getRoomInfo();
            roomInfo.setRoomName(roomName);
            roomInfo.setSeatCount(DEF_SEAT_COUNT);
            roomInfo.setFreeEnterSeat(false);
            roomInfo.setLockAll(false);
            roomInfo.setMuteAll(false);
            VoiceRoomApi.getApi().createAndJoin(roomId, roomInfo, new Api.IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    setTitle(roomName);
                    if (result) create_and_join.setText(CLOSE);
                }
            });
        } else {
            VoiceRoomApi.getApi().leaveRoom(new Api.IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) create_and_join.setText(CREAT);
                    resetAnable();
                }
            });
        }
    }
}