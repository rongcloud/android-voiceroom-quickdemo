package cn.rongcloud.quickdemo;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.bcq.adapter.recycle.RcyHolder;

import java.lang.ref.WeakReference;

import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.VoiceRoomApi;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * 麦位信息数据的绑定器
 */
public class SeatHandleBinder {
    private RcyHolder holder;
    private RCVoiceSeatInfo seatInfo;//麦位信息
    private int index = 0;//麦位索引
    private WeakReference<Activity> activity;

    protected SeatHandleBinder(Activity activity, RcyHolder holder, int index) {
        this.activity = new WeakReference<>(activity);
        this.holder = holder;
        this.index = index;
    }

    public void bind(RCVoiceSeatInfo seatInfo) {
        if (null == seatInfo) {
            return;
        }
        this.seatInfo = seatInfo;
        if (null == holder) {
            return;
        }
        bind();
    }

    private void bind() {
        boolean useing = seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing;
        boolean lock = seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking;
        holder.setBackgroundResource(R.id.iv_portrait, useing ? R.mipmap.default_online : R.drawable.bg_seat_empty);
        holder.setVisible(R.id.seat_locked, lock);
        holder.setVisible(R.id.seat_mute, seatInfo.isMute());
        holder.setText(R.id.member_name, AccoutManager.getAccoutName(seatInfo.getUserId()));
        holder.setText(R.id.member_extra, TextUtils.isEmpty(seatInfo.getExtra()) ? "扩展：" : "扩展：" + seatInfo.getExtra());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiFunDialogHelper.helper().showSeatApiDialog(activity.get(), new ApiFunDialogHelper.OnApiClickListener() {
                    @Override
                    public void onApiClick(View v, ApiFun apiFun) {
                        onApi(apiFun, seatInfo, index);
                    }
                });
            }
        });
    }

    /**
     * 处理api功能
     *
     * @param apiFun
     * @param seatInfo
     */
    public void onApi(ApiFun apiFun, RCVoiceSeatInfo seatInfo, int seatIndex) {
        KToast.showToast(apiFun.name());
        VoiceRoomApi.getApi().handleSeatApi(apiFun, seatIndex);
    }
}