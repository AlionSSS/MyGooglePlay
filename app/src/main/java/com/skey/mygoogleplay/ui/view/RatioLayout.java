package com.skey.mygoogleplay.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.skey.mygoogleplay.R;

/**
 * 自定义控件，按照比例来决定布局高度
 *
 * @author ALion on 2016/10/19 22:40
 */

public class RatioLayout extends FrameLayout {

    private float ratio;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取属性值
//        attrs.getAttributeFloatValue("", "ration", -1);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        ratio = typedArray.getFloat(R.styleable.RatioLayout_ratio, -1);
        typedArray.recycle();//回收
//        System.out.println("ratio = " + ratio);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //widthMeasureSpec带有：模式信息 和 宽度信息

        //1.获取当前控件的宽度 2.根据宽度和ratio，计算控件的高度 3.重新测量控件
        int width = MeasureSpec.getSize(widthMeasureSpec);//宽度
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//宽度的模式
        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //宽度确定，高度不确定，ratio合法，才计算高度值
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio > 0) {
            //图片宽度 = 控件宽度 - 左侧内边距 - 右侧内边距
            int imageWidth = width - getPaddingLeft() - getPaddingRight();
            //图片高度 = 图片宽度 / 图片宽高比例（注：图片ratio和控件ratio有可能不同，因为有padding）
            int imageHeight = (int) (imageWidth / ratio + 0.5f);
            //控件高度 = 图片高度 + 上侧内边距 + 下侧内边距
            height = imageHeight + getPaddingTop() + getPaddingBottom();

            //根据最新的高度，来重新生成heightMeasureSpec（高度模式是确定模式EXACTLY）
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}