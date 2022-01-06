package cn.rongcloud.quickdemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import java.util.List;

import cn.rongcloud.authentication.RoomListActivity;
import cn.rongcloud.authentication.bean.VoiceRoom;
import cn.rongcloud.pk.BottomDialog;
import cn.rongcloud.pk.CancelPKDialog;
import cn.rongcloud.pk.PKApi;
import cn.rongcloud.pk.RoomOwerDialog;
import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * 演示房间api和麦位api的activity
 * 1.设置房间事件监听
 * 2.创建或者加入房间
 * 3.房主上麦 seatIndex = 0
 */
public class RoomActivity extends AppCompatActivity implements
        ApiFunDialogHelper.OnApiClickListener,
        QuickEventListener.SeatListObserver,
        QuickEventListener.RoomInforObserver,
        QuickEventListener.PKObserver,
        DialogInterface.OnDismissListener, View.OnClickListener {
    private final static String ACTION_API = "房间Api";
    private final static String KET_ROOM_ID = "room_id";
    private final static String KET_ROOM_OWNER = "room_owner";
    protected final String TAG = this.getClass().getSimpleName();
    private RecyclerView rl_seat;
    protected TextView room_mode;
    protected SeatHandleBinder createrBinder;
    protected RcySAdapter<RCVoiceSeatInfo, RcyHolder> adapter;

    public static void joinVoiceRoom(Activity activity, String roomId, boolean isRoomOwner) {
        Intent i = new Intent(activity, RoomActivity.class);
        i.putExtra(KET_ROOM_ID, roomId);
        i.putExtra(KET_ROOM_OWNER, isRoomOwner);
        activity.startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if (owner) {
            // 模拟房主 操作房价相关的api
            menu.add(ACTION_API).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(ACTION_API)) {
            ApiFunDialogHelper.helper().showRoomApiDialog(this, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String roomId;
    private boolean owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        roomId = getIntent().getStringExtra(KET_ROOM_ID);
        owner = getIntent().getBooleanExtra(KET_ROOM_OWNER, false);
        // 麦位列表
        initSeats();
        // api 功能
        initApiFun();
        //设置麦位列表监听
        QuickEventListener.get().observeSeatList(this);
        QuickEventListener.get().observeRoomInfo(this);
        QuickEventListener.get().observePKState(this);
        QuickEventListener.get().setVoiceRoomEngine(this, RCVoiceRoomEngine.getInstance());
        joinRoom();
    }

    void joinRoom() {
        if (TextUtils.isEmpty(roomId)) {
            KToast.showToast("房间ID不能为空");
            finish();
            return;
        }
        VoiceRoomApi.getApi().joinRoom(roomId, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                Log.e(TAG, "result = " + result);
                PKApi.synToService(roomId, null);
            }
        });
    }

    @Override
    public void onRoomInfo(RCVoiceRoomInfo roomInfo) {
        setTitle(roomInfo.getRoomName());
        room_mode.setText("上麦模式：" + (roomInfo.isFreeEnterSeat() ? "自由上麦" : "申请上麦"));
    }

    @Override
    public void onSeatList(List<RCVoiceSeatInfo> seatInfos) {
        int count = null == seatInfos ? 0 : seatInfos.size();
        if (count > 0) {
            // 单独处理房主
            if (null != createrBinder) createrBinder.bind(seatInfos.get(0));
            // 麦位信息
            List<RCVoiceSeatInfo> seats = seatInfos.subList(1, count);
            if (null != adapter) adapter.setData(seats, true);
        }
    }

    void initSeats() {
        rl_seat = findViewById(R.id.rl_seat);
        View creater = findViewById(R.id.creater);
        RcyHolder holder = new RcyHolder(creater);
        createrBinder = new SeatHandleBinder(this, holder, 0);
        adapter = new RcySAdapter<RCVoiceSeatInfo, RcyHolder>(this, R.layout.layout_seat_item) {
            @Override
            public void convert(RcyHolder holder, RCVoiceSeatInfo seatInfo, int position) {
                SeatHandleBinder binder = new SeatHandleBinder((Activity) context, holder, position + 1);
                binder.bind(seatInfo);
            }
        };
        rl_seat.setLayoutManager(new GridLayoutManager(this, 4));
        rl_seat.setAdapter(adapter);
    }

    void initApiFun() {
        room_mode = findViewById(R.id.room_mode);
        findViewById(R.id.leave).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.close).setVisibility(owner ? View.VISIBLE : View.GONE);
    }

    private BottomDialog dialog;
    private VoiceRoom pkRoom;

    @Override
    public void onApiClick(View v, ApiFun action) {
        if (action.equals(ApiFun.invite_seat)) {
            //和业务相关
            // 基于QuickDemo 目前在展示的观众列表是：房主进房间之后进入房间的观众
            // 房主进房间的观众列表需要依赖服务端
            ApiFunDialogHelper.helper().showSelectDialog(this, "选择上麦观众", new IResultBack<AccoutManager.Accout>() {
                @Override
                public void onResult(AccoutManager.Accout result) {
                    String userId = result.getUserId();
                    VoiceRoomApi.getApi().handleRoomApi(action, userId, null);
                }
            }, false);

        } else if (action.equals(ApiFun.invite_pk)) {
            // 邀请pk
            if (dialog != null) dialog.dismiss();
            dialog = new RoomOwerDialog(this, new com.kit.wapper.IResultBack<VoiceRoom>() {
                @Override
                public void onResult(VoiceRoom room) {
                    if (null != room) {
                        pkRoom = room;
                    }
                }
            }).setOnCancelListener(this);
            dialog.show();

        } else if (action.equals(ApiFun.invite_pk_cancel)) {
            if (null == pkRoom) {
                KToast.showToast("请先发出PK邀请");
                return;
            }
            if (dialog != null) dialog.dismiss();
            dialog = new CancelPKDialog(this, pkRoom.getRoomId(), pkRoom.getCreateUser().getUserId(), new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean aBoolean) {
                    if (aBoolean) pkRoom = null;
                }
            }).setOnCancelListener(this);
            dialog.show();
        } else {
            VoiceRoomApi.getApi().handleRoomApi(action, null, null);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        dialog = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.leave == id) {
            VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        PKApi.synToService("", null);
                        KToast.showToast("离开房间成功");
                        finish();
                    } else {
                        KToast.showToast("离开房间失败");
                    }
                }
            });
        } else if (R.id.close == id) {
            VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        RoomListActivity.destoryRoomByService(RoomActivity.this, roomId);
                        finish();
                    } else {
                        KToast.showToast("离开房间失败");
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        VoiceRoomApi.getApi().leaveRoom(null);
        super.onBackPressed();
    }

    @Override
    public void onPK(QuickEventListener.PKType type) {
        pkRoom = null;
    }
}