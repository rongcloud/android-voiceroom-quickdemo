package cn.rongcloud.quickdemo;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kit.UIKit;
import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;
import com.kit.utils.PermissionUtil;

/**
 * @author: BaiCQ
 * @createTime: 2017/1/13 11:38
 * @className: AbsPermissionActivity
 * @Description: 权限申请基类
 */
public abstract class AbsPermissionActivity extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();

    protected final static String[] VOICE_PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    @Deprecated
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtil.checkPermissions(this, onCheckPermission())) {
            onPermissionAccept(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.REQUEST_CODE == requestCode) {
            String[] arr = PermissionUtil.getDeniedPermissions(this, permissions);
            Logger.e(TAG, "arr = " + GsonUtil.obj2Json(arr));
            boolean accept = null == arr || 0 == arr.length;
            onPermissionAccept(accept);
        }
    }


    /**
     * 设置检测权限的数组
     *
     * @return
     */
    protected abstract String[] onCheckPermission();

    /**
     * 权限检测结果
     *
     * @param accept
     */
    protected abstract void onPermissionAccept(boolean accept);
}