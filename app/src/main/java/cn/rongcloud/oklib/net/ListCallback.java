package cn.rongcloud.oklib.net;


import java.util.List;

import cn.rongcloud.oklib.wrapper.interfaces.BusiCallback;
import cn.rongcloud.oklib.wrapper.interfaces.IPage;
import cn.rongcloud.oklib.wrapper.interfaces.IResult;

/**
 * @author: BaiCQ
 * @ClassName: ListCallback
 * @Description: 有body网络请求的回调
 */
public class ListCallback<R> implements BusiCallback<IResult.ObjResult<List<R>>, List<R>, IPage, R> {
    private Class<R> rClass;

    public ListCallback(Class<R> rClass) {
        this.rClass = rClass;
        if (null == rClass) {
            throw new IllegalArgumentException("The R Class<R> Can Not Null !");
        }
    }

    public void onResult(IResult.ObjResult<List<R>> result) {
    }

    public void onError(int code, String errMsg) {
    }

    @Override
    public void onAfter() {
    }

    @Override
    public Class<R> onGetType() {
        return rClass;
    }
}
