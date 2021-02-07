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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：MySleepMonitor 类功能：实现睡眠监控 请求访问路径："/MySleepMonitor"
 */
@WebServlet(name = "MySleepMonitor", urlPatterns = {"/MySleepMonitor"})
public class MySleepMonitor extends HttpServlet {

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
            //转化格式
            StringBuilder body = new StringBuilder(new String(Arrays.copyOf(
                    result, cLen), "UTF-8"));

            String bodyStr = body.toString();
            if (bodyStr != null && !bodyStr.equals("")) {
                System.out.println("[睡眠监控]" + bodyStr);
                try {
                    //解析数据
                    JSONObject resultJson = JSONObject.parseObject(bodyStr);
                    JSONObject resultObj = (JSONObject) resultJson.get("result");
                    //遍历json对象，自动抽取每个手环的睡眠
                    try {
                        if (!resultObj.getString("deviceMac").equals("") || resultObj.getString("deviceMac") != null) {
                            //找到Mac数据的手环型号
                            String baseName = resultObj.getString("baseName");
                            String deviceMac = resultObj.getString("deviceMac");
                            String hubMac = resultObj.getString("hubMac");
                            Date date = new Date(resultObj.getLong("timestamp"));
                            //找到Mac数据的睡眠数据
                            BandDao bd = new BandDao();
                            long time = 0L;
                            int waringNumber = 1;
                            String roomID = "";
                            String userName = "";
                            String sex = "";
                            try {

                                /**
                                 * 查询绑定信息查询房间号
                                 */
                                JSONObject getUserNameAndRoomID = bd.searchBandMacAndRoomId(deviceMac);
                                //获取最新的手环绑定信息
                                JSONArray UserNameAndRoomID = (JSONArray) getUserNameAndRoomID.get("data");
                                if (UserNameAndRoomID.isEmpty()) {
                                    System.out.println("【找不手环绑定者！】");
                                    return;
                                }
                                JSONObject UserNameRoomID = (JSONObject) UserNameAndRoomID.get(0);
                                roomID = UserNameRoomID.getString("room_number");
                                userName = UserNameRoomID.getString("userName");
                                sex = UserNameRoomID.getString("sex");
                                //获取最新的历史时间
                                JSONObject lastTime = bd.searchSleepMonitor(deviceMac);
                                JSONArray dataTime = (JSONArray) lastTime.get("data");
                                if (dataTime.isEmpty()) {
                                    //保存睡眠数据
                                    int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "首次睡眠报警", roomID, userName,0);
                                    if (num > 0) {
                                        System.out.println("首次睡眠存储成功！");
                                    } else {
                                        System.out.println("首次睡眠存储失败！");
                                    }
                                    //震动提醒
                                    JSONArray macz = new JSONArray();
                                    macz.add(resultObj.get("deviceMac"));
                                    doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 0);
                                    return;
                                }
                                JSONObject lastTimeData = (JSONObject) dataTime.get(0);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
                                //转换为毫秒数
                                time = simpleDateFormat.parse(lastTimeData.get("update_time").toString()).getTime();
                                waringNumber = Integer.parseInt(lastTimeData.get("waring_number").toString());

                                if (!roomID.isEmpty() & (time != 0L)) {
                                    System.out.println("【进入睡眠判断！】" + (resultObj.getLong("timestamp") - time) + "时间差");
                                    //大于3分钟进入睡眠769987
                                    if (resultObj.getLong("timestamp") - time >= 180000) {
                                        System.out.println("【进入睡眠判断！】大于3分钟进入睡眠");
                                        //保存睡眠数据
                                        int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "第一次睡眠报警", roomID, userName, 1);
                                        if (num > 0) {
                                            System.out.println("第一次睡眠存储成功！");
                                        } else {
                                            System.out.println("第一次睡眠存储失败！");
                                        }
                                        //震动提醒
                                        JSONArray macz = new JSONArray();
                                        macz.add(resultObj.get("deviceMac"));
                                        doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 1);

                                    } else if (resultObj.getLong("timestamp") - time >= 20000 & waringNumber == 1) {
                                        System.out.println("【进入睡眠判断！】大于20s进入睡眠");
                                        //保存睡眠数据
                                        int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "第二次睡眠报警", roomID, userName, 2);
                                        if (num > 0) {
                                            System.out.println("第二次睡眠存储成功！");
                                        } else {
                                            System.out.println("第二次睡眠存储失败！");
                                        }
                                        //震动提醒
                                        JSONArray macz = new JSONArray();
                                        macz.add(resultObj.get("deviceMac"));
                                        doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 2);
                                    } else if (resultObj.getLong("timestamp") - time >= 20000 & waringNumber == 2) {
                                        //保存睡眠数据
                                        System.out.println("【进入睡眠判断！】大于第二次20s进入睡眠");
                                        int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "派人员叫醒", roomID, userName, 3);
                                        if (num > 0) {
                                            System.out.println("派人员叫醒，记录睡眠存储成功！");
                                        } else {
                                            System.out.println("对不起，睡眠存储失败！");
                                        }
                                        //震动提醒
                                        JSONArray macz = new JSONArray();
                                        macz.add(resultObj.get("deviceMac"));
                                        doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 3);
                                    }

                                }

                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                //保存睡眠数据
                                int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "第一次睡眠报警", roomID, userName, 1);
                                if (num > 0) {
                                    System.out.println("首次进入睡眠睡眠存储成功！");
                                } else {
                                    System.out.println("对不起，睡眠存储失败！");
                                }
                                //震动提醒
                                JSONArray macz = new JSONArray();
                                macz.add(resultObj.get("deviceMac"));
                                doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("【多个手环同时采集到数据】");
                        Set keys = resultObj.keySet();
                        Iterator itr = keys.iterator();
                        //mac地址列表
                        while (itr.hasNext()) {
                            String key = (String) itr.next();
                            JSONObject node = resultObj.getJSONObject(key);
                            //保存睡眠数据
                            findsleepMonitor(node);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("[睡眠监测]" + "请求体文本流为空");
            }
        } catch (Exception e) {
            System.out.println("[睡眠监测]" + "访问失败！");
        }

    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：findsleepMonitor 方法功能：实现数据接收 入参; JSONObject
     * resultMacData json结果集 出参：无
     */
    private void findsleepMonitor(JSONObject resultMacData) {
        //找到Mac数据的手环型号
        String baseName = resultMacData.getString("baseName");
        String deviceMac = resultMacData.getString("deviceMac");
        String hubMac = resultMacData.getString("hubMac");
        Date date = new Date(resultMacData.getLong("timestamp"));
        //找到Mac数据的睡眠数据
        BandDao bd = new BandDao();
        long time = 0L;
        int waringNumber = 1;
        String roomID = "";
        String userName = "";
        String sex = "";
        try {
            //获取最新的历史时间
            JSONObject lastTime = bd.searchSleepMonitor(deviceMac);
            JSONArray dataTime = (JSONArray) lastTime.get("data");
            if (dataTime.isEmpty()) {
                System.out.println("首次进入睡眠");
                //保存睡眠数据
                int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "首次睡眠报警", roomID, userName, 0);
                if (num > 0) {
                    System.out.println("首次进入睡眠睡眠存储成功！");
                } else {
                    System.out.println("对不起，睡眠存储失败！");
                }
                //震动提醒
                JSONArray macz = new JSONArray();
                macz.add(resultMacData.get("deviceMac"));
                doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 0);
                return;
            }
            JSONObject lastTimeData = (JSONObject) dataTime.get(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
            //转换为毫秒数
            time = simpleDateFormat.parse(lastTimeData.get("update_time").toString()).getTime();
            waringNumber = Integer.parseInt(lastTimeData.get("waring_number").toString());

            /**
             * 查询绑定信息查询房间号
             */
            JSONObject getUserNameAndRoomID = bd.searchBandMacAndRoomId(deviceMac);
            //获取最新的手环绑定信息
            JSONArray UserNameAndRoomID = (JSONArray) getUserNameAndRoomID.get("data");
            if (UserNameAndRoomID.isEmpty()) {
                System.out.println("【找不绑定人！】");
                return;
            }
            JSONObject UserNameRoomID = (JSONObject) UserNameAndRoomID.get(0);
            roomID = UserNameRoomID.getString("room_number");
            userName = UserNameRoomID.getString("userName");
            sex = UserNameRoomID.getString("sex");

            if (!roomID.isEmpty() & (time != 0L)) {
                System.out.println("时间差" + (resultMacData.getLong("timestamp") - time));
                //大于3分钟进入睡眠
                if (resultMacData.getLong("timestamp") - time >= 180000) {
                    System.out.println("第一次时间差" + (resultMacData.getLong("timestamp") - time));
                    //保存睡眠数据
                    int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "第一次睡眠报警", roomID, userName, 1);
                    if (num > 0) {
                        System.out.println("第一次睡眠存储成功！");
                    } else {
                        System.out.println("第一次睡眠存储失败！");
                    }
                    //震动提醒
                    JSONArray macz = new JSONArray();
                    macz.add(resultMacData.get("deviceMac"));
                    doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 1);

                } else if (resultMacData.getLong("timestamp") - time >= 20000 & waringNumber == 1) {
                    System.out.println("第二次时间差" + (resultMacData.getLong("timestamp") - time));
                    //保存睡眠数据
                    int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "第二次睡眠报警", roomID, userName, 2);
                    if (num > 0) {
                        System.out.println("第二次睡眠存储成功！");
                    } else {
                        System.out.println("第二次睡眠存储失败！");
                    }
                    //震动提醒
                    JSONArray macz = new JSONArray();
                    macz.add(resultMacData.get("deviceMac"));
                    doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 2);
                } else if (resultMacData.getLong("timestamp") - time >= 20000 & waringNumber == 2) {
                    System.out.println("派人员叫醒时间差" + (resultMacData.getLong("timestamp") - time));
                    //保存睡眠数据
                    int num = bd.addSleepMonitor(baseName, deviceMac, hubMac, date.toLocaleString(), "睡眠报警", "派人员叫醒", roomID, userName, 3);
                    if (num > 0) {
                        System.out.println("派人员叫醒，记录睡眠存储成功！");
                    } else {
                        System.out.println("对不起，睡眠存储失败！");
                    }
                    //震动提醒
                    JSONArray macz = new JSONArray();
                    macz.add(resultMacData.get("deviceMac"));
                    doWaringSendMsg(macz, deviceMac, date.toLocaleString(), 3);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        msg.put("title", "不要睡觉！");
        msg.put("content", "注意，请不要睡觉！");
        msg.put("shakeType", "2");
        msgs.put("msg", msg);
        msgs.put("deviceMac", macz);
        data.add(msgs);
        json.put("data", data);
        //发送蓝牙震动
        Util.HttpPostWithJson(CONSTANTS.BLE_MSG_URL, json.toString(), band_mac, send_time, send_id);
    }

}
