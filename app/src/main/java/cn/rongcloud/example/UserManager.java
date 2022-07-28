package cn.rongcloud.example;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.authentication.Api;
import cn.rongcloud.example.provider.IProvider;
import cn.rongcloud.example.provider.IResultBack;
import cn.rongcloud.oklib.OkApi;
import cn.rongcloud.oklib.WrapperCallBack;
import cn.rongcloud.oklib.wrapper.Wrapper;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.UserDataProvider;
import io.rong.imkit.userinfo.model.GroupUserInfo;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * 结合imkit的RongUserInfoManager封装使用用户信息提供者
 */
public class UserManager implements IProvider<UserInfo> {
    private final static String TAG = "UserProvider";
    private final static IProvider<UserInfo> _provider = new UserManager();
    private final UserObserver datObserver;
    private final Map<String, IResultBack<UserInfo>> observers = new HashMap<>(4);
    private final Map<String, IResultBack<UserInfo>> onceObservers = new HashMap<>(4);//只监听一次

    private UserManager() {
        datObserver = new UserObserver(new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                String userId = null == userInfo ? null : userInfo.getUserId();
                if (TextUtils.isEmpty(userId)) {
                    return;
                }
                IResultBack<UserInfo> onceBack = onceObservers.remove(userInfo.getUserId());
                if (null != onceBack) onceBack.onResult(userInfo);
                IResultBack<UserInfo> observer = observers.get(userInfo.getUserId());
                if (null != observer) observer.onResult(userInfo);
            }
        });
        RongUserInfoManager.getInstance().addUserDataObserver(datObserver);
        //设置IM user provider 使用Im 提供的缓存机制
        RongUserInfoManager.getInstance().setUserInfoProvider(new UserDataProvider.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                provideFromService(s, null);
                return null;
            }
        }, true);
    }

    public static IProvider<UserInfo> provider() {
        return _provider;
    }


    public void release() {
        RongUserInfoManager.getInstance().removeUserDataObserver(datObserver);
    }

    private UserInfo get(@NonNull String userId) {
        return RongUserInfoManager.getInstance().getUserInfo(userId);
    }

    @Override
    public void update(UserInfo userInfo) {
        RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo);
    }

    @Override
    public void update(List<UserInfo> updates) {
        for (UserInfo u : updates) {
            RongUserInfoManager.getInstance().refreshUserInfoCache(u);
        }
    }

    @Override
    public void get(@NonNull String userId, @NonNull IResultBack<UserInfo> resultBack) {
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (null != info) {
            resultBack.onResult(info);
            return;
        }
        //执行provider
        onceObservers.put(userId, resultBack);
    }

    @Override
    public void observe(@NonNull String userId, @NonNull IResultBack<UserInfo> resultBack) {
        if (!observers.containsKey(userId)) observers.put(userId, resultBack);
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (null != info) {
            resultBack.onResult(info);
        }
    }

    @Override
    public void removeObserver(String key) {
        if (null != key) observers.remove(key);
    }

    /**
     * @param id         参数:params.put("userIds", keys);
     * @param resultBack
     */
    @Override
    public void provideFromService(String id, @Nullable IResultBack<UserInfo> resultBack) {
        if (TextUtils.isEmpty(id)) {
            if (null != resultBack) resultBack.onResult(null);
            return;
        }
        getUserFormService(id, new IResultBack<User>() {
            @Override
            public void onResult(User user) {
                UserInfo info = null;
                if (null != user) {
                    // user 转换成UserInfo
                    info = user.toUserInfo();
                    RongUserInfoManager.getInstance().refreshUserInfoCache(info);
                }
                if (null != resultBack) resultBack.onResult(info);
            }
        });
    }

    private final static String API_BATCH = Api.HOST + "/user/batch";

    private void getUserFormService(String userId, IResultBack<User> resultBack) {
        // TODO: 2022/7/28 具体实现从服务端获取实体,并通过resultBack.onResult(user)回调
        Map<String, Object> params = new HashMap<>(2);
        params.put("userIds", Arrays.asList(userId));
        OkApi.post(API_BATCH, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "provideFromService#onError code  = " + code + " message = " + msg);
                if (null != resultBack) resultBack.onResult(null);
            }

            @Override
            public void onResult(Wrapper result) {
                List<User> users = result.getList(User.class);
                Log.e(TAG, "provideFromService: size = " + (null == users ? 0 : users.size()));
                User user = null;
                if (null != users && !users.isEmpty()) {
                    user = users.get(0);
                }
                if (null != resultBack) resultBack.onResult(user);
            }
        });
    }

    private static class UserObserver implements RongUserInfoManager.UserDataObserver {
        private final IResultBack<UserInfo> resultBack;

        UserObserver(IResultBack<UserInfo> resultBack) {
            this.resultBack = resultBack;
        }

        @Override
        public void onUserUpdate(UserInfo userInfo) {
            if (null != resultBack) resultBack.onResult(userInfo);
        }

        @Override
        public void onGroupUpdate(Group group) {
        }

        @Override
        public void onGroupUserInfoUpdate(GroupUserInfo groupUserInfo) {
        }
    }
}
