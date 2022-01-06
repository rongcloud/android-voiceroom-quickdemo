package cn.rongcloud.quickdemo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.interfaces.IAdapte;
import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.rongcloud.quickdemo.ApiFun;
import cn.rongcloud.quickdemo.QuickEventListener;
import cn.rongcloud.quickdemo.R;
import cn.rongcloud.quickdemo.interfaces.IResultBack;
import cn.rongcloud.quickdemo.uitls.AccoutManager;
import cn.rongcloud.quickdemo.uitls.UIKit;

public class ApiFunDialogHelper {
    private final static ApiFunDialogHelper seatApi = new ApiFunDialogHelper();
    public final static ApiFun[] SEAT_API = new ApiFun[]{
            ApiFun.seat_mute,
            ApiFun.seat_mute_un,
            ApiFun.seat_lock,
            ApiFun.seat_lock_un,
            ApiFun.seat_enter,
            ApiFun.seat_left,
            ApiFun.seat_request,
            ApiFun.seat_request_cancel,
            ApiFun.seat_extra,
            ApiFun.seat_pick_out,

    };
    public final static ApiFun[] ROOM_API = new ApiFun[]{
            ApiFun.room_all_mute,
            ApiFun.room_all_mute_un,
            ApiFun.room_all_lock,
            ApiFun.room_all_lock_nu,
            ApiFun.room_update_name,
            ApiFun.room_update_count,
            ApiFun.room_free,
            ApiFun.room_free_un,
            ApiFun.invite_seat,
            ApiFun.invite_pk,
            ApiFun.invite_pk_cancel,
            ApiFun.invite_pk_mute,
            ApiFun.invite_quit_pk,
    };


    private QDialog dialog;

    public static ApiFunDialogHelper helper() {
        return seatApi;
    }

    public interface OnApiClickListener {
        void onApiClick(View v, ApiFun fun);
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
    private void showApiDialog(Activity activity, String title, ApiFun[] apis, OnApiClickListener listener) {
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

    private View initApiFunView(ApiFun[] apiNames, OnApiClickListener listener) {
        RecyclerView refresh = new RecyclerView(dialog.getContext());
        refresh.setLayoutManager(new GridLayoutManager(dialog.getContext(), 2));
        IAdapte adapter = new ApiAdapter(dialog.getContext(), listener);
        adapter.setRefreshView(refresh);
        adapter.setData(Arrays.asList(apiNames), true);
        return refresh;
    }

    public void dismissDialog() {
        if (null != dialog) {
            dialog.dismiss();
        }
        dialog = null;
    }

    /**
     * 显示编辑框
     *
     * @param activity
     * @param title    标题
     */
    public void showEditorDialog(Activity activity, String title, IResultBack<String> resultBack) {
        showEditorDialog(activity, title, "", resultBack);
    }

    public void showEditorDialog(Activity activity, String title, String cofirm, IResultBack<String> resultBack) {
        if (null == dialog || !dialog.enable()) {
            dialog = new QDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ApiFunDialogHelper.this.dialog = null;
                        }
                    });
        }
        EditText editText = new EditText(dialog.getContext());
        editText.setHint("请输出房间id");
        dialog.replaceContent(title,
                "",
                null,
                TextUtils.isEmpty(cofirm) ? "新建/加入" : cofirm,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        String roomId = editText.getText().toString().trim();
                        if (null != resultBack) resultBack.onResult(roomId);
                    }
                },
                editText);
        dialog.show();
    }

    /**
     * 显示选择邀请观众的弹框
     *
     * @param activity
     * @param title
     * @param resultBack
     */
    public void showSelectDialog(Activity activity, String title, IResultBack<AccoutManager.Accout> resultBack, boolean all) {
        if (null == dialog || !dialog.enable()) {
            dialog = new QDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ApiFunDialogHelper.this.dialog = null;
                        }
                    });
        }
        RecyclerView refresh = new RecyclerView(dialog.getContext());
        IAdapte adapter = new RcySAdapter<AccoutManager.Accout, RcyHolder>(dialog.getContext(), R.layout.layout_item_selector) {
            @Override
            public void convert(RcyHolder holder, AccoutManager.Accout accout, int position) {
                holder.setText(R.id.selector_name, accout.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != resultBack) resultBack.onResult(accout);
                    }
                });
            }
        };
        refresh.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        adapter.setRefreshView(refresh);
        if (all) {
            adapter.setData(AccoutManager.getAccounts(), true);
        } else {
            List<AccoutManager.Accout> accounts = AccoutManager.getAccounts();
            List<AccoutManager.Accout> onlines = new ArrayList<>();
            for (AccoutManager.Accout a : accounts) {
                String id = a.getUserId();
                // 排除自己
                if (!id.equals(AccoutManager.getCurrentId()) && QuickEventListener.get().getAudienceIds().contains(a.getUserId())) {
                    onlines.add(a);
                }
            }
            adapter.setData(onlines, true);
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
                refresh);
        dialog.show();
    }

    public void showTipDialog(Activity activity,String message, IResultBack<Boolean> resultBack) {
        if (null == dialog || !dialog.enable()) {
            dialog = new QDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ApiFunDialogHelper.this.dialog = null;
                        }
                    });
        }
        TextView textView = new TextView(dialog.getContext());
        textView.setText(message);
        textView.setTextSize(18);
        textView.setTextColor(Color.parseColor("#343434"));
        UIKit.runOnUiTherad(new Runnable() {
            @Override
            public void run() {
                dialog.replaceContent("提示",
                        "取消",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissDialog();
                                if (null != resultBack) resultBack.onResult(false);
                            }
                        },
                        "确定",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissDialog();
                                if (null != resultBack) resultBack.onResult(true);
                            }
                        },
                        textView);
            }
        });
        dialog.show();
    }


    public void showTipDialog(Activity activity, String title, String message, IResultBack<Boolean> resultBack) {
        if (null == dialog || !dialog.enable()) {
            dialog = new QDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ApiFunDialogHelper.this.dialog = null;
                        }
                    });
        }
        TextView textView = new TextView(dialog.getContext());
        textView.setText(message);
        textView.setTextSize(18);
        textView.setTextColor(Color.parseColor("#343434"));
        UIKit.runOnUiTherad(new Runnable() {
            @Override
            public void run() {
                dialog.replaceContent(title,
                        "拒绝",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissDialog();
                                if (null != resultBack) resultBack.onResult(false);
                            }
                        },
                        "同意",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissDialog();
                                if (null != resultBack) resultBack.onResult(true);
                            }
                        },
                        textView);
            }
        });
        dialog.show();
    }

    /**
     * api功能适配器
     */
    private static class ApiAdapter extends RcySAdapter<ApiFun, RcyHolder> {
        private OnApiClickListener listener;

        private ApiAdapter(Context context, OnApiClickListener listener) {
            super(context, R.layout.layout_item_api);
            this.listener = listener;
        }

        @Override
        public void convert(RcyHolder holder, ApiFun s, int position) {
            holder.setText(R.id.api_fun, s.getValue());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != listener) listener.onApiClick(view, s);
                }
            });
        }
    }

}
