package com.skey.mygoogleplay.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.skey.mygoogleplay.http.protocol.HotProtocol;
import com.skey.mygoogleplay.ui.view.FlowLayout;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.MyFlowLayout;
import com.skey.mygoogleplay.utils.DrawableUtils;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 排行Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class HotFragment extends BaseFragment {

    private ArrayList<String> data;

    @Override
    public View onCreateSuccessView() {
        ScrollView scrollView = new ScrollView(UIUtils.getContext());//支持上下滑动
//        FlowLayout flowLayout = new FlowLayout(UIUtils.getContext());
        MyFlowLayout flowLayout = new MyFlowLayout(UIUtils.getContext());

        int padding = UIUtils.dip2px(10);
        flowLayout.setPadding(padding, padding, padding, padding);//设置内边距

        flowLayout.setHorizontalSpacing(UIUtils.dip2px(6));//水平间距
        flowLayout.setVerticalSpacing(UIUtils.dip2px(8));//垂直间距

        for (int i = 0; i < data.size(); i++) {
            TextView view = new TextView(UIUtils.getContext());
            final String keyWord = data.get(i);
            view.setText(keyWord);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            view.setTextColor(Color.WHITE);
            view.setPadding(padding, padding, padding, padding);
            view.setGravity(Gravity.CENTER);

            //生成随机背景颜色
            Random random = new Random();
            int r = 30 + random.nextInt(201);//跳过颜色值，避免颜色过亮、过暗
            int g = 30 + random.nextInt(201);
            int b = 30 + random.nextInt(201);
            StateListDrawable selector = DrawableUtils.getSelector(
                    Color.rgb(r, g, b), 0xffcecece, UIUtils.dip2px(6));
            view.setBackgroundDrawable(selector);

            flowLayout.addView(view);

            //只有设置点击事件，selector才能起作用
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyWord, Toast.LENGTH_SHORT).show();
                }
            });
        }

        scrollView.addView(flowLayout);

        return scrollView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        HotProtocol protocol = new HotProtocol();
        data = protocol.getData(0);
        return check(data);
    }
}
