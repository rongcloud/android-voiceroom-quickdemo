package cn.rongcloud.round;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


/**
 * 圆角形状的视图
 */
public class RoundViewGroup extends FrameLayout {
    private int[] radius;

    public RoundViewGroup(Context context) {
        this(context, null);
    }

    public RoundViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
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

    /**
     * 裁剪画布
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(RoundHelper.getClipPath(this, radius));
        super.dispatchDraw(canvas);
    }
}
