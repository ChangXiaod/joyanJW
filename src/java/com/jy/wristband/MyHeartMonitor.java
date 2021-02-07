
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.wristband;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jy.dao.BandDao;
import com.jy.utility.CONSTANTS;
import com.jy.utility.Util;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：MyHeartMonitor 类功能：实现心率监控 请求访问路径："/MyHeartMonitor"
 */
@WebServlet(name = "MyHeartMonitor", urlPatterns = {"/MyHeartMonitor"})
public class MyHeartMonitor extends HttpServlet {

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：processRequest 方法功能：实现数据接收 入参; HttpServletRequest
     * request:请求参数 HttpServletResponse response：返回参数 出参：无
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            //设置返回参数
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("ok");
            //读取请求数据
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
            System.out.println("[心率监控]" + bodyStr);
            //判断非空
            if (bodyStr != null && !bodyStr.equals("")) {

                try {
                    //解析数据
                    JSONObject resultJson = JSONObject.parseObject(bodyStr);
                    //获取结果集
                    String resultAll = resultJson.getString("result");
                    //打包json数据
                    JSONObject resultOne = JSONObject.parseObject(resultAll);
                    try {
                        //找到Mac数据的睡眠数据
                        String deviceMac = resultOne.getString("deviceMac");
                        findHeartMonitor(deviceMac, resultOne);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("[心率监控]" + "请求体文本流为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：findHeartMonitor 方法功能：实现数据解析 入参; String
     * MacAddress手环MAC地址 JSONObject resultData json数据结果集 出参：无
     */
    private void findHeartMonitor(String MacAddress, JSONObject resultData) throws SQLException {
        //找到Mac数据的睡眠数据
        BandDao bd = new BandDao();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制

        //手环名称
        String baseName = resultData.getString("baseName");
        //手环地址
        String deviceMac = resultData.getString("deviceMac");
        //路由器地址
        String hubMac = resultData.getString("hubMac");
        //采集时间
        Long timeCut = resultData.getLong("timestamp");
        Date timestamp = new Date(timeCut);
        JSONObject data = (JSONObject) resultData.get("data");
        //心率
        Integer heartrate = Integer.parseInt(data.getString("heartrate"));
        long heartTimestamp = data.getLong("timestamp");

        //转换为所需日期格式
        String dateString = secondToDate(heartTimestamp, "yyyy-MM-dd hh:mm:ss");
        /**
         * 判断心率存在范围60-100为正常范围
         */
        if (heartrate >= 60 & heartrate <= 100) {

            try {
                /**
                 * 查询心率 查询房间号
                 */
                JSONObject getUserNameAndRoomID = bd.searchBandMacAndRoomId(deviceMac);
                //获取最新的手环绑定信息
                JSONArray UserNameAndRoomID = (JSONArray) getUserNameAndRoomID.get("data");
                if (UserNameAndRoomID.isEmpty()) {
                    System.out.println("找不到绑定信息！");
                    return;
                }
                JSONObject UserNameRoomID = (JSONObject) UserNameAndRoomID.get(0);
                String roomID = UserNameRoomID.getString("room_number");
                String userName = UserNameRoomID.getString("userName");
                String sex = UserNameRoomID.getString("sex");
                int num = bd.addHeartMonitor(baseName, deviceMac, hubMac, timestamp.toLocaleString(), heartrate, dateString, roomID, userName, sex);
                if (num > 0) {
                    System.out.println("恭喜您，心率监控存储成功！");
                } else {
                    System.out.println("对不起，心率监控存储失败！");
                }
            } catch (Exception e) {
                System.out.println("对不起找不到手环绑定者！");
            }

        } else /**
         * 如果心率在255判断为未佩戴以及时间为某一时刻时报警！
         */
        if (heartrate == 255) {
            try {
                /**
                 * 查询心率 查询房间号
                 */
                JSONObject getUserNameAndRoomID = bd.searchBandMacAndRoomId(deviceMac);
                //获取最新的手环绑定信息
                JSONArray UserNameAndRoomID = (JSONArray) getUserNameAndRoomID.get("data");
                if (UserNameAndRoomID.isEmpty()) {
                    System.out.println("找不到绑定信息！");
                    return;
                }
                JSONObject UserNameRoomID = (JSONObject) UserNameAndRoomID.get(0);
                String roomID = UserNameRoomID.getString("room_number");
                String userName = UserNameRoomID.getString("userName");
                String sex = UserNameRoomID.getString("sex");
                Long receive_time = simpleDateFormat.parse(UserNameRoomID.getString("receive_time").toString()).getTime();

                //查到领用表的领用时间,获取当前时间两者相差10分钟进入报警!!!
                if ((System.currentTimeMillis() - receive_time) >= 600000) {

                    //获取最新的历史时间
                    JSONObject lastTime = bd.searchSleepMonitor(deviceMac);
                    JSONArray dataTime = (JSONArray) lastTime.get("data");
                    if (dataTime.isEmpty()) {
                        int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, timestamp.toLocaleString(), "手环状态报警", "未佩戴手环/手环佩戴不正常", roomID, userName, 10);
                        if (num > 0) {
                            System.out.println("恭喜您，未佩戴手环监控存储成功！");
                        } else {
                            System.out.println("对不起，心率监控存储失败！");
                        }
                        //震动提醒
                        JSONArray macz = new JSONArray();
                        macz.add(deviceMac);
                        doWaringSendMsg(macz, deviceMac, timestamp.toLocaleString(), 20);
                        return;
                    }
                    JSONObject lastTimeData = (JSONObject) dataTime.get(0);
                    //转换为毫秒数
                    Long time = simpleDateFormat.parse(lastTimeData.get("update_time").toString()).getTime();
                    int waringNumber = Integer.parseInt(lastTimeData.get("waring_number").toString());
                    if (timeCut - time > 600000 && waringNumber == 10) {
                        System.out.println("【测试获取结果上次报警已经已经10分钟！-------】");
                        int nums = bd.addSleepMonitor(baseName, deviceMac, hubMac, timestamp.toLocaleString(), "手环状态报警", "未佩戴手环/手环佩戴不正常", roomID, userName, 10);
                        if (nums > 0) {
                            System.out.println("恭喜您，未佩戴手环监控存储成功！");
                        } else {
                            System.out.println("对不起，心率监控存储失败！");
                        }
                        //震动提醒
                        JSONArray macz = new JSONArray();
                        macz.add(deviceMac);
                        doWaringSendMsg(macz, deviceMac, timestamp.toLocaleString(), 20);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            /**
             * 查询心率 查询房间号
             */
            JSONObject getUserNameAndRoomID = bd.searchBandMacAndRoomId(deviceMac);
            //获取最新的手环绑定信息
            JSONArray UserNameAndRoomID = (JSONArray) getUserNameAndRoomID.get("data");
            if (UserNameAndRoomID.isEmpty()) {
                System.out.println("找不到绑定信息！");
                return;
            }
            JSONObject UserNameRoomID = (JSONObject) UserNameAndRoomID.get(0);
            String roomID = UserNameRoomID.getString("room_number");
            String userName = UserNameRoomID.getString("userName");
            String sex = UserNameRoomID.getString("sex");
            int num = bd.addHeartMonitor(baseName, deviceMac, hubMac, timestamp.toLocaleString(), heartrate, dateString, roomID, userName, sex);
            if (num > 0) {
                System.out.println("恭喜您，心率异常监控存储成功！");
            } else {
                System.out.println("对不起，心率监控存储失败！");
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

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：doWaringSendMsg 方法功能：实现数据接收 入参; JSONArray macz
     * json地址集合 出参：无
     */
    public void doWaringSendMsg(JSONArray macz, String band_mac, String send_time, Integer send_id) {
        //发送震动提醒
        JSONObject json = new JSONObject();
        //设置请求参数
        json.put("type", "sleepMonitor");
        json.put("timeout", 1);
        json.put("requireRes", "true");
        json.put("responseUrl", CONSTANTS.BLE_RES_URL);
        //数据体
        JSONArray data = new JSONArray();
        //消息体
        JSONObject msg = new JSONObject();
        JSONObject msgs = new JSONObject();
        msg.put("title", "注意！");
        msg.put("content", "注意，请正确佩戴手环！");
        msg.put("shakeType", "2");
        msgs.put("msg", msg);
        msgs.put("deviceMac", macz);
        data.add(msgs);
        json.put("data", data);
        //发送蓝牙震动
        Util.HttpPostWithJson(CONSTANTS.BLE_MSG_URL, json.toString(), band_mac, send_time, send_id);
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
