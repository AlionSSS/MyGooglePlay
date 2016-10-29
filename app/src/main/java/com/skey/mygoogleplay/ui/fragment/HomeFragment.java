package com.skey.mygoogleplay.ui.fragment;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.BitmapUtils;
import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.domain.AppInfo;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.http.protocol.HomeProtocol;
import com.skey.mygoogleplay.ui.activity.AppDetailActivity;
import com.skey.mygoogleplay.ui.adapter.MyXBaseAdapter;
import com.skey.mygoogleplay.ui.holder.BaseHolder;
import com.skey.mygoogleplay.ui.holder.HomeHolder;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.MyListView;
import com.skey.mygoogleplay.utils.BitmapHelper;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 首页Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class HomeFragment extends BaseFragment {

    private ArrayList<AppInfo> data;
    private HomeProtocol protocol;
    private ArrayList<String> headerData;
    private ViewPager mViewPager;

    private int mPreviousPos;//上一个点的位置，int默认值是0
    private HomeHeaderTask task;

    //如果加载数据成功，就回调此方法，在主线程运行
    @Override
    public View onCreateSuccessView() {
//        TextView view = new TextView(getContext());
//        view.setText(getClass().getSimpleName());
        MyListView view = new MyListView(UIUtils.getContext());

        //给listView增加头布局，展示轮播条
        view.addHeaderView(initHeaderView());

        view.setAdapter(new HomeAdapter(data));//先添加头布局，再加载Adapter

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo = data.get(position - 1);//去掉头布局
                if (appInfo != null) {
                    Intent intent = new Intent(UIUtils.getContext(), AppDetailActivity.class);
                    intent.putExtra("packageName", appInfo.packageName);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private View initHeaderView() {
        //创建根布局，相对布局
        RelativeLayout rlRoot = new RelativeLayout(UIUtils.getContext());
        //初始化布局参数，根布局上层控件是ListView，所以要使用ListView定义的Params
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(160));
        rlRoot.setLayoutParams(params);

        //ViewPager
        mViewPager = new ViewPager(UIUtils.getContext());
        RelativeLayout.LayoutParams vpParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        mViewPager.setLayoutParams(vpParams);
        rlRoot.addView(mViewPager, vpParams);//添加布局

        //初始化指示器的布局
        final LinearLayout llContainer = new LinearLayout(UIUtils.getContext());
        llContainer.setOrientation(LinearLayout.HORIZONTAL);//水平布局
        int padding = UIUtils.dip2px(10);
        llContainer.setPadding(padding, padding, padding, padding);
        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//底部对齐
        llParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//右对齐
        rlRoot.addView(llContainer, llParams);//添加布局

        //初始化指示器的小圆点
        for (int i = 0; i < headerData.size(); i++) {
            ImageView point = new ImageView(UIUtils.getContext());
            LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i == 0) {
                point.setImageResource(R.drawable.indicator_selected);//第一个默认选中
            } else {
                point.setImageResource(R.drawable.indicator_normal);
                ivParams.leftMargin = UIUtils.dip2px(4);//左边距
            }
            point.setLayoutParams(ivParams);
            llContainer.addView(point);
        }

        mViewPager.setAdapter(new HomeHeaderAdapter());
        mViewPager.setCurrentItem(headerData.size() * 10000);
        mPreviousPos = 0;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                position = position % headerData.size();
                //当前点变为选中
                ImageView point = (ImageView) llContainer.getChildAt(position);
                point.setImageResource(R.drawable.indicator_selected);
                //上一个点变为不选中
                ImageView prePoint = (ImageView) llContainer.getChildAt(mPreviousPos);
                prePoint.setImageResource(R.drawable.indicator_normal);

                mPreviousPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //轮播条自动播放效果
        task = new HomeHeaderTask();
        task.start();

        //按住ViewPager停止轮播，放开时继续轮播
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        task.stop();
                        break;
                    case MotionEvent.ACTION_UP:
                        task.start();
                        break;
                }
                return false;
            }
        });

        return rlRoot;
    }

    //运行在子线程，可以直接执行耗时网络操作
    @Override
    public LoadingPage.ResultState onLoad() {
        //请求网络
//        data = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            data.add("测试数据：" + i);
//        }
        protocol = new HomeProtocol();
        //普通页数据
        data = protocol.getData(0);//加载第一页数据
        //头部轮播条数据
        ArrayList<String> pictureList = protocol.getPictureList();
        if (pictureList != null) {
            headerData = pictureList;
        }

        return check(data);//校验数据，并返回
    }

    class HomeHeaderTask implements Runnable {

        void start() {
            UIUtils.getHandler().removeCallbacksAndMessages(null);//移除之前所有的回调、消息，避免消息重复
            UIUtils.getHandler().postDelayed(this, 3000);
        }

        void stop() {
            UIUtils.getHandler().removeCallbacksAndMessages(null);//移除之前所有的回调、消息，避免消息重复
        }

        @Override
        public void run() {
            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(++currentItem);

            UIUtils.getHandler().postDelayed(this, 3000);//继续发消息，实现内循环
        }
    }

    class HomeAdapter extends MyXBaseAdapter<AppInfo> {

        public HomeAdapter(ArrayList<AppInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder<AppInfo> getHolder(int position) {
            return new HomeHolder();
        }

        // 此方法在子线程调用
        @Override
        public ArrayList<AppInfo> onLoadMore() {
            // ArrayList<String> moreData = new ArrayList<String>();
            // for(int i=0;i<20;i++) {
            // moreData.add("测试更多数据:" + i);
            // }
            //
            // SystemClock.sleep(2000);
            HomeProtocol protocol = new HomeProtocol();
            // 20, 40, 60....
            // 下一页数据的位置 等于 当前集合大小
            ArrayList<AppInfo> moreData = protocol.getData(getListSize());

            return moreData;
        }

    }

    private class HomeHeaderAdapter extends PagerAdapter {

        private final BitmapUtils mBitmapUtils;

        HomeHeaderAdapter() {
            mBitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public int getCount() {
//            return headerData.size();
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % headerData.size();

            ImageView view = new ImageView(UIUtils.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            mBitmapUtils.display(view, HttpHelper.URL + "image?name=" + headerData.get(position));
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
