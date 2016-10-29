package com.skey.mygoogleplay.http.protocol;

import com.skey.mygoogleplay.domain.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 首页 网络数据解析
 *
 * @author ALion on 2016/10/18 0:14
 */

public class HomeProtocol extends BaseProtocol<ArrayList<AppInfo>> {

    private ArrayList<String> pictures;

    @Override
    public String getKey() {
        return "home";
    }

    @Override
    public String getParams() {
        return "";//如果没有参数，就传“”，不要传null
    }

    @Override
    public ArrayList<AppInfo> parseData(String result) {
        //Gson, JsonObject
        //使用JsonObject解析方式：如果遇到{}，就是JsonObject；如果遇到[]，就是JsonArray
        try {
            JSONObject jo = new JSONObject(result);

            //解析应用列表数据
            JSONArray ja = jo.getJSONArray("list");
            ArrayList<AppInfo> list = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo1 = ja.getJSONObject(i);

                AppInfo info = new AppInfo();
                info.des = jo1.getString("des");
                info.downloadUrl = jo1.getString("downloadUrl");
                info.iconUrl = jo1.getString("iconUrl");
                info.id = jo1.getString("id");
                info.name = jo1.getString("name");
                info.packageName = jo1.getString("packageName");
                info.size = jo1.getLong("size");
                info.stars = (float) jo1.getDouble("stars");

                list.add(info);
            }

            //解析轮播条的图片数据
            JSONArray ja1 = jo.getJSONArray("picture");
            pictures = new ArrayList<>();
            for (int i = 0; i < ja1.length(); i++) {
                pictures.add(ja1.getString(i));
            }

            return list;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getPictureList() {
        return pictures;
    }
}
