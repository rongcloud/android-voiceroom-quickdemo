package cn.rongcloud.oklib.api.callback;


import cn.rongcloud.oklib.api.OCallBack;
import okhttp3.Response;

public abstract class StringIOCallBack extends OCallBack<String> {
    @Override
    public String onParse(Response response) throws Exception {
        return response.body().string();
    }
}
