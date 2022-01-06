package cn.rongcloud.authentication;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;
import com.bcq.refresh.IRefresh;
import com.bcq.refresh.XRecyclerView;
import com.kit.UIKit;
import com.kit.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.authentication.bean.VoiceRoom;
import cn.rongcloud.oklib.LoadTag;
import cn.rongcloud.oklib.OkApi;
import cn.rongcloud.oklib.WrapperCallBack;
import cn.rongcloud.oklib.wrapper.Wrapper;
import cn.rongcloud.oklib.wrapper.interfaces.ILoadTag;
import cn.rongcloud.quickdemo.AbsPermissionActivity;
import cn.rongcloud.quickdemo.R;
import cn.rongcloud.quickdemo.RoomActivity;
import cn.rongcloud.quickdemo.VoiceRoomApi;
import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

/**
 * 房间列表
 */
public class RoomListActivity extends AbsPermissionActivity {
    public final static String TAG = "RoomListActivity";
    private final static String ACTION_CREATE = "创建房间";
    public final static int DEF_SEAT_COUNT = 5;

    @Override
    protected String[] onCheckPermission() {
        return VOICE_PERMISSIONS;
    }

    private XRecyclerView rl_rooms;
    private RcySAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(ACTION_CREATE).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(ACTION_CREATE)) {
            createVoiceRoom();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPermissionAccept(boolean accept) {
        if (accept) {
            initView();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_room_list);
        setTitle("语聊房");
        rl_rooms = findViewById(R.id.rl_rooms);
        rl_rooms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RcySAdapter<VoiceRoom, RcyHolder>(this, R.layout.layout_room_item) {
            @Override
            public void convert(RcyHolder holder, VoiceRoom room, int position) {
                holder.setText(R.id.room_name, room.getRoomName());
                holder.setText(R.id.user_id, room.getCreateUser().getUserName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean owner = TextUtils.equals(room.getUserId(), AccoutManager.getCurrentId());
                        jumpToVoiceRoom(room.getRoomId(), owner);
                    }
                });
            }
        };
        rl_rooms.setAdapter(adapter);
        // refresh load
        rl_rooms.setLoadListener(new IRefresh.LoadListener() {
            @Override
            public void onRefresh() {
                getRoomListFromService(true);
            }

            @Override
            public void onLoad() {
                getRoomListFromService(false);
            }
        });
        getRoomListFromService(true);
    }

    void createVoiceRoom() {
        ApiFunDialogHelper.helper().showEditorDialog(this, "房间名称", new IResultBack<String>() {
            @Override
            public void onResult(String result) {
                if (TextUtils.isEmpty(result)) {
                    KToast.showToast("请输入房间名称");
                    return;
                }
                createRoomByService(result);
            }
        });
    }

    private int page = 1;
    private final static int PAGE_SIZE = 10;

    /**
     * 获取房间列表
     *
     * @param refresh 是否刷新
     */
    void getRoomListFromService(boolean refresh) {
        if (refresh) {
            page = 1;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", PAGE_SIZE);
        params.put("type", Api.ROOM_TYPE);
        OkApi.get(Api.ROOM_LIST, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                KToast.showToast("拉取房间列表失败");
            }

            @Override
            public void onResult(Wrapper wrapper) {
                List<VoiceRoom> rooms = wrapper.getList("rooms", VoiceRoom.class);
                Logger.e(TAG, "provideFromService: size = " + (null == rooms ? 0 : rooms.size()));
                if (rooms != null && !rooms.isEmpty()) {
                    page++;
                }
                for (VoiceRoom room : rooms) {
                    if (null != room.getCreateUser()) {
                        AccoutManager.setAcctount(room.getCreateUser(), false);
                    }
                }
                if (null != adapter) adapter.setData(rooms, true);
            }

            @Override
            public void onAfter() {
                if (null != rl_rooms) {
                    rl_rooms.refreshComplete();
                    rl_rooms.loadComplete();
                }
            }
        });
    }

    /**
     * 通过服务创建房间 其实就是获取房间id
     *
     * @param roomName 房间名称
     */
    private void createRoomByService(String roomName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", roomName);
        params.put("themePictureUrl", "");
        params.put("isPrivate", 1);
        params.put("password", "");
        params.put("kv", new ArrayList());
        params.put("roomType", Api.ROOM_TYPE);// 1：语聊房  2：电台房 3：直播房
        LoadTag tag = new LoadTag(activity, "创建中...");
        tag.show();
        OkApi.post(Api.ROOM_CREATE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null == result) {
                    KToast.showToast("创建房间失败");
                    if (null != tag) tag.dismiss();
                    return;
                }
                VoiceRoom voiceRoom = result.get(VoiceRoom.class);
                if (null == voiceRoom) {
                    KToast.showToast("创建房间失败");
                    if (null != tag) tag.dismiss();
                    return;
                }
                if (result.getCode() == 10000) {
                    createRoomBySDK(tag, voiceRoom.getRoomId(), voiceRoom.getRoomName());
                } else {
                    if (null != tag) tag.dismiss();
                    if (30016 == result.getCode()) {
                        KToast.showToast("您已经创建过房间，不能重复创建");
                    } else {
                        KToast.showToast("创建房间失败");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != tag) tag.dismiss();
                KToast.showToast("创建房间失败");
            }
        });
    }

    /**
     * 由sdk 创建房间，正在创建房间的逻辑
     *
     * @param tag      load tag
     * @param roomId   房间id
     * @param roomName 房间名称
     */
    void createRoomBySDK(ILoadTag tag, String roomId, String roomName) {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(roomName)) {
            KToast.showToast("房间ID或房间名称不能为空");
            if (null != tag) tag.dismiss();
            return;
        }
        RCVoiceRoomInfo roomInfo = VoiceRoomApi.getApi().getRoomInfo();
        roomInfo.setRoomName(roomName);
        roomInfo.setSeatCount(DEF_SEAT_COUNT);
        roomInfo.setFreeEnterSeat(false);
        roomInfo.setLockAll(false);
        roomInfo.setMuteAll(false);
        VoiceRoomApi.getApi().createAndJoin(roomId, roomInfo, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (null != tag) tag.dismiss();
                KToast.showToast(result ? "创建房间成功" : "创建房间失败");
                if (result) {
                    // 创建成功后 离开执行离开房间 便于：RoomActivity 统一执行joinRoom即可
                    Logger.e(TAG, "createAndJoin:");
                    VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            Logger.e(TAG, "leaveRoom:");
                            UIKit.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    jumpToVoiceRoom(roomId, true);
                                }
                            }, 500);
                        }
                    });
                } else {
                    // 创建失败 需通知服务端销毁房间
                    destoryRoomByService(activity, roomId);
                }
            }
        });
    }

    /**
     * 通过服务api接口销毁房间
     *
     * @param activity activity
     * @param roomId   待销毁的房间id
     */
    public static void destoryRoomByService(Activity activity, String roomId) {
        String url = Api.DELETE_ROOM.replace(Api.KEY_ROOM_ID, roomId);
        LoadTag tag = new LoadTag(activity, "关闭房间...");
        tag.show();
        OkApi.get(url, null, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != tag) tag.dismiss();
                KToast.showToast("关闭房间失败");
            }

            @Override
            public void onResult(Wrapper wrapper) {
                if (null != tag) tag.dismiss();
                if (wrapper.ok()) {
                    KToast.showToast("关闭房间成功");
                } else {
                    KToast.showToast("关闭房间失败");
                }
            }
        });
    }

    /**
     * 跳转到语聊房界面
     *
     * @param roomId 房间Id
     * @param owner  是不是房主
     */
    private void jumpToVoiceRoom(String roomId, boolean owner) {
        RoomActivity.joinVoiceRoom(activity, roomId, owner);
    }
}
