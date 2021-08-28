package cn.rongcloud.quickdemo;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.interfaces.IAdapte;
import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.GsonUtil;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.UIKit;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

public class ConnectActivity extends AppCompatActivity {
    private RecyclerView rl_accout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        setTitle("选择账号");
        rl_accout = findViewById(R.id.rl_accout);
        rl_accout.setLayoutManager(new LinearLayoutManager(this));
        rl_accout.setAdapter(new RcySAdapter<AccoutManager.Accout, RcyHolder>(this, R.layout.layout_accout_item) {
            @Override
            public void convert(RcyHolder holder, AccoutManager.Accout accout, int position) {
                holder.setText(R.id.user_name, accout.getName());
                holder.setText(R.id.user_id, accout.getUserId());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        connect(accout);
                    }
                });
            }
        });
        ((IAdapte) rl_accout.getAdapter()).setData(AccoutManager.getAccounts(), true);
    }

    private void connect(AccoutManager.Accout accout) {
        setTitle(accout.getName() + " 连接中...");
        //先断开连接
        RCVoiceRoomEngine.getInstance().disConnect();
        //连接
        RCVoiceRoomEngine.getInstance().connectWithToken(
                (Application) UIKit.getContext(), accout.getToken(), new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.showToast("connect success");
                        UIKit.startActivity(ConnectActivity.this, MainActivity.class);
                        finish();
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