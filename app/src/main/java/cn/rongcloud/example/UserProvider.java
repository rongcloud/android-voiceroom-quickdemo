package cn.rongcloud.example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.rongcloud.example.provider.AbsProvider;
import cn.rongcloud.example.provider.IProvider;
import cn.rongcloud.example.provider.IResultBack;

/**
 * 直接使用LruCache封装的用户信息提供者示例
 * 使用：
 * UserProvider.getProvider().get(id,new IResultBack<User>(){
 * public void onResult(User user){
 * <p>
 * }
 * });
 */
public class UserProvider extends AbsProvider<User> {
    private final static IProvider<User> _provider = new UserProvider();


    public static IProvider<User> getProvider() {
        return _provider;
    }

    private UserProvider() {
        super(-1);
    }

    @Override
    public void provideFromService(@NonNull String key, @Nullable IResultBack<User> resultBack) {

    }
}
