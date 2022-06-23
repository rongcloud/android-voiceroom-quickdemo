package cn.rongcloud.example;

import java.util.List;

import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * 语聊房SDK实现循环上麦接口
 */
public interface IRepeat {

    /**
     * 麦位变化监听
     */
    interface OnSeatChangeListener {
        void onSeatList(List<RCVoiceSeatInfo> seats);
    }

    /**
     * 设置麦位变化监听
     *
     * @param listener 监听
     */
    void setOnSeatChangeListener(OnSeatChangeListener listener);

    /**
     * 输入SDK事件中的onSeatInfoUpdate回调的原始麦位信息
     *
     * @param seatInfos 麦位信息
     */
    void inputSeatInfo(List<RCVoiceSeatInfo> seatInfos);

    /**
     * 循环上麦
     * 1、ui的麦位列表中获取可用索引
     * 2、如果麦位上有人，则报下麦
     * 3、获取麦位索引
     * 4、上麦，跟新麦位列表
     */
    void enterSeat();


    /**
     * 获取UI列表可用麦位
     *
     * @return 可用麦位
     */
    RCVoiceSeatInfo getAvailableSeatInfo();

    /**
     * 获取SDK中这是索引，对应enterSeat(index)中的索引
     *
     * @param seat 目标麦位
     * @return 在SDK中索引
     */
    int getSeatIndex(RCVoiceSeatInfo seat);
}