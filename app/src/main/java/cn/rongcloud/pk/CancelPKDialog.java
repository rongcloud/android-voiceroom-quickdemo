package cn.rongcloud.pk;

import android.app.Activity;
import android.view.View;

import com.kit.UIKit;
import com.kit.utils.Logger;

import cn.rongcloud.quickdemo.R;
import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;

/**
 * 取消PK邀请弹框
 */
public class CancelPKDialog extends BottomDialog implements View.OnClickListener {
    private IResultBack<Boolean> resultBack;
    private String roomId;
    private String userId;

    public CancelPKDialog(Activity activity, String roomId, String userId, IResultBack<Boolean> resultBack) {
        super(activity);
        this.resultBack = resultBack;
        this.roomId = roomId;
        this.userId = userId;
        setContentView(R.layout.layout_cancelpk_dialog, 25);
        initView();
    }

    private void initView() {
        UIKit.getView(getContentView(), R.id.cancele_pk).setOnClickListener(this);
        UIKit.getView(getContentView(), R.id.cancel_dialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.cancele_pk == id) {
            Logger.e("CancelPKDialog","roomId = "+roomId);
            Logger.e("CancelPKDialog","userId = "+userId);
            RCVoiceRoomEngine.getInstance().cancelPKInvitation(roomId, userId, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    KToast.showToast("取消pk邀请成功");
                    if (null != resultBack) resultBack.onResult(true);
                }

                @Override
                public void onError(int i, String s) {
                    KToast.showToast("取消pk邀请失败");
                    if (null != resultBack) resultBack.onResult(false);
                }
            });
            dismiss();
        } else if (R.id.cancel_dialog == id) {
            dismiss();
            if (null != resultBack) resultBack.onResult(false);
        }
    }
}
