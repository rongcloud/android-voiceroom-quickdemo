package cn.rongcloud.authentication;

import com.kit.UIKit;
import com.kit.utils.Logger;


import cn.rongcloud.voicequickdemo.AbsPermissionActivity;
import cn.rongcloud.voicequickdemo.R;


public class FlashActivity extends AbsPermissionActivity {

    @Override
    protected String[] onCheckPermission() {
        return VOICE_PERMISSIONS;
    }

    @Override
    protected void onPermissionAccept(boolean accept) {
        Logger.e("FlashActivity","accept = "+accept);
        if (accept) {
            initView();
        }
    }

    void initView() {
        setContentView(R.layout.activity_flash);
        UIKit.startActivity(this, LoginActivity.class);
        finish();
    }
}
