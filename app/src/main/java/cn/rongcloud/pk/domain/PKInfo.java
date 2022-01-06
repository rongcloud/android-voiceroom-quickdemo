package cn.rongcloud.pk.domain;

import java.io.Serializable;
import java.util.List;

import cn.rongcloud.authentication.bean.Account;

public class PKInfo implements Serializable {
    private String roomId;
    private int score;
    private List<Account> userInfoList;

    public int getScore() {
        return score;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<Account> getUserInfoList() {
        return userInfoList;
    }

    private String userId;

    public String getUserId() {
        return userId;
    }
}
