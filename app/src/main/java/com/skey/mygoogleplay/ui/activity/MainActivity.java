package com.skey.mygoogleplay.ui.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.ui.fragment.BaseFragment;
import com.skey.mygoogleplay.ui.fragment.FragmentFactory;
import com.skey.mygoogleplay.ui.view.PagerTab;
import com.skey.mygoogleplay.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity {

    private Toolbar mToolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private PagerTab mPagerTab;
    private ViewPager mViewPager;
    private ImageButton ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);//支持ActionBar的方法，例如右边的menu布局、点击事件
//        mToolbar.setLogo(R.drawable.ic_launcher);
//        mToolbar.setTitle("谷歌电子市场");
        mToolbar.setNavigationIcon(R.drawable.ic_drawer_am);
//        mToolbar.setTitleTextColor(Color.BLACK);
//        mToolbar.setSubtitle("Sub title");
//        mToolbar.inflateMenu(R.menu.main);//设置Toolbar的右边menu，不设置setSupportActionBar时，使用此方法


//        ActionBar actionBar = getSupportActionBar();
////        actionBar.setIcon(R.drawable.ic_launcher);
//        actionBar.setLogo(R.drawable.ic_launcher);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setHomeButtonEnabled(true);//logo是否可以点击
//        actionBar.setDisplayShowHomeEnabled(true);//显示or隐藏logo
//        actionBar.setDisplayHomeAsUpEnabled(true);//显示左上角返回键home

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        initView();
        initListener();
        initData();
    }

    //加载ActionBar菜单布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    //ActionBar点击事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.action_search:
                Toast.makeText(UIUtils.getContext(), "开始搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(UIUtils.getContext(), "点开设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_about:
                Toast.makeText(UIUtils.getContext(), "关于我们", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long currentBackPressedTime = 0;
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (SystemClock.elapsedRealtime()- currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = SystemClock.elapsedRealtime();
                Toast.makeText(UIUtils.getContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU: //拦截menu键事件
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mPagerTab = (PagerTab) findViewById(R.id.pager_tab);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ivLogo = (ImageButton) findViewById(R.id.ib_logo);
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment = FragmentFactory.createFragment(position);
                fragment.loadData();//开始加载数据
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        MyAdapter mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mPagerTab.setViewPager(mViewPager);//将指针和viewPager绑定
    }

    /**
     * FragmentPagerAdapter是PagerAdapter的子类
     */
    class MyAdapter extends FragmentPagerAdapter {

        private final String[] mTabNames;

        MyAdapter(FragmentManager fm) {
            super(fm);
            //加载页签标题数组
            mTabNames = UIUtils.getStringArray(R.array.tab_names);
        }

        //返回页签的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment fragment = FragmentFactory.createFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabNames.length;
        }
    }
}
