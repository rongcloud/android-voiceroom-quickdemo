package cn.rongcloud.voicequickdemo;

import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

public class Seat extends RCVoiceSeatInfo {
    private int audioLevel;

    public Seat(RCVoiceSeatInfo seatInfo) {
        this.audioLevel = 0;
        setExtra(seatInfo.getExtra());
        setMute(seatInfo.isMute());
        setStatus(seatInfo.getStatus());
        setUserId(seatInfo.getUserId());
    }

    public String getAudioLevel() {
        return "音量:" + audioLevel;
    }

    public void setAudioLevel(int audioLevel) {
        this.audioLevel = audioLevel;
    }

}
