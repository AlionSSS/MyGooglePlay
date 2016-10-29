package com.skey.mygoogleplay.ui.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.utils.UIUtils;

/**
 * 游戏Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class GameFragment extends BaseFragment {
    @Override
    public View onCreateSuccessView() {
        TextView view = new TextView(UIUtils.getContext());
//        view.setText(getClass().getSimpleName());
        view.setText("游戏页面");
        view.setTextColor(Color.BLACK);
        view.setTextSize(24);
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return LoadingPage.ResultState.STATE_SUCCESS;
    }
}
