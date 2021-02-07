/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.wristband;

import com.alibaba.fastjson.JSONArray;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：sleepHistory 类功能：实现睡眠历史监控 请求访问路径："/sleepHistory"
 */
@WebServlet(name = "sleepHistory", urlPatterns = {"/sleepHistory"})
public class sleepHistory extends HttpServlet {

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：processRequest 方法功能：实现数据接收 入参; HttpServletRequest
     * request:请求参数 HttpServletResponse response：返回参数 出参：无
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置返回参数
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("ok");
        //获取接收数据
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
        //设在接收数据格式
        StringBuilder body = new StringBuilder(new String(Arrays.copyOf(
                result, cLen), "UTF-8"));

        String bodyStr = body.toString();
        if (bodyStr != null && !bodyStr.equals("")) {
            System.out.println("[历史睡眠]" + bodyStr);
            //解析数据
            JSONObject time = JSONObject.parseObject(bodyStr);
            //当前采集时间当前时间戳
            Date timestamp = new Date(time.getLong("timestamp"));
            String resultData = time.getString("result");
            if (resultData != null && !resultData.equals("")) {
                //将字符串解析为json对象
                //找到结果集Mac数据
                JSONObject resultAll = JSONObject.parseObject(resultData);
                //遍历json对象，自动抽取没有手环的采集数据
                if (resultAll != null) {
                    Set keys = resultAll.keySet();
                    Iterator itr = keys.iterator();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        JSONObject node = resultAll.getJSONObject(key);
                        findSleepData(node, key, timestamp.toLocaleString());
                    }
                }

            } else {
                System.out.println("[历史睡眠]" + "请求体文本流为空");
            }
        }
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：findSleepData 方法功能：实现数据解析 入参; String
     * MacAddress手环MAC地址 JSONObject sleepData json数据结果集 String timeNow 当前时间 出参：无
     */
    private void findSleepData(JSONObject sleepData, String MacAddress, String timeNow) {
        String resultSleepData = sleepData.getString("data");
        //找到Mac数据的睡眠数据
        JSONArray sleepDataTime = JSONArray.parseArray(resultSleepData);
        for (int i = 0; i < sleepDataTime.size(); i++) {
            JSONObject jsonObject = sleepDataTime.getJSONObject(i);
            String status = jsonObject.getString("status");
            if (status.equals("lightSleep") | status.equals("deepSleep")) {
                long heartTimestamp = jsonObject.getLong("timestamp");
                //转换为所需日期格式
                String dateString = secondToDate(heartTimestamp, "yyyy-MM-dd hh:mm:ss");
                Double timespan = Double.parseDouble(jsonObject.getString("timespan"));
                BandDao bd = new BandDao();
                int num = bd.addSleepData(((status.equals("lightSleep")) ? "浅睡" : "深睡"), MacAddress, timeNow, dateString, timespan);
                System.out.println("睡眠存储成功\n");
            }
        }
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：secondToDate 方法功能：实现日期格式转化 入参; long second秒数
     * String patten日期格式字符串 出参：String 制定格式的字符串
     */
    private String secondToDate(long second, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        java.util.Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
