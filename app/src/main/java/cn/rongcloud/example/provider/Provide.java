package cn.rongcloud.example.provider;

import java.io.Serializable;

/**
 * 可提供接口
 */
public interface Provide extends Serializable {
    /**
     * 获取唯一标识
     *
     * @return key 唯一标识
     */
    String getKey();
}
