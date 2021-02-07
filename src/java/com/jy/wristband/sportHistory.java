/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.wristband;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jy.dao.BandDao;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：sportHistory 类功能：实现运动历史监控 请求访问路径："/sportHistory"
 */
@WebServlet(name = "sportHistory", urlPatterns = {"/sportHistory"})
public class sportHistory extends HttpServlet {

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
        //设置接受数据格式
        StringBuilder body = new StringBuilder(new String(Arrays.copyOf(
                result, cLen), "UTF-8"));

        String bodyStr = body.toString();
        if (bodyStr != null && !bodyStr.equals("")) {
            System.out.println("[历史运动]" + bodyStr);
            //解析数据
            JSONObject time = JSONObject.parseObject(bodyStr);
            //当前采集时间当前时间戳
            String timeNow = time.getString("timestamp");
            String resultData = time.getString("result");
            //找到结果集Mac数据
            try {
                //找到Mac数据的运动数据
                JSONObject resultAll = JSONObject.parseObject(resultData);
                //遍历json对象，自动抽取没有手环的采集数据
                if (resultAll != null) {
                    Set keys = resultAll.keySet();
                    Iterator itr = keys.iterator();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        JSONObject node = resultAll.getJSONObject(key);
                        findSportData(node, key, timeNow);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }

        } else {
            System.out.println("[历史运动]" + "请求体文本流为空");
        }
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：findSportData 方法功能：实现数据解析 入参; String
     * MacAddress手环MAC地址 JSONObject sleepData json数据结果集 String timeNow 当前时间 出参：无
     */
    private void findSportData(JSONObject node, String MacAddress, String timeNow) {
        JSONArray sleepDataTime = node.getJSONArray("data");
        for (int i = 0; i < sleepDataTime.size(); i++) {
            JSONObject jsonObject = sleepDataTime.getJSONObject(i);
            //设置监测时间
            long heartTimestamp = jsonObject.getLong("timestamp");
            //转换为所需日期格式
            String dateString = secondToDate(heartTimestamp, "yyyy-MM-dd hh:mm:ss");
            Integer calory = 0;
            try {
                //设置卡路里
                calory = jsonObject.getIntValue("calory");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Integer step = 0;
            try {
                //读取步数
                step = jsonObject.getIntValue("step");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Integer distance = 0;
            try {
                distance = jsonObject.getIntValue("distance");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Integer heartrat = 0;
            try {
                //读取心率
                heartrat = jsonObject.getIntValue("activeHeartRate");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Integer bloodOxygen = 0;
            try {
                //读取血氧
                bloodOxygen = jsonObject.getIntValue("bloodOxygen");
            } catch (Exception e) {
                e.printStackTrace();
            }
            BandDao bd = new BandDao();
            if (step > 0 | distance > 0 | calory > 0 | bloodOxygen > 0) {
                int num = bd.addSportData(step, dateString, distance, calory, heartrat, MacAddress, bloodOxygen);
                if (num > 0) {
                    System.out.println("恭喜您," + MacAddress + "运动存储成功!");
                } else {
                    System.out.println("对不起," + MacAddress + "运动存储失败!");
                }
                if (bloodOxygen > 0 & bloodOxygen < 90) {
                    /**
                     * 查询心率 查询房间号
                     */
                    JSONObject getUserNameAndRoomID = bd.searchBandMacAndRoomId(MacAddress);
                    //获取最新的手环绑定信息
                    JSONArray UserNameAndRoomID = (JSONArray) getUserNameAndRoomID.get("data");
                    JSONObject UserNameRoomID = (JSONObject) UserNameAndRoomID.get(0);
                    String roomID = UserNameRoomID.getString("room_number");
                    String userName = UserNameRoomID.getString("userName");
                    String sex = UserNameRoomID.getString("sex");
                    int nums = bd.addSleepMonitor("B4P", MacAddress, "自己查", dateString, "血氧报警", "血样检测结果不正常", roomID, userName, 0);
                    if (nums > 0) {
                        System.out.println("恭喜您,血氧不正常存储成功!");
                    } else {
                        System.out.println("对不起,血氧不正常存储失败!");
                    }
                }
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
        Date date = calendar.getTime();
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
