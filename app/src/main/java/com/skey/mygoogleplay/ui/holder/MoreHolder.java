package com.skey.mygoogleplay.ui.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.utils.UIUtils;

/**
 * 更多Holder
 *
 * @author ALion on 2016/10/13 22:50
 */
public class MoreHolder extends BaseHolder<Integer> {

	// 加载更多的几种状态
	// 1. 可以加载更多
	// 2. 加载更多失败
	// 3. 没有更多数据
	public static final int STATE_MORE_MORE = 1;
	public static final int STATE_MORE_ERROR = 2;
	public static final int STATE_MORE_NONE = 3;

	private LinearLayout llLoadMore;
	private TextView tvLoadError;

	public MoreHolder(boolean hasMore) {
		// 如果有更多数据,状态为STATE_MORE_MORE,否则为STATE_MORE_NONE,将此状态传递给父类的data,
		// 父类会同时刷新界面
		setData(hasMore ? STATE_MORE_MORE : STATE_MORE_NONE);//setData结束后一定会调refreshView
	}

	@Override
	public View initView() {
		View view = UIUtils.inflate(R.layout.item_more_list);

		llLoadMore = (LinearLayout) view.findViewById(R.id.ll_load_more);
		tvLoadError = (TextView) view.findViewById(R.id.tv_load_error);

		return view;
	}

	@Override
	public void refreshView(Integer data) {
		switch (data) {
		case STATE_MORE_MORE:
			// 显示加载更多
			llLoadMore.setVisibility(View.VISIBLE);
			tvLoadError.setVisibility(View.GONE);
			break;
		case STATE_MORE_NONE:
			// 隐藏加载更多
			llLoadMore.setVisibility(View.GONE);
			tvLoadError.setVisibility(View.GONE);
			break;
		case STATE_MORE_ERROR:
			// 显示加载失败的布局
			llLoadMore.setVisibility(View.GONE);
			tvLoadError.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

}
