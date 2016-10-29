package com.skey.mygoogleplay.ui.fragment;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.skey.mygoogleplay.http.protocol.RecommendProtocol;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.fly.ShakeListener;
import com.skey.mygoogleplay.ui.view.fly.StellarMap;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 推荐Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class RecommendFragment extends BaseFragment {

    private ArrayList<String> data;

    @Override
    public View onCreateSuccessView() {
        final StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        stellarMap.setAdapter(new RecommendAdapter());

        stellarMap.setRegularity(6, 9);//随机的方式，6列 9行

        int padding = UIUtils.dip2px(10);
        stellarMap.setInnerPadding(padding, padding, padding, padding);//设置内边距

        stellarMap.setGroup(0, true);//默认页面，第一组

        //手机摇晃监听
        ShakeListener shakeListener = new ShakeListener(UIUtils.getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn();//跳到下一页
            }
        });

        return stellarMap;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        RecommendProtocol protocol = new RecommendProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class RecommendAdapter implements StellarMap.Adapter {

        //返回一共有多少组
        @Override
        public int getGroupCount() {
            return 2;
        }

        //返回某组的item个数
        @Override
        public int getCount(int group) {
            int count = data.size() / getGroupCount();
            if (group == getGroupCount() - 1) {
                //将除不尽的余数追加在最后一页，保证数据完整不丢失
                count += data.size() % getGroupCount();
            }
            return count;
        }

        //初始化布局
        @Override
        public View getView(int group, int position, View convertView) {
            TextView view = new TextView(UIUtils.getContext());
            position += group * getCount(group - 1);//position每次都会从0开始计数，所以需要将前面的数据加起来，才是当前的position
            final String keyWord = data.get(position);
            view.setText(keyWord);

            Random random = new Random();
            //文字随机大小
            int size = 16 + random.nextInt(10);//16-25
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            //文字随机颜色 R, G, B -> 30-200
            int r = 30 + random.nextInt(201);//跳过颜色值，避免颜色过亮、过暗
            int g = 30 + random.nextInt(201);
            int b = 30 + random.nextInt(201);
            view.setTextColor(Color.rgb(r, g, b));

            //TextView的点击监听
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyWord, Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        //返回下一组的id
        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            if (isZoomIn) { //往下滑动，加载上一页
                if (group > 0) {
                    group--;
                } else {
                    group = getGroupCount() - 1;//跳到最后一页
                }
            } else {    //往上滑动，加载下一页
                if (group < getGroupCount() - 1) {
                    group++;
                } else {
                    group = 0;//跳到第一页
                }
            }
            return group;
        }
    }
}
