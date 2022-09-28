package cn.rongcloud.round;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {
    int[] radius;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        radius = RoundHelper.initRadius(context, attrs);
    }


    /**
     * 设置圆角的radius 单位dp
     *
     * @param leftTopRadius     左顶点圆角半径
     * @param rightTopRadius    右顶点圆角半径
     * @param rightBottomRadius 右底点圆角半径
     * @param leftBottomRadius  左底点圆角半径
     */
    public void setRadius(int leftTopRadius, int rightTopRadius, int rightBottomRadius, int leftBottomRadius) {
        radius = RoundHelper.formatRadius(leftTopRadius, rightTopRadius, rightBottomRadius, leftBottomRadius);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(RoundHelper.getClipPath(this, radius));
        super.onDraw(canvas);
    }
}