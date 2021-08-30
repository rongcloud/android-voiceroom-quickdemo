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

import cn.rongcloud.quickdemo.uitls.Api;
import cn.rongcloud.quickdemo.uitls.KToast;
import cn.rongcloud.quickdemo.uitls.VoiceRoomApi;
import cn.rongcloud.quickdemo.widget.ApiFunDialogHelper;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * 演示房间api和麦位api的activity基类
 * 1.创建并加入房间
 * 2.上麦 麦位index = 0
 */
public abstract class BaseApiActivity extends AppCompatActivity implements View.OnClickListener, ApiFunDialogHelper.OnApiClickListener
        , QuickEventListener.SeatListObserver, QuickEventListener.RoomInforObserver {
    protected final String TAG = this.getClass().getSimpleName();
    private RecyclerView rl_seat;
    protected TextView create_and_join, add_event_listeren, room_mode;
    protected SeatHandleBinder createrBinder;
    protected RcySAdapter<RCVoiceSeatInfo, RcyHolder> adapter;
    // 在麦位状态 true：在麦位 false：不在麦位上
    protected boolean seatEntered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_base);
        // 麦位列表
        initSeats();
        // api 功能
        initApiFun();
        //设置麦位列表监听
        QuickEventListener.get().observeSeatList(this);
        QuickEventListener.get().observeRoomInfo(this);
        initCustomer();
    }

    @Override
    public void onRoomInfo(RCVoiceRoomInfo roomInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(roomInfo.getRoomName());
                room_mode.setText("上麦模式：" + (roomInfo.isFreeEnterSeat() ? "自由上麦" : "申请上麦"));
            }
        });
    }

    @Override
    public void onSeatList(List<RCVoiceSeatInfo> seatInfos) {
        int count = null == seatInfos ? 0 : seatInfos.size();
        if (count > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 单独处理房主
                    if (null != createrBinder) createrBinder.bind(seatInfos.get(0));
                    // 麦位信息
                    List<RCVoiceSeatInfo> seats = seatInfos.subList(1, count);
                    if (null != adapter) adapter.setData(seats, true);
                }
            });
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
        //房间事件监听
        add_event_listeren = findViewById(R.id.add_event_listeren);
        add_event_listeren.setOnClickListener(this);
        //创建犯贱
        create_and_join = findViewById(R.id.create_and_join);
        create_and_join.setOnClickListener(this);
    }

    //离开房间后修重写initlistener
    protected void resetAnable() {
        add_event_listeren.setEnabled(true);
        create_and_join.setEnabled(false);
    }

    protected void initCustomer() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_event_listeren:
                QuickEventListener.get().setVoiceRoomEngine(this, RCVoiceRoomEngine.getInstance());
                add_event_listeren.setEnabled(false);
                //create 可用
                create_and_join.setEnabled(true);
                break;
            case R.id.create_and_join:
                handleJoinOrCreateAndJoin();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showRoomApiAction()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
        }
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
    public void onApiClick(View v, ApiFun action) {
        KToast.showToast(action.name());
        if (action.equals(ApiFun.invite_seat)) {
            ApiFunDialogHelper.helper().showSelectDialog(this, "邀请人列表", new Api.IResultBack<String>() {
                @Override
                public void onResult(String result) {
                    String userId = result;
                    VoiceRoomApi.getApi().handleRoomApi( action, userId);
                }
            });

        } else {
            VoiceRoomApi.getApi().handleRoomApi(action, null);
        }
    }

    /**
     * 是否是创建
     *
     * @return
     */
    abstract void handleJoinOrCreateAndJoin();

    /**
     * ActionBar是否显示Room相关api的action
     *
     * @return
     */
    abstract boolean showRoomApiAction();
}