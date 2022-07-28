package cn.rongcloud.authentication;

import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.kit.UIKit;
import com.kit.cache.GsonUtil;
import com.kit.utils.KToast;
import com.kit.utils.Logger;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.rongcloud.authentication.bean.Account;
import cn.rongcloud.example.buffer.IBuffer;
import cn.rongcloud.example.buffer.RCRefreshBuffer;
import cn.rongcloud.oklib.LoadTag;
import cn.rongcloud.oklib.OkApi;
import cn.rongcloud.oklib.WrapperCallBack;
import cn.rongcloud.oklib.wrapper.Wrapper;
import cn.rongcloud.voicequickdemo.AbsPermissionActivity;
import cn.rongcloud.voicequickdemo.R;
import cn.rongcloud.voicequickdemo.uitls.AccoutManager;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;

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
        Logger.e(TAG, "accept = " + accept);
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
                    KToast.show("请输入手机号");
                    return;
                }
                login(phone, "111111");
            }
        });

//        testBuffer();
    }

    /**
     * 使用融云测试服务器 获取连接融云IM 服务器的token
     *
     * @param phone 电话token 获取businessToken 的手机号
     * @param code  验证码
     */
    void login(String phone, String code) {
        LoadTag tag = new LoadTag(activity, "登录...");
        tag.show();
        Map<String, Object> params = new HashMap<>(4);
        params.put("mobile", phone);
        params.put("verifyCode", code);
        params.put("deviceId", getDeviceId());
        OkApi.post(Api.LOGIN, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null != tag) tag.dismiss();
                Logger.e("result = " + GsonUtil.obj2Json(result));
                if (result.getCode() == 10000) {
                    Account account = result.get(Account.class);
                    if (null != account) {
                        QuickApplication.setAuthorization(account.getAuthorization());
                        AccoutManager.setAcctount(account, true);
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
        RongCoreClient.getInstance().disconnect(false);
        //连接
        String token = account.getImToken();
        Logger.e(TAG,"token = "+token);
        RongCoreClient.connect(token,
                new IRongCoreCallback.ConnectCallback() {
                    @Override
                    public void onSuccess(String t) {
                        KToast.show("connect success");
                        Log.e("ConnectActivity", "connect success");
                        UIKit.startActivity(LoginActivity.this, RoomListActivity.class);
                        finish();
                    }

                    @Override
                    public void onError(IRongCoreEnum.ConnectionErrorCode e) {
                        String info = "connect fail：\n【" + e.getValue() + "】" + e.name();
                        Log.e("ConnectActivity", info);
                        KToast.show(info);
                    }

                    @Override
                    public void onDatabaseOpened(IRongCoreEnum.DatabaseOpenStatus code) {

                    }
                }
        );
    }

    static String getDeviceId() {
        String deviceIdShort =
                "35" + (Build.BOARD.length() % 10) +
                        (Build.BRAND.length() % 10) +
                        (Build.CPU_ABI.length() % 10) +
                        (Build.DEVICE.length() % 10) +
                        (Build.MANUFACTURER.length() % 10) +
                        (Build.MODEL.length() % 10) +
                        (Build.PRODUCT.length() % 10) +
                        (Build.SERIAL.length() % 10);
        return UUID.nameUUIDFromBytes(deviceIdShort.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private void testBuffer() {
        Logger.e(TAG, "onOutflow: start loop");
        RCRefreshBuffer<String> buffer = new RCRefreshBuffer<>(1000);
//        buffer.setInterval(1000);
        buffer.setSize(30);
        buffer.setOnOutflowListener(new IBuffer.OnOutflowListener<String>() {
            @Override
            public void onOutflow(List<String> data) {
                StringBuilder builder = new StringBuilder();
                int count = null == data ? 0 : data.size();
                Logger.e(TAG, "onOutflow: count = " + count);
                for (int i = 0; i < count; i++) {
                    builder.append(data.get(i));
                    builder.append("  ");
                }
                Logger.e(TAG, "onOutflow: " + builder.toString());
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "onOutflow: start apply");
                for (int i = 0; i < 1000; i++) {
                    buffer.apply("test_" + i);
                    SystemClock.sleep(50);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "onOutflow: start apply");
                for (int i = 1000; i < 2000; i++) {
                    buffer.apply("test_" + i);
                    SystemClock.sleep(50);
                }
            }
        }).start();
    }
}
