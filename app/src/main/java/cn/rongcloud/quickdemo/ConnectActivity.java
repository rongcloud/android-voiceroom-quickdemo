package cn.rongcloud.quickdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.interfaces.IAdapte;
import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.UIKit;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;

public class ConnectActivity extends AbsPermissionActivity {
    private RecyclerView rl_accout;

    @Override
    protected String[] onCheckPermission() {
        return VOICE_PERMISSIONS;
    }

    @Override
    protected void onPermissionAccept(boolean accept) {
        if (accept) {
            init();
        }
    }

    private void init() {
        setContentView(R.layout.activity_connect);
        setTitle("选择账号");
        rl_accout = findViewById(R.id.rl_accout);
        rl_accout.setLayoutManager(new LinearLayoutManager(this));
        rl_accout.setAdapter(new RcySAdapter<AccoutManager.Accout, RcyHolder>(this, R.layout.layout_accout_item) {
            @Override
            public void convert(RcyHolder holder, AccoutManager.Accout accout, int position) {
                final boolean isCreater = position == 0;
                holder.setText(R.id.user_name, accout.getName() + (isCreater ? "   模拟房主：创建房间" : "   模拟观众：加入房间"));
                holder.setText(R.id.user_id, accout.getUserId());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2021/8/30 模拟两种场景
                        //  创建房间 position == 0
                        //  加入房间 postiton >0
                        connect(accout, isCreater);
                    }
                });
            }
        });
        ((IAdapte) rl_accout.getAdapter()).setData(AccoutManager.getAccounts(), true);
    }


    private void connect(AccoutManager.Accout accout, boolean isCreater) {
        setTitle(accout.getName() + " 连接中...");
        //先断开连接
        RCVoiceRoomEngine.getInstance().disconnect();
        AccoutManager.setCurrent(accout.getUserId());
        //连接
        RCVoiceRoomEngine.getInstance().connectWithToken(accout.getToken(), new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.showToast("connect success");
                        if (isCreater) {
                            UIKit.startActivity(ConnectActivity.this, CreaterActivity.class);
                        } else {
                            UIKit.startActivity(ConnectActivity.this, JoinActivity.class);
                        }
                        //fix：可重写连接
//                        finish();
                    }

                    @Override
                    public void onError(int code, String s) {
                        String info = "connect fail：\n【" + code + "】" + s;
                        Log.e("ConnectActivity", info);
                        KToast.showToast(info);
                        setTitle(accout.getName() + " 连接失败");
                    }
                }
        );
    }
}