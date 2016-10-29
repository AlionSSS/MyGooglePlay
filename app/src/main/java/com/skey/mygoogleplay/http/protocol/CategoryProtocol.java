package com.skey.mygoogleplay.http.protocol;

import com.skey.mygoogleplay.domain.CategoryInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 分类 页面网络数据解析
 *
 * @author ALion on 2016/10/21 15:48
 */

public class CategoryProtocol extends BaseProtocol<ArrayList<CategoryInfo>> {

    @Override
    public String getKey() {
        return "category";
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public ArrayList<CategoryInfo> parseData(String result) {
        try {
            JSONArray ja = new JSONArray(result);

            ArrayList<CategoryInfo> list = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                //初始化标题对象
                if (jo.has("title")) {  //判断是否有title这个字段
                    CategoryInfo titleInfo = new CategoryInfo();
                    titleInfo.title = jo.getString("title");
                    titleInfo.isTitle = true;

                    list.add(titleInfo);
                }

                //初始化分类对象
                if (jo.has("infos")) {
                    JSONArray jaInfo = jo.getJSONArray("infos");

                    for (int j = 0; j < jaInfo.length(); j++) {
                        JSONObject joInfo = jaInfo.getJSONObject(j);
                        CategoryInfo info = new CategoryInfo();
                        info.name1 = joInfo.getString("name1");
                        info.name2 = joInfo.getString("name2");
                        info.name3 = joInfo.getString("name3");
                        info.url1 = joInfo.getString("url1");
                        info.url2 = joInfo.getString("url2");
                        info.url3 = joInfo.getString("url3");
                        info.isTitle = false;

                        list.add(info);
                    }
                }
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
