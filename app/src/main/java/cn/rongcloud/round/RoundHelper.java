package cn.rongcloud.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.kit.UIKit;

import cn.rongcloud.voicequickdemo.R;

/**
 * 圆角形状辅助类
 */
public class RoundHelper {
    public static int[] initRadius(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundHelper);
        int defaultRadius = 0;
        int radius = typedArray.getDimensionPixelOffset(R.styleable.RoundHelper_radius, defaultRadius);
        int leftTopRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundHelper_left_top_radius, defaultRadius);
        int rightTopRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundHelper_right_top_radius, defaultRadius);
        int leftBottomRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundHelper_left_bottom_radius, defaultRadius);
        int rightBottomRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundHelper_right_bottom_radius, defaultRadius);
        //如果四个角的值没有设置，就用通用的radius
        if (leftTopRadius == defaultRadius) {
            leftTopRadius = radius;
        }
        if (rightTopRadius == defaultRadius) {
            rightTopRadius = radius;
        }
        if (leftBottomRadius == defaultRadius) {
            leftBottomRadius = radius;
        }
        if (rightBottomRadius == defaultRadius) {
            rightBottomRadius = radius;
        }
        typedArray.recycle();
        return new int[]{leftTopRadius, rightTopRadius, rightBottomRadius, leftBottomRadius};
    }

    public static Path getClipPath(View view, int[] radius) {
        float width = view.getWidth();
        float height = view.getHeight();
        Path path = new Path();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        float rids[] = {radius[0], radius[0],
                radius[1], radius[1],
                radius[2], radius[2],
                radius[3], radius[3]};
        path.addRoundRect(new RectF(0, 0, width, height), rids, Path.Direction.CW);
        return path;
    }
//        protected void dispatchDraw(Canvas canvas) {
//        Path path = new Path();
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//        Logger.e("RoundViewGroup", "width = " + width);
//        Logger.e("RoundViewGroup", "height = " + height);
//
//        path.moveTo(0, leftTopRadius);
//        // top left
//        RectF tl = new RectF(0,
//                0,
//                2 * leftTopRadius,
//                2 * leftTopRadius);
//        path.arcTo(tl, 180, 90);
//        // top right
//        path.lineTo(width - rightTopRadius, 0);
//        RectF tr = new RectF(width - 2 * rightTopRadius,
//                0,
//                width,
//                2 * rightTopRadius);
//        path.arcTo(tr, 270, 90);
//        // bottom right
//        path.lineTo(width, height * rightBottomRadius);
//        RectF br = new RectF(width - 2 * rightBottomRadius,
//                height - 2 * rightBottomRadius,
//                width,
//                height);
//        path.arcTo(br, 0, 90);
//        // bottom left
//        path.lineTo(width - leftBottomRadius, height);
//        RectF bl = new RectF(0,
//                height - 2 * leftBottomRadius,
//                2 * leftBottomRadius,
//                height);
//        path.arcTo(bl, 90, 90);
//        path.close();
//        canvas.clipPath(path);
//        super.dispatchDraw(canvas);
//    }

    /**
     * 设置圆角的radius 单位dp
     *
     * @param leftTopRadius     左顶点圆角半径
     * @param rightTopRadius    右顶点圆角半径
     * @param rightBottomRadius 右底点圆角半径
     * @param leftBottomRadius  左底点圆角半径
     */
    public static int[] formatRadius(int leftTopRadius, int rightTopRadius, int rightBottomRadius, int leftBottomRadius) {
        int[] radius = new int[4];
        radius[0] = dp2px(leftTopRadius);
        radius[1] = dp2px(rightTopRadius);
        radius[2] = dp2px(rightBottomRadius);
        radius[3] = dp2px(leftBottomRadius);
        return radius;
    }

    public static int dp2px(float dp) {
        float density = UIKit.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}