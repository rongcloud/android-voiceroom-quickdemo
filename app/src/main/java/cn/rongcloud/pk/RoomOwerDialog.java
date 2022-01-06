package cn.rongcloud.pk;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bcq.adapter.interfaces.IAdapte;
import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;
import com.bcq.refresh.XRecyclerView;
import com.kit.UIKit;
import com.kit.cache.GsonUtil;
import com.kit.utils.ImageLoader;
import com.kit.utils.KToast;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.util.List;

import cn.rongcloud.authentication.Api;
import cn.rongcloud.authentication.bean.VoiceRoom;
import cn.rongcloud.oklib.OkApi;
import cn.rongcloud.oklib.WrapperCallBack;
import cn.rongcloud.oklib.wrapper.Wrapper;
import cn.rongcloud.pk.domain.PKResult;
import cn.rongcloud.quickdemo.R;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;

/**
 * pk在线房主弹框
 */
public class RoomOwerDialog extends BottomDialog {
    public RoomOwerDialog(Activity activity, IResultBack<VoiceRoom> resultBack) {
        super(activity);
        this.resultBack = resultBack;
        setContentView(R.layout.layout_owner_dialog, 60);
        initView();
        requestOwners();
    }

    private XRecyclerView rcyOwner;
    private IAdapte adapter;
    private IResultBack<VoiceRoom> resultBack;

    private void initView() {
        rcyOwner = UIKit.getView(getContentView(), R.id.rcy_owner);
        rcyOwner.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new RcySAdapter<VoiceRoom, RcyHolder>(mActivity, R.layout.layout_owner_item) {

            @Override
            public void convert(RcyHolder holder, VoiceRoom item, int position) {
                holder.setText(R.id.tv_name, item.getRoomName());
                ImageLoader.loadUrl(holder.getView(R.id.head),
                        item.getCreateUser().getPortrait(),
                        R.mipmap.default_portrait,
                        ImageLoader.Size.SZ_200);
                holder.rootView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String roomId = item.getRoomId();
                        isInPk(roomId, new IResultBack<Boolean>() {
                            @Override
                            public void onResult(Boolean aBoolean) {
                                if (!aBoolean) {// 没有正在pk
                                    dismiss();
                                    RCVoiceRoomEngine.getInstance().sendPKInvitation(item.getRoomId(), item.getCreateUser().getUserId(),
                                            new RCVoiceRoomCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    KToast.show("已邀请PK,等待对方接受");
                                                    if (null != resultBack)
                                                        resultBack.onResult(item);
                                                }

                                                @Override
                                                public void onError(int i, String s) {
                                                    KToast.show("PK邀请失败");
                                                    if (null != resultBack)
                                                        resultBack.onResult(null);
                                                }
                                            });
                                } else {
                                    KToast.show("对方正在PK中");
                                }
                            }
                        });
                    }
                });
            }
        };
        adapter.setRefreshView(rcyOwner);
        rcyOwner.enableRefresh(false);
        rcyOwner.enableRefresh(false);
    }

    /**
     * 判断是否正在pk
     *
     * @param roomId     房间id
     * @param resultBack 回调
     */
    void isInPk(String roomId, IResultBack<Boolean> resultBack) {
        PKApi.getPKInfo(roomId, new IResultBack<PKResult>() {
            @Override
            public void onResult(PKResult pkResult) {
                if (null == pkResult || pkResult.getStatusMsg() == -1 || pkResult.getStatusMsg() == 2) {
                    Logger.e(TAG, "init: Not In PK");
                    resultBack.onResult(false);
                } else {
                    resultBack.onResult(true);
                }
            }
        });
    }

    /**
     * 获取房主在线房间列表
     */
    private void requestOwners() {
        OkApi.get(Api.ONLINE_CREATER, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "requestOwners#onResult:" + GsonUtil.obj2Json(result));
                List<VoiceRoom> rooms = result.getList(VoiceRoom.class);
                adapter.setData(rooms, true);
            }

            @Override
            public void onAfter() {
                if (null != rcyOwner) {
                    rcyOwner.loadComplete();
                    rcyOwner.refreshComplete();
                }
            }
        });
    }

}
