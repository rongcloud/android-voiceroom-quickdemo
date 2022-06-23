package cn.rongcloud.voicequickdemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.kit.UIKit;
import com.kit.utils.KToast;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.util.List;

import cn.rongcloud.authentication.RoomListActivity;
import cn.rongcloud.authentication.bean.Account;
import cn.rongcloud.authentication.bean.VoiceRoom;
import cn.rongcloud.pk.BottomDialog;
import cn.rongcloud.pk.CancelPKDialog;
import cn.rongcloud.pk.PKApi;
import cn.rongcloud.pk.RoomOwerDialog;
import cn.rongcloud.voicequickdemo.uitls.AccoutManager;
import cn.rongcloud.voicequickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.utils.VMLog;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;

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
    public final static String ACTION_NOTIFY = "RoomActivity";
    public final static int ACTION_ROOM = 1000;
    private final static String ACTION_API = "房间Api";
    private final static String KET_ROOM_ID = "room_id";
    private final static String KET_ROOM_OWNER = "room_owner";
    private final static String KET_ENTER = "enter";
    protected final String TAG = this.getClass().getSimpleName();
    private RecyclerView rl_seat;
    protected TextView room_mode, online_count;
    protected SeatHandleBinder createrBinder;
    protected RcySAdapter<Seat, RcyHolder> adapter;

    public static void joinVoiceRoom(Activity activity, String roomId, boolean isRoomOwner, boolean enter) {
        Intent i = new Intent(activity, RoomActivity.class);
        i.putExtra(KET_ROOM_ID, roomId);
        i.putExtra(KET_ROOM_OWNER, isRoomOwner);
        i.putExtra(KET_ENTER, enter);
        activity.startActivityForResult(i, ACTION_ROOM);
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
    private boolean enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        roomId = getIntent().getStringExtra(KET_ROOM_ID);
        owner = getIntent().getBooleanExtra(KET_ROOM_OWNER, false);
        enter = getIntent().getBooleanExtra(KET_ENTER, false);
        // 麦位列表
        initSeats();
        // api 功能
        initApiFun();
        //设置麦位列表监听
        QuickEventListener.get().observeSeatList(this);
        QuickEventListener.get().observeRoomInfo(this);
        QuickEventListener.get().observePKState(this);
        QuickEventListener.get().register(this, owner);
        joinRoom();
        refreshMembers();
    }

    void joinRoom() {
        if (TextUtils.isEmpty(roomId)) {
            KToast.show("房间ID不能为空");
            finish();
            return;
        }
        VoiceRoomApi.getApi().joinRoom(roomId, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                Log.e(TAG, "result = " + result);
                PKApi.synToService(roomId, null);
                if (result) {// 加入房间成功 后跟新在线麦位数
                    onOnLineCount();
                    if (enter) {
                        VoiceRoomApi.getApi().enterSeat(0, null);
                    }
                    NotificationService.bindNotifyService(RoomActivity.this, ACTION_NOTIFY);
                }
            }
        });
    }

    @Override
    public void onRoomInfo(RCVoiceRoomInfo roomInfo) {
        setTitle(roomInfo.getRoomName());
        room_mode.setText("上麦模式：" + (roomInfo.isFreeEnterSeat() ? "自由上麦" : "申请上麦"));
    }

    void refreshMembers() {
        PKApi.getRoomMembers(roomId, new com.kit.wapper.IResultBack<List<Account>>() {
            @Override
            public void onResult(List<Account> result) {
                // 跟新的信息已经保存到AccountManager中
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onOnLineCount() {
        refreshMembers();
        RongIMClient.getInstance().getChatRoomInfo(roomId,
                0,
                ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC,
                new RongIMClient.ResultCallback<ChatRoomInfo>() {
                    @Override
                    public void onSuccess(ChatRoomInfo chatRoomInfo) {
                        int onlineCount = null == chatRoomInfo ? 0 : chatRoomInfo.getTotalMemberCount();
                        UIKit.runOnUiTherad(new Runnable() {
                            @Override
                            public void run() {
                                online_count.setText("在线人：" + onlineCount);
                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        VMLog.e(TAG, "getOnLineUserCount#onError" + errorCode);
                    }
                });
    }

    String onSeatUserId;

    @Override
    public void onSeatList(List<Seat> seatInfos) {
        int count = null == seatInfos ? 0 : seatInfos.size();
        if (count > 0) {
            // 单独处理房主
            if (null != createrBinder) createrBinder.bind(seatInfos.get(0));
            // 麦位信息
            List<Seat> seats = seatInfos.subList(1, count);
            if (null != adapter) adapter.setData(seats, true);
            for (int i = 0; i < count; i++) {
                Seat seat = seatInfos.get(i);
                String userId = seat.getUserId();
                if (!TextUtils.isEmpty(userId) && !TextUtils.equals(userId, AccoutManager.getCurrentId())) {
                    onSeatUserId = userId;
                }
            }
        }
    }

    void initSeats() {
        rl_seat = findViewById(R.id.rl_seat);
        View creater = findViewById(R.id.creater);
        RcyHolder holder = new RcyHolder(creater);
        createrBinder = new SeatHandleBinder(this, holder, 0);
        adapter = new RcySAdapter<Seat, RcyHolder>(this, R.layout.layout_seat_item) {
            @Override
            public void convert(RcyHolder holder, Seat seatInfo, int position) {
                SeatHandleBinder binder = new SeatHandleBinder((Activity) context, holder, position + 1);
                binder.bind(seatInfo);
            }
        };
        rl_seat.setLayoutManager(new GridLayoutManager(this, 4));
        rl_seat.setAdapter(adapter);
    }

    void initApiFun() {
        room_mode = findViewById(R.id.room_mode);
        online_count = findViewById(R.id.online_count);
        findViewById(R.id.leave).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.solo).setOnClickListener(this);
        findViewById(R.id.chorus).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.pause).setOnClickListener(this);
        findViewById(R.id.resume).setOnClickListener(this);
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
            ApiFunDialogHelper.helper().showSelectDialog(this, roomId, "选择上麦观众", new IResultBack<Account>() {
                @Override
                public void onResult(Account result) {
                    String userId = result.getUserId();
                    VoiceRoomApi.getApi().handleRoomApi(action, userId, null);
                }
            });

        } else if (action.equals(ApiFun.invite_pk)) {
            // 邀请pk
            if (dialog != null) dialog.dismiss();
            dialog = new RoomOwerDialog(this, new IResultBack<VoiceRoom>() {
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
                KToast.show("请先发出PK邀请");
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
                    NotificationService.unbindNotifyService();
                    if (result) {
                        PKApi.synToService("", null);
                        KToast.show("离开房间成功");
                        finish();
                    } else {
                        KToast.show("离开房间失败");
                    }
                }
            });
        } else if (R.id.close == id) {
            VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        RoomListActivity.destoryRoomByService(RoomActivity.this, roomId);
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        KToast.show("离开房间失败");
                    }
                }
            });
        } else if (R.id.stop == id) {
//            RCKTVManager.getInstance().stop(new RCVoiceRoomCallback() {
//                @Override
//                public void onSuccess() {
//                    Logger.e("stop#onSuccess: ");
//                }
//
//                @Override
//                public void onError(int code, IError error) {
//                    Logger.e("stop#onError: " + error.toJson());
//                }
//            });
        } else if (R.id.pause == id) {
//            RCKTVManager.getInstance().pause(new RCVoiceRoomCallback() {
//                @Override
//                public void onSuccess() {
//                    Logger.e("pause#onSuccess: ");
//                }
//
//                @Override
//                public void onError(int code, IError error) {
//                    Logger.e("pause#onError: " + error.toJson());
//                }
//            });
        } else if (R.id.resume == id) {
//            RCKTVManager.getInstance().resume(new RCVoiceRoomCallback() {
//                @Override
//                public void onSuccess() {
//                    Logger.e("resume#onSuccess: ");
//                }
//
//                @Override
//                public void onError(int code, IError error) {
//                    Logger.e("resume#onError: " + error.toJson());
//                }
//            });
        } else if (R.id.chorus == id) {
            if (TextUtils.isEmpty(onSeatUserId)) {
                KToast.show("合唱至少需要两个主播");
                return;
            }
            String path = getFilesPath();
            path = path + "/test/test.mp3";
            Logger.e(TAG, "path = " + path);

            String currentId = AccoutManager.getCurrentId();
//            RCChorusConfig config = RCChorusConfig.formFile(roomId, new File(path));
//            if (owner) {
//                // 主唱
//                config.setLeadId(currentId);
//                config.applyChorus(onSeatUserId);
//            } else {
//                //副唱
//                config.setLeadId(onSeatUserId);
//                config.applyChorus(currentId);
//            }
//            RCKTVManager.getInstance().chorus(config, rcktvCallback);
        } else if (R.id.solo == id) {
            String path = getFilesPath();
//            path = path + "/test/变了";
//            path = path + "/test/test.mp3";
//            Logger.e(TAG, "path = " + path);
//            RCSoloConfig config = RCSoloConfig.formFile(roomId, new File(path));
//            RCKTVManager.getInstance().solo(config, rcktvCallback);
        }
    }

//    private RCKTVCallback rcktvCallback = new RCKTVCallback() {
//        @Override
//        public void onStart() {
//            Logger.e("onStart");
//        }
//
//        @Override
//        public void onPause() {
//            Logger.e("onPause");
//        }
//
//        @Override
//        public void onResume() {
//            Logger.e("onResume");
//        }
//
//        @Override
//        public void onStop() {
//            Logger.e("onStop");
//        }
//
//        @Override
//        public void onComplete() {
//            Logger.e("onComplete");
//        }
//
//        @Override
//        public void onProgress(float progress, long duration) {
//            Logger.e("onProgress: progress = " + progress + " duration = " + duration);
//        }
//
//        @Override
//        public void onError(int code, IError error) {
//            Logger.e("onError: " + error.toJson());
//        }
//    };
    // /storage/emulated/0/变了
    private String path = "/storage/emulated/0/Android/data/cn.rongcloud.voiceroomdemo.dev/files/Music/变了";


    /**
     * 获取文件存储根路径：
     * 外部存储可用，返回外部存储路径:/storage/emulated/0/Android/data/包名/files
     * 外部存储不可用，则返回内部存储路径：data/data/包名/files
     */
    public static String getFilesPath() {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用:/storage/emulated/0/Android/data/包名/files
            filePath = UIKit.getContext().getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用，内部存储路径：data/data/com.learn.test/files
            filePath = UIKit.getContext().getFilesDir().getPath();
        }
        return filePath;
    }

    @Override
    protected void onDestroy() {
        VoiceRoomApi.getApi().leaveRoom(null);
        super.onDestroy();
    }

    @Override
    public void onPK(QuickEventListener.PKType type) {
        pkRoom = null;
    }
}