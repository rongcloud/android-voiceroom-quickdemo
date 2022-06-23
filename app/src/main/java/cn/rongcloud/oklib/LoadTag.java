package cn.rongcloud.oklib;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcq.refresh.progress.IndicatorView;
import com.bcq.refresh.progress.Style;

import cn.rongcloud.oklib.wrapper.interfaces.ILoadTag;
import cn.rongcloud.voicequickdemo.R;


/**
 * @author: BaiCQ
 * @ClassName: LoadTag
 * @Description: 标准加载进度条
 */
public class LoadTag implements ILoadTag {
    private Dialog dialog;
    private String loadMsg;
    private IndicatorView progressBar;
    private DialogInterface.OnDismissListener dismissListener;
    private TextView messageTextView;

    public LoadTag(Activity activity) {
        this(activity, activity.getString(R.string.re_loading));
    }

    public LoadTag(Activity activity, String dialogMsg) {
        dialog = new Dialog(activity, R.style.Basis_Style_WX_Dialog);
        View rootView = LayoutInflater.from(activity).inflate(R.layout.basis_layout_loadtag, null);
        progressBar = rootView.findViewById(R.id.prgressBar);
        progressBar.setStyle(Style.BallSpinFadeLoader);
        messageTextView = (TextView) rootView.findViewById(R.id.tv_load_msg);
        if (TextUtils.isEmpty(dialogMsg)) {
            dialogMsg = activity.getString(R.string.re_loading);
        }
        this.loadMsg = dialogMsg;
        messageTextView.setText(loadMsg);
        // 允许点返回键取消
        dialog.setCancelable(true);
        // 触碰其他地方不消失
        dialog.setCanceledOnTouchOutside(false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.addContentView(rootView, params);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != dismissListener) dismissListener.onDismiss(dialog);
            }
        });
    }

    public ILoadTag processStyle(Style processStyle) {
        if (null != progressBar) {
            progressBar.setStyle(processStyle);
        }
        return this;
    }

    @Override
    public ILoadTag setOnDismissListener(DialogInterface.OnDismissListener onDismiss) {
        this.dismissListener = onDismiss;
        return this;
    }

    @Override
    public String getTagMsg() {
        return loadMsg;
    }

    public void show(String msg) {
        if (messageTextView != null) {
            messageTextView.setText(msg);
        }
        show();
    }

    @Override
    public void show() {
        if (null != dialog) {
            dialog.show();
        }
    }

    public void dismiss() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
//                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}