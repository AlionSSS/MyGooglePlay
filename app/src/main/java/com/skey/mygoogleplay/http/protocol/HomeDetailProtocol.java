package com.skey.mygoogleplay.http.protocol;

import com.skey.mygoogleplay.domain.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 应用详情页 网络数据解析
 *
 * @author ALion on 2016/10/21 22:28
 */

public class HomeDetailProtocol extends BaseProtocol<AppInfo> {

    public String packageName;

    public HomeDetailProtocol(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getKey() {
        return "detail";
    }

    @Override
    public String getParams() {
        return "&packageName=" + packageName;
    }

    @Override
    public AppInfo parseData(String result) {
        try {
            JSONObject jo = new JSONObject(result);

            //解析应用列表数据
            AppInfo info = new AppInfo();
            info.des = jo.getString("des");
            info.downloadUrl = jo.getString("downloadUrl");
            info.iconUrl = jo.getString("iconUrl");
            info.id = jo.getString("id");
            info.name = jo.getString("name");
            info.packageName = jo.getString("packageName");
            info.size = jo.getLong("size");
            info.stars = (float) jo.getDouble("stars");

            info.author = jo.getString("author");
            info.date = jo.getString("date");
            info.downloadNum = jo.getString("downloadNum");

            JSONArray jaSafe = jo.getJSONArray("safe");
            ArrayList<AppInfo.SafeInfo> safeList = new ArrayList<>();
            for (int i = 0; i < jaSafe.length(); i++) {
                JSONObject joSafe = jaSafe.getJSONObject(i);

                AppInfo.SafeInfo safeInfo = new AppInfo.SafeInfo();
                safeInfo.safeDes = joSafe.getString("safeDes");
                safeInfo.safeDesUrl = joSafe.getString("safeDesUrl");
                safeInfo.safeUrl = joSafe.getString("safeUrl");

                safeList.add(safeInfo);
            }
            info.safe = safeList;

            JSONArray jaScreen = jo.getJSONArray("screen");
            ArrayList<String> screenList = new ArrayList<>();
            for (int i = 0; i < jaScreen.length(); i++) {
                screenList.add(jaScreen.getString(i));
            }
            info.screen = screenList;

            info.version = jo.getString("version");

            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}