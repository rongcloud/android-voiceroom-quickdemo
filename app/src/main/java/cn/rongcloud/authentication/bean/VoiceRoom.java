package cn.rongcloud.authentication.bean;

import java.io.Serializable;

public class VoiceRoom implements Serializable {

    private int id;
    private String roomId;
    private String roomName;
    private String themePictureUrl;
    private String backgroundUrl;
    private int isPrivate;
    private String password;
    private String userId;
    private long updateDt;
    private Account createUser;
    private int roomType;
    private int userTotal;
    private String stopEndTime;
    private String currentTime;
    private boolean stop;

    public int getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getThemePictureUrl() {
        return themePictureUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public Account getCreateUser() {
        return createUser;
    }

    public int getRoomType() {
        return roomType;
    }

    public int getUserTotal() {
        return userTotal;
    }

    public String getStopEndTime() {
        return stopEndTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public boolean isStop() {
        return stop;
    }
}
