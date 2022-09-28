package cn.rongcloud.voicequickdemo;

public enum ApiFun {
    seat_mute("麦位静音"),
    seat_mute_un("取消麦位静麦"),
    seat_lock("麦位锁定"),
    seat_lock_un("取消麦位锁定"),
    seat_enter("上麦"),
    seat_left("下麦"),
    seat_enter_plugin("上麦(plugin)"),
    seat_left_plugin("下麦(plugin)"),
    seat_request("请求上麦"),
    seat_request_cancel("取消上麦请求"),
//    seat_extra("扩展属性"),
    seat_pick_out("抱下麦"),
    seat_close_mic("关闭麦克风"),
    seat_open_mic("打开麦克风"),
    seat_switch("跳麦"),
    seat_switch_plugin("跳麦(plugin)"),
    seat_update("修改扩展"),
    seat_update_plugin("修改扩展(plugin)"),

    room_all_mute("全麦静麦"),
    room_all_mute_un("取消全麦静音"),
    room_all_lock("全麦锁麦"),
    room_all_lock_nu("取消全麦锁定"),
    room_update_name("修改名称"),
    room_update_count("修改麦位数"),
    room_free("自由模式"),
    room_free_un("申请模式"),

    invite_seat("邀请上麦"),
    invite_pk("邀请PK"),
    invite_pk_cancel("取消PK邀请"),
    invite_pk_mute("静麦PK对象"),
    invite_quit_pk("退出PK");

    private String value;

    ApiFun(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
