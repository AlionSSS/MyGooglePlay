package com.skey.mygoogleplay.http.protocol;

import com.skey.mygoogleplay.http.HttpHelper;
import com.skey.mygoogleplay.utils.IOUtils;
import com.skey.mygoogleplay.utils.StringUtils;
import com.skey.mygoogleplay.utils.UIUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 访问网络的基类
 * 【权限】: <uses-permission android:name="android.permission.INTERNET" />
 *
 * @author ALion on 2016/10/17 20:56
 */

public abstract class BaseProtocol<T> {

    public T getData(int index) {
        //先判断是否有缓存，有的话加载缓存
        String result = getCache(index);
        if (StringUtils.isEmpty(result)) {   //如果没有缓存、缓存失效
            result = getDataFromServer(index);
        }
        if (result != null) {
            return parseData(result);
        }

        return null;
    }

    /**
     * 从网络获取数据
     */
    private String getDataFromServer(int index) { //index分页
        //http://www.itheima.com/home?index=0&name=zhangsan&age=18
        HttpHelper.HttpResult httpResult = HttpHelper.get(HttpHelper.URL + getKey() +
                "?index=" + index + getParams());

        if (httpResult != null) {
            String result = httpResult.getString();
//            System.out.println("访问结果：" + result);

            //写缓存
            if (!StringUtils.isEmpty(result)) {
                setCache(index, result);
            }
            return result;
        }
        return null;
    }

    //获取网络链接的关键词，子类必须实现
    public abstract String getKey();

    //获取网络链接的参数，子类必须实现
    public abstract String getParams();

    /**
     * 写缓存
     */
    private void setCache(int index, String json) {
        //以url为文件名，以json为文件内容，保存在本地
        File cacheDir = UIUtils.getContext().getCacheDir();//本应用的缓存文件夹
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index + getParams());//生成缓存文件
        FileWriter writer = null;
        try {
            writer = new FileWriter(cacheFile);

            //缓存失效的截止时间
            long deadline = System.currentTimeMillis() + 30 * 60 * 1000;//半个小时的有效期
            writer.write(deadline + "\n");//在第一行写入缓存时间，换行

            writer.write(json);//写入json
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

    /**
     * 读缓存
     */
    private String getCache(int index) {
        File cacheDir = UIUtils.getContext().getCacheDir();//本应用的缓存文件夹
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index + getParams());//生成缓存文件

        if (cacheFile.exists()) {   //判断缓存是否存在
            //判断缓存是否超过有效期
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));
                long deadline = Long.parseLong(reader.readLine());//读取第一行
                if (System.currentTimeMillis() < deadline) {    //缓存有效
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {    //line不包含有效期，因为前面已经读取过第一行了
                        sb.append(line);
                    }
                    return sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(reader);
            }
        }
        return null;
    }

    //解析json数据，子类必须实现
    public abstract T parseData(String result);
}
