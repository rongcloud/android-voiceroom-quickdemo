package cn.rongcloud.example.provider;

import android.text.TextUtils;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbsProvider<T extends Provide> implements IProvider<T> {
    private final static int MAX_MEMORY = 2 * 1024 * 1024;
    protected final String TAG = getClass().getSimpleName();
    Map<String, IResultBack<T>> singleObservers = new HashMap<>(4);
    private final LruCache<String, T> lruCache;

    public AbsProvider(int max) {
        lruCache = new LruCache<>(Math.max(max, MAX_MEMORY));
    }

    @Override
    public void update(T t) {
        updateCache(Collections.singletonList(t));
    }


    @Override
    public void update(List<T> updates) {
        updateCache(updates);
    }

    @Override
    public void get(@NonNull String key, IResultBack<T> resultBack) {
        provideEntry(key, resultBack, false);
    }

    /**
     * @param key         实体的唯一标识
     * @param resultBack  回调
     * @param fromObserve 如果缓存中没有，请求网络成功后是否需要返回
     */
    private void provideEntry(@NonNull String key, IResultBack<T> resultBack, boolean fromObserve) {
        T t = lruCache.get(key);
        if (null != t && null != resultBack) {
            resultBack.onResult(t);
            return;
        }
        provideFromService(key, new IResultBack<T>() {
            @Override
            public void onResult(T t) {
                if (null != t) {
                    if (!fromObserve) {
                        //来至observe 不需要执行次回调 updateCache时会触发SingleObserver
                        if (null != resultBack) resultBack.onResult(t);
                    }
                    updateCache(Collections.singletonList(t));
                }
            }
        });
    }

    /**
     * 批量更新
     *
     * @param ts 待更新的集合
     */
    protected final void updateCache(List<T> ts) {
        int count = null == ts ? 0 : ts.size();
        for (int i = 0; i < count; i++) {
            T temp = ts.get(i);
            if (temp != null && !TextUtils.isEmpty(temp.getKey())) {
                lruCache.remove(temp.getKey());
                lruCache.put(temp.getKey(), temp);
                IResultBack<T> singleBack = singleObservers.get(temp.getKey());
                if (null != singleBack) singleBack.onResult(temp);
            }
        }
        if (count > 0) onUpdateComplete(ts);
    }

    /**
     * 清除所有缓存数据
     */
    public void clear() {
        lruCache.evictAll();
    }

    /**
     * 处理关联实体的跟新
     * 比如 VoiceRoom的creater 关联着User， 在跟新VoiceRoom时 顺便跟新一个关联的User实体
     *
     * @param data
     */
    protected void onUpdateComplete(List<T> data) {
    }

    public boolean contains(String key) {
        return lruCache.snapshot().containsKey(key);
    }

    @Override
    public void observe(@NonNull String id, @NonNull IResultBack<T> resultBack) {
        if (!singleObservers.containsKey(id)) {
            singleObservers.put(id, resultBack);
        }
        // 尝试回调回调一次
        provideEntry(id, resultBack, true);
    }

    @Override
    public void removeObserver(String key) {
        if (null != key) singleObservers.remove(key);
    }

    @Override
    public abstract void provideFromService(@NonNull String key, @Nullable IResultBack<T> resultBack);
}
