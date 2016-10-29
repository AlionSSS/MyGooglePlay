package com.skey.mygoogleplay.ui.fragment;

import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.skey.mygoogleplay.R;
import com.skey.mygoogleplay.domain.AppInfo;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.http.protocol.AppProtocol;
import com.skey.mygoogleplay.http.protocol.HomeProtocol;
import com.skey.mygoogleplay.ui.adapter.MyBaseAdapter;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.MyListView;
import com.skey.mygoogleplay.utils.BitmapHelper;
import com.skey.mygoogleplay.utils.UIUtils;

import java.util.ArrayList;

/**
 * 应用Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class AppFragment extends BaseFragment {

    private ArrayList<AppInfo> data;
    private AppProtocol protocol;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new AppAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        protocol = new AppProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class AppAdapter extends MyBaseAdapter<AppInfo> {
        private BitmapUtils mBitmapUtils;

        AppAdapter(ArrayList<AppInfo> data) {
            super(data);
            mBitmapUtils = BitmapHelper.getBitmapUtils();
            mBitmapUtils.configDefaultLoadingImage(R.drawable.ic_default);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_MORE) {
                return super.getView(position, convertView, parent);//父类实现“加载更多”的布局
            } else {
                if (convertView == null) {
                    convertView = UIUtils.inflate(R.layout.item_home_list);//普通布局
                    convertView.setTag(new ViewHolder(convertView));
                }
                initializeViews(getItem(position), (ViewHolder)convertView.getTag());
                return convertView;
            }
        }

        private void initializeViews(AppInfo data, ViewHolder holder) {
            mBitmapUtils.display(holder.ivIcon, HttpHelper.URL + "image?name=" + data.iconUrl);
            holder.tvName.setText(data.name);
            holder.rbStar.setRating(data.stars);
            holder.tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.size));
            holder.tvDes.setText(data.des);
        }

        class ViewHolder {

            private final TextView tvName;
            private final ImageView ivIcon;
            private final RatingBar rbStar;
            private final TextView tvSize;
            private final TextView tvDes;

            ViewHolder(View view) {
                tvName = (TextView) view.findViewById(R.id.tv_name);
                ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                rbStar = (RatingBar) view.findViewById(R.id.rb_star);
                tvSize = (TextView) view.findViewById(R.id.tv_size);
                tvDes = (TextView) view.findViewById(R.id.tv_des);
            }
        }

        //此方法在子线程
        @Override
        public ArrayList<AppInfo> onLoadMore() {
            return protocol.getData(getListSize());//下一页数据
        }
    }

}