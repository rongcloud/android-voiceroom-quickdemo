package cn.rongcloud.example;

import android.net.Uri;

import cn.rongcloud.example.provider.Provide;
import io.rong.imlib.model.UserInfo;

/**
 * 模拟网络获取的用户信息实体
 */
public class User implements Provide {
    private String userId;
    private String userName;
    private String portraitUrl;

    @Override
    public String getKey() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserInfo toUserInfo() {
        return new UserInfo(userId, userName, Uri.parse(portraitUrl));
    }

    public static User fromUserInfo(UserInfo userInfo) {
        User user = new User();
        if (null != userInfo) {
            user.userId = userInfo.getUserId();
            user.userName = userInfo.getName();
            user.portraitUrl = userInfo.getPortraitUri().toString();
        }
        return user;
    }
}
