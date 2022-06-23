package cn.rongcloud.example;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * voiceroom_lib 实现循环上麦功能的辅助类
 */
public class RepeatEnterHelper extends HandlerThread implements IRepeat {
    private final static String TAG = "RepeatEnterHelper";
    private volatile static IRepeat _helper = new RepeatEnterHelper();
    private OnSeatChangeListener listener;
    private final static Object lock = new Object();
    // sdk seats
    private final List<RCVoiceSeatInfo> sdkSeats = new ArrayList<>();
    // 动态转换后的seats
    private final List<RCVoiceSeatInfo> uiSeats = new ArrayList<>();
    private Handler handler;

    private RepeatEnterHelper() {
        super(TAG);
        start();
        handler = new Handler(getLooper());
    }

    public static IRepeat getHelper() {
        return _helper;
    }

    int size(List<RCVoiceSeatInfo> list) {
        return null != list ? list.size() : 0;
    }

    @Override
    public void setOnSeatChangeListener(OnSeatChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void inputSeatInfo(List<RCVoiceSeatInfo> seatInfos) {
        List<RCVoiceSeatInfo> seats = new ArrayList<>(seatInfos);
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (seats.size() != sdkSeats.size()) {
                        // 麦位数变化 重置
                        uiSeats.clear();
                        uiSeats.addAll(seats);
                        sdkSeats.clear();
                        sdkSeats.addAll(seats);
                    } else {
                        uiSeats.clear();
                        uiSeats.addAll(orderSeatByChanged(seats, sdkSeats));
                        sdkSeats.clear();
                        sdkSeats.addAll(seats);
                    }
                    // run current handler thread
                    if (null != listener) listener.onSeatList(new ArrayList<>(uiSeats));
                }
            }
        });
    }

    @Override
    public void enterSeat() {
        RCVoiceSeatInfo desSeat = getAvailableSeatInfo();
        // 目标麦位上有人 则包抱下麦
        String userId = desSeat.getUserId();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(userId)) {
                    CountDownLatch latch = new CountDownLatch(1);
                    RCVoiceRoomEngine.getInstance().pickUserToSeat(userId, new RCVoiceRoomCallback() {
                        @Override
                        public void onSuccess() {
                            latch.countDown();
                        }

                        @Override
                        public void onError(int i, String s) {
                            latch.countDown();
                        }
                    });
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //转换sdk index
                int index = getSeatIndex(desSeat);
                //enter seat
                CountDownLatch latch = new CountDownLatch(1);
                RCVoiceRoomEngine.getInstance().enterSeat(index, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(int i, String s) {
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //修改麦位顺序
                uiSeats.remove(desSeat);
                uiSeats.add(0, desSeat);
                // run current handler thread
                if (null != listener) listener.onSeatList(new ArrayList<>(uiSeats));
            }
        });
    }


    @Override
    public RCVoiceSeatInfo getAvailableSeatInfo() {
        int count = size(uiSeats);
        if (count < 1) {
            return null;
        }
        synchronized (lock) {
            for (int i = 0; i < count; i++) {
                RCVoiceSeatInfo seat = uiSeats.get(i);
                if (RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty == seat.getStatus()) {
                    return seat;
                }
            }
            return uiSeats.get(count - 1);// 默认 最后一个
        }
    }


    @Override
    public int getSeatIndex(RCVoiceSeatInfo des) {
        synchronized (lock) {
            int index = -1;
            int count = size(sdkSeats);
            for (int i = 0; i < count; i++) {
                RCVoiceSeatInfo seat = sdkSeats.get(i);
                if (des.equals(seat)) {
                    index = i;
                }
            }
            return index;
        }
    }

    private List<RCVoiceSeatInfo> orderSeatByChanged(@NonNull List<RCVoiceSeatInfo> news, @NonNull List<RCVoiceSeatInfo> olds) {
        List<RCVoiceSeatInfo> result = new ArrayList<>();
        List<RCVoiceSeatInfo> noChanages = new ArrayList<>();
        int count = Math.min(size(news), size(olds));
        for (int i = 0; i < count; i++) {
            RCVoiceSeatInfo ns = news.get(i);
            RCVoiceSeatInfo os = olds.get(i);
            if (!TextUtils.equals(ns.getUserId(), os.getUserId())) {
                result.add(ns);
            } else {
                noChanages.add(ns);
            }
        }
        // add no chanage
        result.addAll(noChanages);
        return result;
    }
}