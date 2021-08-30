package cn.rongcloud.quickdemo;

import android.view.View;

import cn.rongcloud.quickdemo.uitls.Api;
import cn.rongcloud.quickdemo.uitls.VoiceRoomApi;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;

/**
 * 演示join房间activity
 * 1.加入房间
 * 2.ActionBar不显示房间api的action
 */
public class JoinActivity extends BaseApiActivity implements View.OnClickListener, ApiFunDialogHelper.OnApiClickListener {
    private final static String JOIN = "Join";
    private final static String CLOSE = "Close";

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
            ApiFunDialogHelper.helper().showEditorDialog(this, "房间ID", new Api.IResultBack<String>() {
                @Override
                public void onResult(String result) {
                    join("10010R");
//                    join(result);
                }
            });
        } else {
            VoiceRoomApi.getApi().leaveRoom(new Api.IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) create_and_join.setText(JOIN);
                    resetAnable();
                }
            });
        }
    }

    private void join(String roomId) {
        VoiceRoomApi.getApi().joinRoom(roomId, new Api.IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) create_and_join.setText(CLOSE);
            }
        });
    }
}