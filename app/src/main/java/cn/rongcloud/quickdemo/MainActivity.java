package cn.rongcloud.quickdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import java.util.List;

import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.VoiceRoomApi;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ApiFunDialogHelper.OnApiClickListener {
    private final static String TAG = "MainActivity";
    private RecyclerView rl_seat;
    private TextView create_and_join, add_event_listeren;
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
        QuickEventListener.get().observeRoomInfo(roomInfo -> {
           setTitle(roomInfo.getRoomName());
        });
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
        //房间事件监听
        add_event_listeren = findViewById(R.id.add_event_listeren);
        add_event_listeren.setOnClickListener(this);
        //创建犯贱
        create_and_join = findViewById(R.id.create_and_join);
        create_and_join.setOnClickListener(this);
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
                String roomName = "Room_" + roomId;
                crateAndJoin(roomId, roomName, 5);
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
        RCVoiceRoomInfo roomInfo = VoiceRoomApi.getApi().getRoomInfo();
        roomInfo.setRoomName(roomName);
        roomInfo.setSeatCount(count);
        roomInfo.setFreeEnterSeat(false);
        roomInfo.setLockAll(seatAllLocked);
        roomInfo.setMuteAll(seatAllMuteed);
        RCVoiceRoomEngine.getInstance().createAndJoinRoom(roomId, roomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                KToast.showToastWithLag(TAG, "crateAndJoin#onSuccess");
                //上麦
                VoiceRoomApi.getApi().enterSeat(0, null);
                //修改ui状态
                create_and_join.post(new Runnable() {
                    @Override
                    public void run() {
                        setTitle("房间:" + roomName);
                        create_and_join.setEnabled(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "item.getTitle = " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.room_api:
                ApiFunDialogHelper.helper().showRoomApiDialog(this, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onApiClick(View v, int index) {
        String action = ApiFunDialogHelper.ROOM_API[index];
        KToast.showToast(action);
        VoiceRoomApi.getApi().handleRoomApi(index, action);
    }

}