package cn.rongcloud.quickdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import java.util.List;

import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

public class MainActivity1 extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private RCVoiceRoomInfo roomInfo = new RCVoiceRoomInfo();
    private RecyclerView rl_seat;
    private TextView create_and_join, add_event_listeren, enter_seat, lock_all, mute_all;
    // 在麦位状态 true：在麦位 false：不在麦位上
    private boolean seatEntered = false;
    //全麦位 锁定状态
    private boolean seatAllLocked = false;
    //全麦位 静音状态
    private boolean seatAllMuteed = false;
    private SeatHandleBinder createrBinder;
    private RcySAdapter<RCVoiceSeatInfo, RcyHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 麦位列表
        initSeats();
        // api 功能
        initApiFun();
        //设置麦位列表监听
        QuickEventListener.get().observeSeatList(seatInfos -> {
            int count = null == seatInfos ? 0 : seatInfos.size();
            if (count > 0) {
                // 单独处理房主
                if (null != createrBinder) createrBinder.bind(seatInfos.get(0));
                // 麦位信息
                List<RCVoiceSeatInfo> seats = seatInfos.subList(1, count);
                if (null != adapter) adapter.setData(seats, true);
            }
        });
    }

    void initSeats() {
        rl_seat = findViewById(R.id.rl_seat);
        View creater = findViewById(R.id.creater);
        RcyHolder holder = new RcyHolder(creater);
        createrBinder = new SeatHandleBinder(this,holder, 0);
        adapter = new RcySAdapter<RCVoiceSeatInfo, RcyHolder>(this, R.layout.layout_seat_item) {
            @Override
            public void convert(RcyHolder holder, RCVoiceSeatInfo seatInfo, int position) {
                SeatHandleBinder binder = new SeatHandleBinder((Activity) context,holder, position + 1);
                binder.bind(seatInfo);
            }
        };
        rl_seat.setLayoutManager(new GridLayoutManager(this,4));
        rl_seat.setAdapter(adapter);
    }

    void initApiFun() {
        //房间事件监听
        add_event_listeren = findViewById(R.id.add_event_listeren);
        add_event_listeren.setOnClickListener(this);
        //创建犯贱
        create_and_join = findViewById(R.id.create_and_join);
        create_and_join.setOnClickListener(this);
        //上麦
        enter_seat = findViewById(R.id.enter_seat);
        enter_seat.setOnClickListener(this);
        //锁麦所有
        lock_all = findViewById(R.id.lock_all);
        lock_all.setOnClickListener(this);
        //静麦所有
        mute_all = findViewById(R.id.mute_all);
        mute_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_event_listeren:
                QuickEventListener.get().setVoiceRoomEngine(RCVoiceRoomEngine.getInstance());
                add_event_listeren.setEnabled(false);
                //create 可用
                create_and_join.setEnabled(true);
                break;
            case R.id.create_and_join:
                String roomId = "" + System.currentTimeMillis();
                String roomName = "QuickRoom" + roomId;
                crateAndJoin(roomId, roomName, 5);
                break;
            case R.id.enter_seat:
                enterOrLeaveSeat();
                break;
            case R.id.lock_all:
                switchLockAll();
                break;
            case R.id.mute_all:
                switchMuteAll();
                break;
        }
    }

    /**
     * 创建并加入房间
     *
     * @param roomId   房间Id
     * @param roomName 房间名称
     * @param count    麦位数
     */
    private void crateAndJoin(String roomId, String roomName, int count) {
        if (null == roomInfo) {
            roomInfo = new RCVoiceRoomInfo();
        }
        roomInfo.setRoomName(roomName);
        roomInfo.setSeatCount(count);
        roomInfo.setFreeEnterSeat(false);
        roomInfo.setLockAll(seatAllLocked);
        roomInfo.setMuteAll(seatAllMuteed);
        RCVoiceRoomEngine.getInstance().createAndJoinRoom(roomId, roomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.showToastWithLag(TAG, "crateAndJoin#onSuccess");
                //修改ui状态
                create_and_join.post(new Runnable() {
                    @Override
                    public void run() {
                        create_and_join.setText("名称:" + roomName);
                        create_and_join.setEnabled(false);
                        //上麦 锁麦 静麦 功能可用
                        enter_seat.setEnabled(true);
                        lock_all.setEnabled(true);
                        mute_all.setEnabled(true);
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                String info = "crateAndJoin#onError [" + code + "]:" + message;
                KToast.showToastWithLag(TAG, info);
            }
        });
    }

    /**
     * 切换麦位上的状态
     * 1.在麦位上 -> 下麦 leaveSeat
     * 2.不在麦位 -> 上麦 enterSeat
     */
    private void enterOrLeaveSeat() {
        RCVoiceRoomCallback callback = new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //置换状态
                seatEntered = !seatEntered;
                KToast.showToastWithLag(TAG, seatEntered ? "上麦成功" : "下麦成功");
                enter_seat.setText(seatEntered ? "Leave Seat" : "Enter Seat");

            }

            @Override
            public void onError(int code, String error) {
                KToast.showToastWithLag(TAG, seatEntered ? "下麦失败" : "上麦失败" + " [" + code + "] message = " + error);
            }
        };
        if (seatEntered) {
            RCVoiceRoomEngine.getInstance().leaveSeat(callback);
        } else {
            RCVoiceRoomEngine.getInstance().enterSeat(0, callback);
        }
    }

    /**
     * 切换全麦锁定状态
     */
    private void switchLockAll() {
        seatAllLocked = !seatAllLocked;
        RCVoiceRoomEngine.getInstance().lockOtherSeats(seatAllLocked);
        KToast.showToastWithLag(TAG, seatAllLocked ? "全麦锁定成功" : "全麦解锁成功");
        lock_all.setText(seatAllLocked ? "Unlock All Seat" : "Lock All Seat");
    }

    /**
     * 切换全麦的静音状态
     */
    private void switchMuteAll() {
        seatAllMuteed = !seatAllMuteed;
        RCVoiceRoomEngine.getInstance().muteOtherSeats(seatAllMuteed);
        KToast.showToastWithLag(TAG, seatAllMuteed ? "全麦静音成功" : "全麦取消静音成功");
        mute_all.setText(seatAllMuteed ? "UnMute All Seat" : "Mute All Seat");
    }

}