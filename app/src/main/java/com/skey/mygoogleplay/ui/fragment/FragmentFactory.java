package com.skey.mygoogleplay.ui.fragment;

import android.util.SparseArray;

/**
 * 生产Fragment的工厂
 *
 * @author ALion on 2016/10/13 22:49
 */
public class FragmentFactory {

    //    private static HashMap<Integer, BaseFragment> mFragmentMap = new HashMap<>();
    private static SparseArray<BaseFragment> mFragmentArray = new SparseArray<>();

    public static BaseFragment createFragment(int pos) {
        BaseFragment fragment = mFragmentArray.get(pos);//先从集合中取

        if (fragment == null) {//如果集合中没有，才创建
            switch (pos) {
                case 0:     //首页
                    fragment = new HomeFragment();
                    break;
                case 1:     //应用
                    fragment = new AppFragment();
                    break;
                case 2:     //游戏
                    fragment = new GameFragment();
                    break;
                case 3:     //专题
                    fragment = new SubjectFragment();
                    break;
                case 4:     //推荐
                    fragment = new RecommendFragment();
                    break;
                case 5:     //分类
                    fragment = new CategoryFragment();
                    break;
                case 6:     //排行
                    fragment = new HotFragment();
                    break;
            }
        }

        mFragmentArray.put(pos, fragment);//将fragment保存在集合中

        return fragment;
    }
}
