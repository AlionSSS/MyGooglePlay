package com.skey.mygoogleplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.manager.ThreadManager;
import com.skey.mygoogleplay.utils.UIUtils;

/**
 * 根据当前状态来显示不同页面的自定义控件
 * -未加载 -加载中 -加载失败 - 数据为空 -加载成功
 *
 * @author ALion on 2016/10/14 16:55
 */
public abstract class LoadingPage extends FrameLayout {

    private static final int STATE_LOAD_UNDO = 1;   //未加载
    private static final int STATE_LOAD_LOADING = 2;//加载中
    private static final int STATE_LOAD_ERROR = 3;  //加载失败
    private static final int STATE_LOAD_EMPTY = 4;  //数据为空
    private static final int STATE_LOAD_SUCCESS = 5;//加载成功
    private int mCurrentState = STATE_LOAD_UNDO;    //当前状态

    private View mLoadingPage;
    private View mErrorPage;
    private View mEmptyPage;
    private View mSuccessPage;

    public LoadingPage(Context context) {
        super(context);
        initView();
    }


    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //初始化加载中布局
        if (mLoadingPage == null) {
            mLoadingPage = UIUtils.inflate(R.layout.page_loading);
            addView(mLoadingPage);//将加载中的布局添加个帧布局
        }

        //初始化加载失败布局
        if (mErrorPage == null) {
            mErrorPage = UIUtils.inflate(R.layout.page_error);
            //点击重试事件
            Button btnRetry = (Button) mErrorPage.findViewById(R.id.btn_retry);
            btnRetry.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData();//重新加载数据
                }
            });
            addView(mErrorPage);
        }

        //初始化数据为空布局
        if (mEmptyPage == null) {
            mEmptyPage = UIUtils.inflate(R.layout.page_empty);
            addView(mEmptyPage);
        }

        showRightPage();
    }

    /**
     * 根据当前状态，决定显示哪个布局
     */
    private void showRightPage() {
        mLoadingPage.setVisibility((mCurrentState == STATE_LOAD_UNDO || mCurrentState == STATE_LOAD_LOADING) ? VISIBLE : GONE);
        mErrorPage.setVisibility(mCurrentState == STATE_LOAD_ERROR ? VISIBLE : GONE);
        mEmptyPage.setVisibility(mCurrentState == STATE_LOAD_EMPTY ? VISIBLE : GONE);

        //初始化成功的布局
        if (mSuccessPage == null && mCurrentState == STATE_LOAD_SUCCESS) {
            mSuccessPage = onCreateSuccessView();
            if (mSuccessPage != null)
                addView(mSuccessPage);
        }
        if (mSuccessPage != null) {
            mSuccessPage.setVisibility(mCurrentState == STATE_LOAD_SUCCESS ? VISIBLE : GONE);
        }
    }

    //2.加载成功后显示的布局，必须由调用者来实现
    public abstract View onCreateSuccessView();

    /**
     * 开始加载数据
     */
    public void loadData() {
        if (mCurrentState != STATE_LOAD_LOADING) {//如果当前没有加载，就开始加载

            mCurrentState = STATE_LOAD_LOADING;

//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//
//                    final ResultState resultState = onLoad();
//                    //运行在主线程
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (resultState != null) {
//                                mCurrentState = resultState.getState();//网络加载结束后，更新网络状态
//                                //根据最新的状态，刷新页面
//                                showRightPage();
//                            }
//                        }
//                    });
//                }
//            }.start();
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final ResultState resultState = onLoad();
                    //运行在主线程
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultState != null) {
                                mCurrentState = resultState.getState();//网络加载结束后，更新网络状态
                                //根据最新的状态，刷新页面
                                showRightPage();
                            }
                        }
                    });
                }
            });
        }
    }

    //1.加载网络数据，返回值表示请求网络结束后的状态
    public abstract ResultState onLoad();

    /**
     * 枚举
     */
    public enum ResultState {
        STATE_ERROR(STATE_LOAD_ERROR), STATE_EMPTY(STATE_LOAD_EMPTY), STATE_SUCCESS(STATE_LOAD_SUCCESS);

        private int state;

        ResultState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }
}
