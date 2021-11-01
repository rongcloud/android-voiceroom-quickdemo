package cn.rongcloud.quickdemo;

import android.text.TextUtils;
import android.view.View;

import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;

/**
 * 演示join房间activity
 * 1.加入房间
 * 2.ActionBar不显示房间api的action
 */
public class JoinActivity extends BaseApiActivity implements View.OnClickListener, ApiFunDialogHelper.OnApiClickListener {
    private final static String JOIN = "Join";

    @Override
    protected void initCustomer() {
        create_and_join.setText("Join");
    }

    @Override
    boolean showRoomApiAction() {
        // 不显示action
        return false;
    }

    @Override
    void handleJoinOrCreateAndJoin() {
        if (JOIN.equals(create_and_join.getText().toString().trim())) {
            ApiFunDialogHelper.helper().showEditorDialog(this, "加入房间ID", new IResultBack<String>() {
                @Override
                public void onResult(String result) {
                    join(result);
                }
            });
        } else {
            VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) create_and_join.setText(JOIN);
                    resetAnable();
                    finish();
                }
            });
        }
    }

    private void join(String roomId) {
        if (TextUtils.isEmpty(roomId)) {
            roomId = TEST_ROOM_ID;
        }

        VoiceRoomApi.getApi().joinRoom(roomId, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) create_and_join.setText(LEFT_ROOM);
            }
        });
    }
}