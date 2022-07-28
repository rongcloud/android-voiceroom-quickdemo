package cn.rongcloud.example.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.List;

/**
 * 实体provider的接口
 *
 * @param <T>
 */
public interface IProvider<T> {

    /**
     * 更新
     *
     * @param t
     */
    void update(T t);

    /**
     * 批量更新
     *
     * @param updates
     */
    void update(List<T> updates);


    /**
     * 异步获取 缓存没有尝试从网络取
     *
     * @param key
     * @param resultBack
     */
    void get(@NonNull String key, IResultBack<T> resultBack);


    /**
     * 监听指定key的实体
     *
     * @param key
     * @param resultBack
     */
    void observe(@NonNull String key, @NonNull IResultBack<T> resultBack);

    /**
     * 移除单个实例监听器
     *
     * @param key
     */
    void removeObserver(String key);


    /**
     * 远端获取实例
     *  @param key        参数
     * @param resultBack
     */
    void provideFromService(@NonNull String key, @Nullable IResultBack<T> resultBack);

}
