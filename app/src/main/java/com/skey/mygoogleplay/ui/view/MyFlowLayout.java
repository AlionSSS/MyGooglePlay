package com.skey.mygoogleplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 自定义FlowLayout
 *
 * @author ALion on 2016/10/23 14:52
 */

public class MyFlowLayout extends ViewGroup {

    private int mUsedWidth;//当前行已使用的宽度
    private int mHorizontalSpacing = UIUtils.dip2px(6);//水平间距，默认
    private int mVerticalSpacing = UIUtils.dip2px(8);//竖直间距，默认

    private Line mLine;//当前行对象

    private ArrayList<Line> mLineList = new ArrayList<>();//维护所有Line集合

    private int MAX_LINES = 100;//最大行数100，默认

    public MyFlowLayout(Context context) {
        super(context);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHorizontalSpacing(int spacing) {
        if (mHorizontalSpacing != spacing) {
            mHorizontalSpacing = spacing;
            requestLayoutInner();
        }
    }

    public void setVerticalSpacing(int spacing) {
        if (mVerticalSpacing != spacing) {
            mVerticalSpacing = spacing;
            requestLayoutInner();
        }
    }

    public void setMaxLines(int maxLines) {
        if (MAX_LINES != maxLines) {
            MAX_LINES = maxLines;
            requestLayoutInner();
        }
    }

    private void requestLayoutInner() {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = l + getPaddingLeft();
        int top = t + getPaddingTop();

        //遍历所有行对象，设置每行位置
        for (int i = 0; i < mLineList.size(); i++) {
            Line line = mLineList.get(i);
            line.layout(left, top);
            top += line.mMaxHeight + mVerticalSpacing;//更新top值
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //获取有效宽度、高度
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //获取所有子控件的数量
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //如果父控件是确定模式，子控件就包裹内容，否则子控件模式和父控件一致
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                    (widthMode == MeasureSpec.EXACTLY) ? MeasureSpec.AT_MOST : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    (heightMode == MeasureSpec.EXACTLY) ? MeasureSpec.AT_MOST : heightMode);
            //开始测量
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            if (mLine == null) {    //如果当前行为空，初始化一个Line对象
                mLine = new Line();
            }

            int childWidth = childView.getMeasuredWidth();//子控件宽度
            mUsedWidth += childWidth;//当前已使用的控件增加一个宽度
            if (mUsedWidth < width) {   //判断是否超出了宽边界
                mLine.addView(childView);//给当前行添加子控件
                mUsedWidth += mHorizontalSpacing;
                if (mUsedWidth > width) {   //增加水平间距后超出边界，此时需要换行
                    if (!newLine()) {
                        break;//超出最大行，结束循环，不再添加
                    }
                }
            } else {    //超出宽边界
                if (mLine.getChildCount() == 0) {   //1.当前没有任何元素，一添加第一个子控件，就超出边界
                    mLine.addView(childView);//强制添加到当前行
                    if (!newLine()) {   //保存，换行
                        break;
                    }
                } else {    //2.当前有元素，一添加下一个子控件，就超出
                    if (!newLine()) {//先换行
                        break;
                    }
                    mLine.addView(childView);//再添加
                    mUsedWidth += childWidth + mHorizontalSpacing;//更新已使用宽度
                }
            }
        }

        //保存最后一行的对象
        if (mLine != null && mLine.getChildCount() != 0 && !mLineList.contains(mLine)) {
            mLineList.add(mLine);
        }

        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int totalHeight = 0;
        for (int i = 0; i < mLineList.size(); i++) {
            Line line = mLineList.get(i);
            totalHeight += line.mMaxHeight;
        }
        totalHeight += (mLineList.size() - 1) * mVerticalSpacing;//增加竖直间距
        totalHeight += getPaddingTop() + getPaddingBottom();//增加上下内边距

        //根据最新的宽高来测量整体布局大小
//        setMeasuredDimension(totalWidth, totalHeight);
        setMeasuredDimension(totalWidth, resolveSize(totalHeight, heightMeasureSpec));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 换行
     */
    private boolean newLine() {
        mLineList.add(mLine);//保存上一行
        if (mLineList.size() < MAX_LINES) { //如果小于最大行，可以继续添加
            mLine = new Line();
            mUsedWidth = 0;//新行，已使用宽度清零
            return true;//创建成功
        }
        return false;//超出最大行数，失败
    }

    //每一行的对象封装
    class Line {

        private int mTotalWidth;//当前所有控件的总宽度
        private int mMaxHeight;//当前控件的高度（以最高的控件为准）

        private ArrayList<View> mChildViewList = new ArrayList<>();//当前行所有子控件集合

        //添加一个子控件
        public void addView(View view) {
            mChildViewList.add(view);
            mTotalWidth += view.getMeasuredWidth();//总宽度增加
            int height = view.getMeasuredHeight();
            mMaxHeight = mMaxHeight < height ? height : mMaxHeight;
        }

        //获取孩子的数量
        public int getChildCount() {
            return mChildViewList.size();
        }

        //子控件位置设置
        public void layout(int left, int top) {
            int childCount = getChildCount();

            //计算剩余多少空间，平均分配给每个子控件
            int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();//有效宽度
            int surplusWidth = validWidth - mTotalWidth - (childCount - 1) * mHorizontalSpacing;//剩余宽度 = 有效宽度 - 当前已使用的总宽度 - 水平间距
            if (surplusWidth >= 0) {    //说明有剩余空间
                int averageWidth = (int) ((float) surplusWidth / childCount + 0.5f);//平均每个子控件分配的大小

                //重新测量子控件
                for (int i = 0; i < childCount; i++) {
                    View childView = mChildViewList.get(i);
                    int measuredWidth = childView.getMeasuredWidth();
                    int measuredHeight = childView.getMeasuredHeight();
                    measuredWidth += averageWidth;
                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
                    childView.measure(widthMeasureSpec, heightMeasureSpec);//重新测量控件

                    //使所有子控件centerVertical
                    int topOffset = (mMaxHeight - measuredHeight) / 2;
                    if (topOffset < 0) {
                        topOffset = 0;
                    }
                    childView.layout(left, top + topOffset, left + measuredWidth, top + topOffset + measuredHeight);
                    left += measuredWidth + mHorizontalSpacing;//更新left值，每一个控件不同
                }

            } else {    //这个控件很长，没有剩余空间
                View childView = mChildViewList.get(0);
                childView.layout(left, top, left + childView.getMeasuredWidth(), top + childView.getMeasuredHeight());
            }
        }
    }
}
