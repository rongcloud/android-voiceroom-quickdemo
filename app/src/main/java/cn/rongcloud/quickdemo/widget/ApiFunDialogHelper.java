package cn.rongcloud.quickdemo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.interfaces.IAdapte;
import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import java.util.Arrays;

import cn.rongcloud.quickdemo.R;

public class ApiFunDialogHelper {
    private final static ApiFunDialogHelper seatApi = new ApiFunDialogHelper();
    public final static String[] SEAT_API = new String[]{
            "麦位静音",
            "取消静音",
            "麦位锁定",
            "取消锁定",
            "下麦",
            "上麦",
            "邀请上麦",
            "取消邀请上麦",
            "扩展属性"
    };

    public final static String[] ROOM_API = new String[]{
            "全麦锁定",
            "全麦解锁",
            "全麦静音",
            "取消全麦静音",
            "修改名称",
            "修改麦位数"
    };

    private QDialog dialog;

    public static ApiFunDialogHelper helper() {
        return seatApi;
    }

    public interface OnApiClickListener {
        void onApiClick(View v, int index);
    }

    public void showSeatApiDialog(Activity activity, OnApiClickListener listener) {
        showApiDialog(activity, "麦位Api功能演示", SEAT_API, listener);
    }

    public void showRoomApiDialog(Activity activity, OnApiClickListener listener) {
        showApiDialog(activity, "房间Api功能演示", ROOM_API, listener);
    }

    /**
     * 显示api演示弹框
     *
     * @param activity
     * @param title    标题
     * @param apis     api名称
     * @param listener 监听
     */
    private void showApiDialog(Activity activity, String title, String[] apis, OnApiClickListener listener) {
        if (null == dialog || !dialog.enable()) {
            dialog = new QDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ApiFunDialogHelper.this.dialog = null;
                        }
                    });
        }
        dialog.replaceContent(title,
                "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                    }
                },
                "",
                null,
                initApiFunView(apis, listener));
        dialog.show();
    }

    private View initApiFunView(String[] apiNames, OnApiClickListener listener) {
        RecyclerView refresh = new RecyclerView(dialog.getContext());
        refresh.setLayoutManager(new GridLayoutManager(dialog.getContext(),2));
        IAdapte adapter = new ApiAdapter(dialog.getContext(),listener);
        adapter.setRefreshView(refresh);
        adapter.setData(Arrays.asList(apiNames), true);
        return refresh;
    }

    private void dismissDialog() {
        if (null != dialog) {
            dialog.dismiss();
        }
        dialog = null;
    }

    /**
     * api功能适配器
     */
    private static class ApiAdapter extends RcySAdapter<String, RcyHolder> {
        private OnApiClickListener listener;

        private ApiAdapter(Context context, OnApiClickListener listener) {
            super(context, R.layout.layout_item_api);
            this.listener = listener;
        }

        @Override
        public void convert(RcyHolder holder, String s, int position) {
            holder.setText(R.id.api_fun, s);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != listener) listener.onApiClick(view, position);
                }
            });
        }
    }

}
