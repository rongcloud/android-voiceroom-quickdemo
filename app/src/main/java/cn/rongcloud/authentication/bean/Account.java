package cn.rongcloud.authentication.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Objects;

import cn.rongcloud.authentication.Api;

public class Account implements Serializable {
    String userId;
    String userName;
    String portrait;
    int type;
    String authorization;
    String imToken;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPortrait() {
        return TextUtils.isEmpty(portrait) ?
                Api.DEFAULT_PORTRAIT
                : Api.FILE_URL + portrait;
    }

    public int getType() {
        return type;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getImToken() {
        return imToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account accout = (Account) o;
        return Objects.equals(userId, accout.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
