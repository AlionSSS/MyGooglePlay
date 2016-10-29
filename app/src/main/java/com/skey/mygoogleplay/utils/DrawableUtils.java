package com.skey.mygoogleplay.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by Administrator on 2016/10/21.
 *
 * @author ALion on 2016/10/21 0:06
 */

public class DrawableUtils {

    //获取形状
    public static GradientDrawable getGradientDrawable(int radius, int color) {
        //xml中定义的shape标签对应此类
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);//矩形
        shape.setCornerRadius(radius);//圆角半径
        shape.setColor(color);//颜色

        return shape;
    }

    //获取状态选择器
    public static StateListDrawable getSelector(Drawable normal, Drawable pressed) {
        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[]{android.R.attr.state_pressed}, pressed);//按下的图片
        selector.addState(new int[]{}, normal);//默认图片

        return selector;
    }

    //获取状态选择器，重载
    public static StateListDrawable getSelector(int normal, int pressed, int radius) {
        GradientDrawable bgNormal = getGradientDrawable(radius, normal);
        GradientDrawable bgPressed = getGradientDrawable(radius, pressed);
        return getSelector(bgNormal, bgPressed);
    }
}
