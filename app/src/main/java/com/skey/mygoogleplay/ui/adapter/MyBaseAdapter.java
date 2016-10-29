package com.skey.mygoogleplay.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.manager.ThreadManager;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 对BaseAdapter的封装
 *
 * @author ALion on 2016/10/15 23:00
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    protected static final int TYPE_MORE = 0;//加载更多布局
    protected static final int TYPE_NORMAL = 1;//普通布局

    protected static final int STATE_MORE_NONE = 0;//0.没有更多数据
    protected static final int STATE_MORE_MORE = 1;//1.可以加载更多
    protected static final int STATE_MORE_ERROR = 2;//2.加载更多失败
    private int STATE_MORE = hasMore();

    protected ArrayList<T> data;

    public MyBaseAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() + 1;//增加加载更多布局数量
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //返回布局类型个数
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    //返回当前位置布局类型
    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {//最后一个
            return TYPE_MORE;
        } else {
            return getInnerType(position);
        }
        //return的int必须从0开始
    }

    //子类可以重写此方法来更改返回的布局类型
    protected int getInnerType(int position) {
        return TYPE_NORMAL;
    }

    //子类可以重写，决定是否可以加载更多
    protected int hasMore() {
        return STATE_MORE_MORE;//默认有更多数据
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = UIUtils.inflate(R.layout.item_more_list);//加载更多布局
            convertView.setTag(new MoreViewHolder(convertView));
        }
        initializeView((MoreViewHolder) convertView.getTag());
        if (STATE_MORE == STATE_MORE_MORE) {
            loadMore();
        }
        return convertView;
    }

    /**
     * 加载更多的界面
     */
    private void initializeView(MoreViewHolder holder) {
        switch (STATE_MORE) {
            case STATE_MORE_MORE:
                holder.llLoadMore.setVisibility(View.VISIBLE);
                holder.tvLoadError.setVisibility(View.GONE);
                break;
            case STATE_MORE_ERROR:
                holder.llLoadMore.setVisibility(View.GONE);
                holder.tvLoadError.setVisibility(View.VISIBLE);
                break;
            case STATE_MORE_NONE:
                holder.llLoadMore.setVisibility(View.GONE);
                holder.tvLoadError.setVisibility(View.GONE);
                break;
        }
    }

    private class MoreViewHolder {

        private final LinearLayout llLoadMore;
        private final TextView tvLoadError;

        MoreViewHolder(View view) {
            llLoadMore = (LinearLayout) view.findViewById(R.id.ll_load_more);
            tvLoadError = (TextView) view.findViewById(R.id.tv_load_error);
        }
    }

    private boolean isLoadMore = false;//标记是否正在加载更多

    /**
     * 加载更多
     */
    private void loadMore() {
        if (!isLoadMore) {
            isLoadMore = true;
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//
//                    final ArrayList<T> moreData = onLoadMore();
//
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (moreData != null) {
//                                //每一页有20条数据，如果返回数据小于20条，就认为到了最后一页了
//                                if (moreData.size() < 20) {
//                                    STATE_MORE = STATE_MORE_NONE;
//                                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    STATE_MORE = STATE_MORE_MORE;
//                                }
//                                //将当前数据追加到集合中
//                                data.addAll(moreData);
//                            } else {    //加载更多失败
//                                STATE_MORE = STATE_MORE_ERROR;
////                                initializeView(holder);//刷新加载更多布局的界面
//                            }
//                            MyBaseAdapter.this.notifyDataSetChanged();//刷新界面
//                            isLoadMore = false;
//                        }
//                    });
//                }
//            }.start();
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<T> moreData = onLoadMore();

                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moreData != null) {
                                //每一页有20条数据，如果返回数据小于20条，就认为到了最后一页了
                                if (moreData.size() < 20) {
                                    STATE_MORE = STATE_MORE_NONE;
                                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                                } else {
                                    STATE_MORE = STATE_MORE_MORE;
                                }
                                //将当前数据追加到集合中
                                data.addAll(moreData);
                            } else {    //加载更多失败
                                STATE_MORE = STATE_MORE_ERROR;
//                                initializeView(holder);//刷新加载更多布局的界面
                            }
                            MyBaseAdapter.this.notifyDataSetChanged();//刷新界面
                            isLoadMore = false;
                        }
                    });
                }
            });
        }
    }

    //加载更多数据，必须由子类实现
    public abstract ArrayList<T> onLoadMore();

    /**
     * 获取当前集合的大小
     */
    protected int getListSize() {
        return data.size();
    }

}
