package cn.rongcloud.pk.domain;


public class PKInfo extends RCChatroomPK.RoomScore {
    private String roomId;
    private int score;
    private List<User> userInfoList;

    public int getScore() {
        return score;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<User> getUserInfoList() {
        return userInfoList;
    }

    private String userId;

    public String getUserId() {
        return userId;
    }
}
