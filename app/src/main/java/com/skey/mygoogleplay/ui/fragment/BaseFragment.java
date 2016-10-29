package com.skey.mygoogleplay.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * Fragment的基类
 *
 * @author ALion on 2016/10/13 22:52
 */
public abstract class BaseFragment extends Fragment {

    private LoadingPage mLoadingPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //使用TextView显示当前类的类名
//        TextView view = new TextView(getContext());
//        view.setText(getClass().getSimpleName());
        mLoadingPage = new LoadingPage(UIUtils.getContext()) {
            @Override
            public View onCreateSuccessView() {
                //此处一定要调用BaseFragment的onCreateSuccessView()，否则栈溢出
                return BaseFragment.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return BaseFragment.this.onLoad();
            }
        };

        return mLoadingPage;
    }

    //加载成功的布局，必须由子类来实现
    public abstract View onCreateSuccessView();
    //加载网络数据，必须由子类来实现
    public abstract LoadingPage.ResultState onLoad();

    //开始加载数据
    public void loadData() {
        if (mLoadingPage != null) {
            mLoadingPage.loadData();
        }
    }

    /**
     * 对网络返回数据的合法性进行校验
     * @param object 网络返回的数据
     * @return STATE_EMPTY, STATE_SUCCESS, STATE_ERROR
     */
    protected LoadingPage.ResultState check(Object object) {
        if (object != null && object instanceof ArrayList) {
            ArrayList list = (ArrayList) object;
            return list.isEmpty() ? LoadingPage.ResultState.STATE_EMPTY : LoadingPage.ResultState.STATE_SUCCESS;
        }
        return LoadingPage.ResultState.STATE_ERROR;
    }
}
