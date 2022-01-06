package cn.rongcloud.authentication;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.kit.utils.Logger;

import java.util.HashMap;
import java.util.Map;

import cn.rongcloud.authentication.bean.Account;
import cn.rongcloud.oklib.LoadTag;
import cn.rongcloud.oklib.OkApi;
import cn.rongcloud.oklib.WrapperCallBack;
import cn.rongcloud.oklib.wrapper.Wrapper;
import cn.rongcloud.quickdemo.AbsPermissionActivity;
import cn.rongcloud.quickdemo.R;
import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.GsonUtil;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.UIKit;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;

/**
 * 登录
 */
public class LoginActivity extends AbsPermissionActivity {

    @Override
    protected String[] onCheckPermission() {
        return VOICE_PERMISSIONS;
    }

    @Override
    protected void onPermissionAccept(boolean accept) {
        if (accept) {
            initView();
        }
    }

    private EditText et_phone;

    void initView() {
        setContentView(R.layout.activity_login);
        et_phone = findViewById(R.id.et_phone);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    KToast.showToast("请输入手机号");
                    return;
                }
                login(phone, "111111");
            }
        });
    }

    /**
     * 使用融云测试服务器 获取连接融云IM 服务器的token
     *
     * @param phone 电话token 获取businessToken 的手机号
     * @param code  验证码
     */
    void login(String phone, String code) {
        LoadTag tag = new LoadTag(activity, "login...");
        tag.show();
        Map<String, Object> params = new HashMap<>(4);
        params.put("mobile", phone);
        params.put("verifyCode", code);
        OkApi.post(Api.LOGIN, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null != tag) tag.dismiss();
                Logger.e("result = " + GsonUtil.obj2Json(result));
                if (result.getCode() == 10000) {
                    Account account = result.get(Account.class);
                    if (null != account) {
                        QuickApplication.setAuthorization(account.getAuthorization());
                        AccoutManager.setAcctount(account,true);
                        connect(account);
                    }
                }
            }
        });
    }

    /**
     * 连接 融云IM 服务
     *
     * @param account 账号信息
     */
    private void connect(Account account) {
        //先断开连接
        RCVoiceRoomEngine.getInstance().disconnect(false);
        //连接
        RCVoiceRoomEngine.getInstance().connectWithToken(account.getImToken(), new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        UIKit.startActivity(LoginActivity.this, RoomListActivity.class);
                    }

                    @Override
                    public void onError(int code, String s) {
                        String info = "connect fail：\n【" + code + "】" + s;
                        Log.e("ConnectActivity", info);
                        KToast.showToast(info);
                    }
                }
        );
    }

}
