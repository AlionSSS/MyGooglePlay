package com.skey.mygoogleplay.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.utils.BitmapHelper;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

public class AppPicsActivity extends Activity {

    private ArrayList<String> screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_pics);

        screen = getIntent().getStringArrayListExtra("screen");
        int currentItem = getIntent().getIntExtra("currentItem", 0);

        ViewPager vpPics = (ViewPager) findViewById(R.id.vp_pics);
        vpPics.setAdapter(new MyAdapter());
        vpPics.setCurrentItem(currentItem);
    }

    class MyAdapter extends PagerAdapter {

        private final BitmapUtils mBitmapUtils;

        MyAdapter() {
            mBitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public int getCount() {
            return screen.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(UIUtils.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            mBitmapUtils.display(view, HttpHelper.URL + "image?name=" + screen.get(position));
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
