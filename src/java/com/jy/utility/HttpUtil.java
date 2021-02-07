/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.utility;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author root
 */
public class HttpUtil {

    private static HttpClient client = null;
    private static HttpGet get = null;
    static HttpResponse response = null;
    static HttpEntity entity = null;
    static String result = null;

    public static String SyncRemoteData(String url) {
        try {
            //设置统计时间段
            CloseableHttpClient client = HttpClients.createDefault();
            //初始化httpget
            get = new HttpGet(url);

            //执行get查询
            response = client.execute(get);
            entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
            System.out.print("[DEBUG]" + result);

        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {
            return result;
        }

    }

    /*
    * 函数名称：HttpPostWithJson
    * 功能描述：通过调用httpclient访问远端web接口
    * 输入参数：String json：数据对象字符串
    * 输出参数：String：服务器端返回的json格式字符串
    * 作者：　douzf
    * 日期：　2019-03-11
     */
    public static String HttpPostWithJson(String url, String json) {
        String returnValue = "这是默认返回值，接口调用失败";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            //第一步：创建HttpClient对象
            httpClient = HttpClients.createDefault();

            //第二步：创建httpPost对象
            HttpPost httpPost = new HttpPost(url);

            //第三步：给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json, "utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);

            //第四步：发送HttpPost请求，获取返回值
            returnValue = httpClient.execute(httpPost, responseHandler); //调接口获取返回值时，必须用此方法

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //第五步：处理返回值
        return returnValue;
    }
}
