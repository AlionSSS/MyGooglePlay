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
import com.skey.mygoogleplay.domain.SubjectInfo;
import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.http.protocol.SubjectProtocol;
import com.skey.mygoogleplay.ui.adapter.MyBaseAdapter;
import com.skey.mygoogleplay.ui.view.LoadingPage;
import com.skey.mygoogleplay.ui.view.MyListView;
import com.skey.mygoogleplay.utils.BitmapHelper;
import com.skey.mygoogleplay.utils.UIUtils;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;

/**
 * 专题Fragment
 *
 * @author ALion on 2016/10/13 22:55
 */
public class SubjectFragment extends BaseFragment {

    private ArrayList<SubjectInfo> data;
    private SubjectProtocol protocol;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new SubjectAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        protocol = new SubjectProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class SubjectAdapter extends MyBaseAdapter<SubjectInfo> {

        private BitmapUtils mBitmapUtils;

        SubjectAdapter(ArrayList<SubjectInfo> data) {
            super(data);
            mBitmapUtils = BitmapHelper.getBitmapUtils();
            mBitmapUtils.configDefaultLoadingImage(R.drawable.subject_default);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_MORE) {
                return super.getView(position, convertView, parent);//父类实现“加载更多”的布局
            } else {
                if (convertView == null) {
                    convertView = UIUtils.inflate(R.layout.item_subject_list);//普通布局
                    convertView.setTag(new ViewHolder(convertView));
                }
                initializeViews(getItem(position), (ViewHolder)convertView.getTag());
                return convertView;
            }
        }

        private void initializeViews(SubjectInfo data, ViewHolder holder) {
            mBitmapUtils.display(holder.ivIcon, HttpHelper.URL + "image?name=" + data.url);
            holder.tvDes.setText(data.des);
        }

        class ViewHolder {

            private final ImageView ivIcon;
            private final TextView tvDes;

            ViewHolder(View view) {
                ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                tvDes = (TextView) view.findViewById(R.id.tv_des);
            }
        }

        @Override
        public ArrayList<SubjectInfo> onLoadMore() {
            return protocol.getData(getListSize());
        }
    }
}
