# 融云语聊房SDK QuicKDemo
## 使用步骤
#### 第一步：在QuickApplication中添加appkey
    private final static String APP_KEY = "";
#### 第二步：在utils/AccoutManager.java的static代码块中添加测试账号 即可运行
    static {
        // TODO: 2021/8/31  must add account
        //  Accout accout = new Accout("Your UserId", "Your UserName");
        //  accout.token = "Your Token ";//token 需和userId一一对应
        //  accounts.add(accout);
    }
#### 功能演示定义枚举列表
	seat_mute("麦位静音"),
    seat_mute_un("取消麦位静麦"),
    seat_lock("麦位锁定"),
    seat_lock_un("取消麦位锁定"),
    seat_enter("上麦"),
    seat_left("下麦"),
    seat_request("请求上麦"),
    seat_request_cancel("取消上麦请求"),
    seat_extra("扩展属性"),
    seat_pick_out("抱下麦"),
    room_all_mute("全麦静麦"),
    room_all_mute_un("取消全麦静音"),
    room_all_lock("全麦锁麦"),
    room_all_lock_nu("取消全麦锁定"),
    room_update_name("修改名称"),
    room_update_count("修改麦位数"),
    room_free("自由模式"),
    room_free_un("申请模式"),
    invite_seat("邀请上麦");
	- 后续会跟新更多演示功能
### Demo 结构
 - Application执行init
	- RCVoiceRoomEngine.getInstance().initWithAppKey(this, APP_KEY);

- ConnectActivity
	- 列表展示AccoutManager.java中添加的测试账号
	- 选取 一个测试账号即可连接 注意：第一个账号模拟房主创建房间，其他账号模拟观众加入房间
	- RCVoiceRoomEngine.getInstance().connectWithToken(context, accout.getToken(), new RCVoiceRoomCallback())
- BaseApiActivity
	- api 演示的基类 统一处理：房间事件监听，和麦位信息的管理
- CreaterActivity
	- 模拟房主创建房间的场景 继承至BaseApiActivity
	- ActionBar显示房间操作的action按钮
- JoinActivity
	- 模拟观众进房间的场景 继承至BaseApiActivity
	- 不显示房间操作的action按钮
- QuickEventListener
	- 单例实现房间事件
	- 通过监听对外提供房间数据和麦位列表数据
- ApiFunDialogHelper
	- 对demo中的弹框统一封装处理
- VoiceRoomApi
	- 具体实现演示api的功能接口
	
		
