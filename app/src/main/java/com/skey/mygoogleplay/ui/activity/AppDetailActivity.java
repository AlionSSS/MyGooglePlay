package com.skey.mygoogleplay.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.domain.AppInfo;
import com.skey.mygoogleplay.domain.DownloadInfo;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.http.protocol.HomeDetailProtocol;
import com.skey.mygoogleplay.manager.DownloadManager;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.ProgressHorizontal;
import com.skey.mygoogleplay.utils.BitmapHelper;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

public class AppDetailActivity extends BaseActivity {

    private Toolbar mToolbar;

    private LoadingPage mLoadingPage;
    private String packageName;
    private AppInfo data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("应用详情");
        mToolbar.setNavigationIcon(R.drawable.ic_drawer_am);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //正文页面
        mLoadingPage = new LoadingPage(UIUtils.getContext()) {

            @Override
            public View onCreateSuccessView() {
                return AppDetailActivity.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return AppDetailActivity.this.onLoad();
            }
        };
        FrameLayout flAppDetail = (FrameLayout) findViewById(R.id.fl_app_detail);
        flAppDetail.addView(mLoadingPage);

        packageName = getIntent().getStringExtra("packageName");//获取从HomeFragment传递过来的包名

        mLoadingPage.loadData();//开始加载数据

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                Toast.makeText(UIUtils.getContext(), "分享应用:" + data.name, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public LoadingPage.ResultState onLoad() {
        //请求网络，加载数据
        HomeDetailProtocol protocol = new HomeDetailProtocol(packageName);
        data = protocol.getData(0);
        if (data != null) {
            return LoadingPage.ResultState.STATE_SUCCESS;
        } else {
            return LoadingPage.ResultState.STATE_ERROR;
        }
    }

    public View onCreateSuccessView() {
        //初始化成功的布局
        View view = UIUtils.inflate(R.layout.content_app_detail);

        //初始化应用信息模块
        FrameLayout flAppInfo = (FrameLayout) view.findViewById(R.id.fl_app_info);
        flAppInfo.addView(initAppInfoView());//动态给帧布局填充页面

        //初始化安全描述模块
        FrameLayout flAppSafe = (FrameLayout) view.findViewById(R.id.fl_app_safe);
        flAppSafe.addView(initSafeView());

        //初始化截图展示模块
        HorizontalScrollView hsvAppPics = (HorizontalScrollView) view.findViewById(R.id.hsv_app_pics);
        hsvAppPics.addView(initAppPics());

        //初始化描述模块
        FrameLayout flAppDes = (FrameLayout) view.findViewById(R.id.fl_app_des);
        flAppDes.addView(initAppDes());

        //初始化下载模块
        FrameLayout flAppDownload = (FrameLayout) view.findViewById(R.id.fl_app_download);
        flAppDownload.addView(initAppDownload());

        return view;
    }

    private View initAppInfoView() {
        View view = UIUtils.inflate(R.layout.item_app_detail_info);

        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvDownloadNum = (TextView) view.findViewById(R.id.tv_download_num);
        TextView tvVersion = (TextView) view.findViewById(R.id.tv_version);
        TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
        TextView tvSize = (TextView) view.findViewById(R.id.tv_size);
        RatingBar rbStar = (RatingBar) view.findViewById(R.id.rb_star);

        BitmapUtils mBitmapUtils = BitmapHelper.getBitmapUtils();
        mBitmapUtils.display(iv_icon, HttpHelper.URL + "image?name=" + data.iconUrl);
        tvName.setText(data.name);
        tvDownloadNum.setText("下载量:" + data.downloadNum);
        tvVersion.setText("版本号:" + data.version);
        tvDate.setText("日期:" + data.date);
        tvSize.setText("大小:" + Formatter.formatFileSize(UIUtils.getContext(), data.size));
        rbStar.setRating(data.stars);

        return view;
    }


    private boolean isSafeOpen = false;//标记安全描述view的开关

    private View initSafeView() {
        View view = UIUtils.inflate(R.layout.item_app_detail_safe);


        int[] ivSafeInt = new int[]{R.id.iv_safe1, R.id.iv_safe2, R.id.iv_safe3, R.id.iv_safe4};
        int[] ivDesInt = new int[]{R.id.iv_des1, R.id.iv_des2, R.id.iv_des3, R.id.iv_des4};
        int[] tvDesInt = new int[]{R.id.tv_des1, R.id.tv_des2, R.id.tv_des3, R.id.tv_des4};
        int[] llDesInt = new int[]{R.id.ll_des1, R.id.ll_des2, R.id.ll_des3, R.id.ll_des4};
        ImageView[] ivSafe = new ImageView[ivSafeInt.length];
        ImageView[] ivDes = new ImageView[ivDesInt.length];
        TextView[] tvDes = new TextView[tvDesInt.length];
        LinearLayout[] llDes = new LinearLayout[llDesInt.length];

        BitmapUtils mBitmapUtils = BitmapHelper.getBitmapUtils();
        ArrayList<AppInfo.SafeInfo> safe = data.safe;
        for (int i = 0; i < 4; i++) {
            ivSafe[i] = (ImageView) view.findViewById(ivSafeInt[i]);
            ivDes[i] = (ImageView) view.findViewById(ivDesInt[i]);
            tvDes[i] = (TextView) view.findViewById(tvDesInt[i]);
            llDes[i] = (LinearLayout) view.findViewById(llDesInt[i]);

            if (i < safe.size()) {
                mBitmapUtils.display(ivSafe[i], HttpHelper.URL + "image?name=" + safe.get(i).safeUrl);
                mBitmapUtils.display(ivDes[i], HttpHelper.URL + "image?name=" + safe.get(i).safeDesUrl);
                tvDes[i].setText(data.safe.get(i).safeDes);
            } else {
                ivDes[i].setVisibility(View.GONE);
                llDes[i].setVisibility(View.GONE);
            }
        }

        //点击收放抽屉效果
        RelativeLayout rlDesRoot = (RelativeLayout) view.findViewById(R.id.rl_des_root);
        final LinearLayout llDesRoot = (LinearLayout) view.findViewById(R.id.ll_des_root);
        final ImageView ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);

        llDesRoot.measure(0, 0);
        final int measuredHeight = llDesRoot.getMeasuredHeight();
        //修改安全描述布局高度为0，默认隐藏
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llDesRoot.getLayoutParams();
        params.height = 0;
        llDesRoot.setLayoutParams(params);
        rlDesRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueAnimator animator = isSafeOpen ?
                        ValueAnimator.ofInt(measuredHeight, 0) : ValueAnimator.ofInt(0, measuredHeight);
                isSafeOpen = !isSafeOpen;

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.height = (Integer) animation.getAnimatedValue();
                        llDesRoot.setLayoutParams(params);
                    }
                });
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //动画结束，更新小箭头
                        if (isSafeOpen)
                            ivArrow.setImageResource(R.drawable.arrow_up);
                        else
                            ivArrow.setImageResource(R.drawable.arrow_down);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animator.setDuration(200);
                animator.start();
            }
        });

        return view;
    }


    private View initAppPics() {
        View view = UIUtils.inflate(R.layout.item_app_detail_pics);

        int[] picsInt = new int[]{R.id.iv_pic1, R.id.iv_pic2, R.id.iv_pic3, R.id.iv_pic4, R.id.iv_pic5};
        ImageView[] ivPics = new ImageView[picsInt.length];

        BitmapUtils mBitmapUtils = BitmapHelper.getBitmapUtils();
        final ArrayList<String> screen = data.screen;
        for (int i = 0; i < picsInt.length; i++) {
            ivPics[i] = (ImageView) view.findViewById(picsInt[i]);

            if (i < screen.size()) {
                mBitmapUtils.display(ivPics[i], HttpHelper.URL + "image?name=" + screen.get(i));
                final int finalI = i;
                ivPics[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AppDetailActivity.this, AppPicsActivity.class);
                        intent.putExtra("screen", screen);
                        intent.putExtra("currentItem", finalI);
                        startActivity(intent);
                    }
                });
            } else {
                ivPics[i].setVisibility(View.GONE);
            }
        }

        return view;
    }


    private boolean isDesOpen = false;

    private View initAppDes() {
        View view = UIUtils.inflate(R.layout.item_app_detail_des);

        final TextView tvDetailDes = (TextView) view.findViewById(R.id.tv_detail_des);
        TextView tvDetailAuthor = (TextView) view.findViewById(R.id.tv_detail_author);
        final ImageView ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        final RelativeLayout rlDetailToggle = (RelativeLayout) view.findViewById(R.id.rl_detail_toggle);

        tvDetailDes.setText(data.des);
        tvDetailAuthor.setText(data.author);

        tvDetailDes.post(new Runnable() {
            @Override
            public void run() {
                rlView(tvDetailDes, ivArrow, rlDetailToggle);
            }
        });

        return view;
    }

    private void rlView(final TextView tvDetailDes, final ImageView ivArrow, RelativeLayout rlDetailToggle) {
        //默认展示7行
        final int shortHeight = getTVHeight(tvDetailDes, 7);
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvDetailDes.getLayoutParams();
//        params.height = shortHeight;
//        tvDetailDes.setLayoutParams(params);

        //点击收放效果
        final int longHeight = getTVHeight(tvDetailDes, 100);
        rlDetailToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longHeight > shortHeight) { //只有最大高度 > 7行，才有动画
                    ValueAnimator animator = isDesOpen ?
                            ValueAnimator.ofInt(longHeight, shortHeight) : ValueAnimator.ofInt(shortHeight, longHeight);
                    isDesOpen = !isDesOpen;

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            params.height = (Integer) animation.getAnimatedValue();
                            tvDetailDes.setLayoutParams(params);
                        }
                    });
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //动画结束，更新小箭头
                            if (isDesOpen)
                                ivArrow.setImageResource(R.drawable.arrow_up);
                            else
                                ivArrow.setImageResource(R.drawable.arrow_down);

                            //ScrollView要滑动到最底部
                            final ScrollView scrollView = getScrollVie(tvDetailDes);
                            if (scrollView != null) {
                                scrollView.post(new Runnable() {    //为了更加安全和稳定，可以将滑动到底部方法放在消息队列中
                                    @Override
                                    public void run() {
                                        scrollView.fullScroll(View.FOCUS_DOWN);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    animator.setDuration(200);
                    animator.start();
                }
            }
        });
    }

    /**
     * 获取7行TextView的高度
     */
    private int getTVHeight(TextView tvDetailDes, int maxLines) {
        //模拟一个TextView，设置最大行数为7行，计算该虚拟TextView高度，从而知道tvDetailDes在展示7行时有多高
        TextView textView = new TextView(UIUtils.getContext());
        textView.setText(data.des);//文字一致
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//文字大小一致
        if (maxLines < 100)
            textView.setMaxLines(maxLines);//最大行数

        int width = tvDetailDes.getMeasuredWidth();//宽度
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);//宽不变，确定值，match_parent
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);//高，包裹内容，wrap_content。包裹内容时，arg0:表示最大值

        //开始测量
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();//获取到测量后的高度
    }

    /**
     * 获取父ScrollView
     */
    private ScrollView getScrollVie(TextView tvDetailDes) {
        ViewParent parent = tvDetailDes.getParent();

        for (int i = 0; i < 20; i++) { //找20次
            parent = parent.getParent();
            if (parent instanceof ScrollView)
                return (ScrollView) parent;
        }
        return null;
    }


    private int mCurrentState;
    private float mProgress;//进度，例：20%
    private FrameLayout flProgress;
    private Button btnDownload;
    private ProgressHorizontal pbProgress;

    private View initAppDownload() {
        View view = UIUtils.inflate(R.layout.item_app_detail_download);

        //初始化自定义进度条
        btnDownload = (Button) view.findViewById(R.id.btn_download);
        flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
        pbProgress = new ProgressHorizontal(UIUtils.getContext());
        pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);//背景图片
        pbProgress.setProgressResource(R.drawable.progress_normal);//进度条图片
        pbProgress.setProgressTextColor(Color.WHITE);//进度文字颜色
        pbProgress.setProgressTextSize(UIUtils.dip2px(18));//进度文字大小
        //填充flProgress
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        flProgress.addView(pbProgress, params);

        //下载管理
        final DownloadManager mDM = DownloadManager.getInstance();
        //注册观察者，监听状态、进度变化
        mDM.registerObserver(new DownloadManager.DownloadObserver() {
            //状态更新，子线程or主线程
            @Override
            public void onDownloadStateChanged(DownloadInfo info) {
                refreshUIOnMainThread(info);
            }
            //进度更新，子线程
            @Override
            public void onDownloadProgressChanged(DownloadInfo info) {
                refreshUIOnMainThread(info);
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep(mDM);
            }
        });
        flProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep(mDM);
            }
        });

        //判断当前应用是否下载过
        DownloadInfo downloadInfo = mDM.getDownloadInfo(data);

        if (downloadInfo != null) { //之前下载过
            mCurrentState = downloadInfo.currentState;
            mProgress = downloadInfo.getProgress();
        } else {    //没下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mProgress = 0;
        }
        refreshUI(mCurrentState, mProgress);

        return view;
    }

    /**
     * 根据当前下载的状态、进度更新界面
     */
    private void refreshUI(int currentState, float progress) {
        mCurrentState = currentState;
        mProgress = progress;

        switch (currentState) {
            case DownloadManager.STATE_UNDO:    //未下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载");
                break;
            case DownloadManager.STATE_WAITING: //等待中
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("等待中...");
                break;
            case DownloadManager.STATE_DOWNLOADING: //正在下载
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("");
                pbProgress.setProgress(mProgress);
                break;
            case DownloadManager.STATE_PAUSE:   //暂停
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("暂停");
                pbProgress.setProgress(mProgress);
                break;
            case DownloadManager.STATE_ERROR:   //错误
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS: //下载成功
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("安装");
                break;
        }
    }

    /**
     * 主线程更新UI
     */
    private void refreshUIOnMainThread(final DownloadInfo info) {
        //判断传过来的info对象是否是当前的应用
        if (data.id.equals(info.id)) {
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(info.currentState, info.getProgress());
//                    System.out.println("更新：" + info.currentState + "; " + info.getProgress());
                }
            });
        }
    }

    //根据当前状态来决定下一步操作
    private void nextStep(DownloadManager mDM) {
        if (mCurrentState == DownloadManager.STATE_UNDO || mCurrentState == DownloadManager.STATE_ERROR || mCurrentState == DownloadManager.STATE_PAUSE) {
            mDM.download(data);
        } else if (mCurrentState == DownloadManager.STATE_DOWNLOADING || mCurrentState == DownloadManager.STATE_WAITING) {
            mDM.pause(data);
        } else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
            mDM.install(data);
        }
    }

}
