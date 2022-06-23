package cn.rongcloud.pk;

import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.authentication.Api;
import cn.rongcloud.authentication.bean.Account;
import cn.rongcloud.oklib.OkApi;
import cn.rongcloud.oklib.WrapperCallBack;
import cn.rongcloud.oklib.wrapper.Wrapper;
import cn.rongcloud.pk.domain.PKResult;
import cn.rongcloud.voicequickdemo.uitls.AccoutManager;

public class PKApi {
    private final static String TAG = "PKApi";

    /**
     * 获取pk信息
     *
     * @param roomId     房间id
     * @param resultBack 结果回调
     */
    static void getPKInfo(String roomId, IResultBack<PKResult> resultBack) {
        OkApi.get(Api.PK_INFO.replace(Api.KEY_ROOM_ID, roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "result:" + GsonUtil.obj2Json(result));
                if (null != result && result.ok()) {
                    PKResult pkResult = result.get(PKResult.class);
                    if (null != resultBack) resultBack.onResult(pkResult);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }

    /**
     * 同步服务端所在房间
     *
     * @param roomId     id
     * @param resultBack 回调
     */
    public static void synToService(String roomId, IResultBack<Boolean> resultBack) {
        //add 进房间标识
        Map<String, Object> params = new HashMap<>(2);
        params.put("roomId", roomId);
        OkApi.get(Api.USER_ROOM_CHANGE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null != resultBack) resultBack.onResult(result.ok());
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    public static void getRoomMembers(String roomId, IResultBack<List<Account>> resultBack) {
        OkApi.get(Api.MEMBERS.replace(Api.KEY_ROOM_ID, roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<Account> members = result.getList(Account.class);
                if (null == members) members = new ArrayList<>();
                for (Account account : members) {
                    AccoutManager.setAcctount(account, false);
                }
                if (null != resultBack) resultBack.onResult(members);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(new ArrayList<>());
            }
        });
    }
}
