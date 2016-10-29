package com.skey.mygoogleplay.ui.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.domain.CategoryInfo;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.http.protocol.CategoryProtocol;
import com.skey.mygoogleplay.ui.adapter.MyBaseAdapter;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.MyListView;
import com.skey.mygoogleplay.utils.BitmapHelper;
import com.skey.mygoogleplay.utils.StringUtils;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 分类Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class CategoryFragment extends BaseFragment {

    private ArrayList<CategoryInfo> data;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new CategoryAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        CategoryProtocol protocol = new CategoryProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class CategoryAdapter extends MyBaseAdapter<CategoryInfo> {

        private final BitmapUtils mBitmapUtils;

        CategoryAdapter(ArrayList<CategoryInfo> data) {
            super(data);
            mBitmapUtils = BitmapHelper.getBitmapUtils();
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;//在原来基础上，再加一种类型
        }

        @Override
        protected int getInnerType(int position) {
            //判断是标题类型or普通类型
            CategoryInfo info = data.get(position);
            if (info.isTitle) {
                return super.getInnerType(position) + 1;//标题类型
            } else {
                return super.getInnerType(position);
            }
        }

        @Override
        protected int hasMore() {
            return STATE_MORE_NONE;//没有更多数据，隐藏加载更多的布局
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_MORE) {
                return super.getView(position, convertView, parent);//父类实现“加载更多”的布局
            } else if (getItemViewType(position) == TYPE_NORMAL) {
                if (convertView == null) {
                    convertView = UIUtils.inflate(R.layout.item_category_list);//普通布局
                    convertView.setTag(new ViewHolder(convertView));
                }
                initializeViews(getItem(position), (ViewHolder) convertView.getTag());
                return convertView;
            } else {
                if (convertView == null) {
                    convertView = UIUtils.inflate(R.layout.item_title_list);//标题布局
                    convertView.setTag(new TitleHolder(convertView));
                }
                initializeViews(getItem(position), (TitleHolder) convertView.getTag());
                return convertView;
            }
        }

        private void initializeViews(final CategoryInfo data, ViewHolder holder) {
            holder.tvName1.setText(data.name1);
            mBitmapUtils.display(holder.ivIcon1, HttpHelper.URL + "image?name=" + data.url1);
            holder.tvName2.setText(data.name2);
            mBitmapUtils.display(holder.ivIcon2, HttpHelper.URL + "image?name=" + data.url2);
            holder.tvName3.setText(data.name3);
            if (StringUtils.isEmpty(data.url3))
                holder.ivIcon3.setImageResource(android.R.color.transparent);
            else
                mBitmapUtils.display(holder.ivIcon3, HttpHelper.URL + "image?name=" + data.url3);


            holder.llGrid1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), data.name1, Toast.LENGTH_SHORT).show();
                }
            });
            holder.llGrid2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), data.name2, Toast.LENGTH_SHORT).show();
                }
            });
            holder.llGrid3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), data.name3, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initializeViews(CategoryInfo data, TitleHolder holder) {
            holder.tvTitle.setText(data.title);
        }

        class ViewHolder {

            private final TextView tvName1, tvName2, tvName3;
            private final ImageView ivIcon1, ivIcon2, ivIcon3;
            private final LinearLayout llGrid1, llGrid2, llGrid3;

            ViewHolder(View view) {
                llGrid1 = (LinearLayout) view.findViewById(R.id.ll_grid1);
                tvName1 = (TextView) view.findViewById(R.id.tv_name1);
                ivIcon1 = (ImageView) view.findViewById(R.id.iv_icon1);

                llGrid2 = (LinearLayout) view.findViewById(R.id.ll_grid2);
                tvName2 = (TextView) view.findViewById(R.id.tv_name2);
                ivIcon2 = (ImageView) view.findViewById(R.id.iv_icon2);

                llGrid3 = (LinearLayout) view.findViewById(R.id.ll_grid3);
                tvName3 = (TextView) view.findViewById(R.id.tv_name3);
                ivIcon3 = (ImageView) view.findViewById(R.id.iv_icon3);
            }
        }

        class TitleHolder {

            private final TextView tvTitle;

            TitleHolder(View view) {
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
            }
        }

        @Override
        public ArrayList<CategoryInfo> onLoadMore() {
            return null;
        }
    }
}
