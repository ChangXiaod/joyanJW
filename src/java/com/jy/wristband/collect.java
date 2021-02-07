/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.wristband;

import com.alibaba.fastjson.JSONObject;
import com.jy.dao.BandDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：collect 类功能：实现数据采集 请求访问路径："/collect"
 */
@WebServlet(name = "collect", urlPatterns = {"/collect"})
public class collect extends HttpServlet {

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：doGet 方法功能：实现请求转化post 入参; HttpServletRequest
     * request:请求参数 HttpServletResponse response：返回参数 出参：无
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：doPost 方法功能：实现数据接收 入参; HttpServletRequest
     * request:请求参数 HttpServletResponse response：返回参数 出参：无
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            //设置返回参数
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("ok");
            //读取输入数据
            BufferedInputStream buffer = new BufferedInputStream(request.getInputStream());
            int cLen = 0, bufLen = 0;
            byte[] result = new byte[4096];
            byte[] buf = new byte[4096];

            while ((bufLen = buffer.read(buf)) > 0) {
                if (bufLen > result.length - cLen) {
                    byte[] tmp = new byte[result.length + 4096];
                    System.arraycopy(result, 0, tmp, 0, cLen);
                    result = tmp;
                }
                System.arraycopy(buf, 0, result, cLen, bufLen);
                cLen += bufLen;
            }
            //设置数据格式
            StringBuilder body = new StringBuilder(new String(Arrays.copyOf(
                    result, cLen), "UTF-8"));

            String bodyStr = body.toString();
            if (bodyStr != null && !bodyStr.equals("")) {
                System.out.println("[采集信息]:" + bodyStr);
                //将字符串解析为json对象
                JSONObject collections = JSONObject.parseObject(bodyStr);
                //遍历json对象，自动抽取没有手环的采集数据
                if (collections != null) {
                    Set keys = collections.keySet();
                    Iterator itr = keys.iterator();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        if (key != null && !key.equalsIgnoreCase("type")) {
                            //获取手环数据
                            JSONObject node = collections.getJSONObject(key);
                            saveCollect(node);
                        } else {
                            System.out.println("[数据采集]失败！未发现手环数据");
                        }
                    }
                }
            } else {
                System.out.println("[数据采集]" + "请求体文本流为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：saveCollect 方法功能：实现数据保存 入参; JSONObject
     * bandMsg：手环参数信息 出参：无
     */
    private void saveCollect(JSONObject bandMsg) {
        //获取手环节点
        String node = bandMsg.getString("node");
        //获取强度
        Integer rssi = Integer.parseInt(bandMsg.getString("rssi"));
        //获取心率
        Integer heartrate = Integer.parseInt(bandMsg.getString("heartrate"));
        //获取卡路里
        Integer calorie = Integer.parseInt(bandMsg.getString("calorie"));
        //获取手环型号
        String model = bandMsg.getString("model");
        //获取步数
        Integer step = Integer.parseInt(bandMsg.getString("step"));
        //获取路由器节点
        String position = bandMsg.getString("position");
        //获取路由器地址
        String routerMac = bandMsg.getString("routerMac");
        //获取数据时间戳
        long timestamp = bandMsg.getLong("timestamp");
        Date date = new Date(timestamp);
        /**
         * 心率在60-100正常范围
         */
        if (heartrate >= 60 & heartrate <= 100) {
            //数据存储
            BandDao bd = new BandDao();
            int num = bd.addCollectData(rssi, heartrate, calorie, model, step, routerMac, date.toLocaleString(), node);
            if (num > 0) {
                System.out.println("恭喜您，存储手环" + node + "采集信息成功！");
            } else {
                System.out.println("对不起，存储手环" + node + "采集信息失败！");
            }
        } else {
            System.out.println("【数据采集心率不正常】" + node + "心率为" + heartrate);
        }

        String url = "http://8.131.103.186:8080/bi/education/api/sportHistory";
        String driverMac = node;
        String json = "{\n"
                + "    \"type\": \"sportHistory\",\n"
                + "    \"timeout\": 300,\n"
                + "    \"requireRes\": \"true\",\n"
                + "    \"responseUrl\": \"http://39.107.67.162:8080/jw/sportHistory\",\n"
                + "    \"deviceMac\":[\"" + driverMac + "\"]\n"
                + "}";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        StringEntity postingString;
        try {
            postingString = new StringEntity(json); // json传递
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse responses = httpClient.execute(post);
            String content = EntityUtils.toString(responses.getEntity());
            System.out.println(content);
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}
