/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jy.dao.BandDao;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author L
 */
public class sortClassUtils {

    public static boolean sortGroupToClass(String dateStart, String dateStop, Integer howLong, int groupID, int room_id,String room_name) {
        try {
//            ServletDataDao bd = new ServletDataDao();
            BandDao bandDao = new BandDao();
            //获取时间段，结束时间减去开始时间的秒数
            long startTime = 0L;
            long stopTime = 0L;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
            try {
                startTime = simpleDateFormat.parse(dateStart).getTime();
                stopTime = simpleDateFormat.parse(dateStop).getTime();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
            //计算时间差
            long timeLong = stopTime - startTime;
            //判断时间差是否正常
            if (timeLong <= 0) {
                return false;
            }
            //获取时间间隔,几小时一班 将前端时间间隔转换成毫秒
            long duration = howLong * 3600000;

            //分班总次数
            int numClass = (int) Math.ceil(timeLong / duration);
            //根据组号查询班次数据
            JSONArray resultClass = bandDao.searchCountOfClass(groupID);
            if (resultClass == null || resultClass.size() <= 0) {
                System.out.println("[debug] no class info.");
                return false;
            }
            int class_num = resultClass.size();
            JSONArray class_schedule_array = new JSONArray();
            long lastTime = startTime;
            for (int i = 0; i < numClass; i++) {
                Date dates = new Date();
                dates.setTime(lastTime);
                System.out.println("开始实现时间" + i + "时间" + simpleDateFormat.format(dates));
                //班次开始时间
                String timeStart = (simpleDateFormat.format(dates));

                lastTime = lastTime + duration;
                dates.setTime(lastTime);
                System.out.println("结束实现时间" + i + "时间" + simpleDateFormat.format(dates));
                //班次结束时间
                String timeStop = (simpleDateFormat.format(dates));
                //排班序号
                int order_id = i;
                int index = i % class_num;
                //获得对应的值班数据
                JSONObject class_obj = resultClass.getJSONObject(index);
                //警员信息
                JSONArray polices = new JSONArray();
                //第一个警员信息
                JSONObject obj1 = new JSONObject();
                obj1.put("id", class_obj.getIntValue("police_id1"));
                obj1.put("name", class_obj.getString("police_name1"));
                polices.add(obj1);
                //第二个警员信息
                JSONObject obj2 = new JSONObject();
                obj2.put("id", class_obj.getIntValue("police_id2"));
                obj2.put("name", class_obj.getString("police_name2"));
                polices.add(obj2);
                //构建排班对象
                JSONObject schedule = new JSONObject();
                //当班警察信息
                schedule.put("peoples", polices.toJSONString());
                //组号
                schedule.put("group_id", groupID);
                //组名,新建时间2021年1月28日10:48:47
                schedule.put("group_name", class_obj.getString("group_name"));
                //分班编号
                schedule.put("class_id", class_obj.getIntValue("id"));
                //班名,新建时间2021年1月28日10:48:47
                schedule.put("class_name", class_obj.getString("class_name"));
                //房间编号
                schedule.put("room_id", room_id);
                //房间名,新建时间2021年1月28日10:48:47
//                schedule.put("room_name", room_name);
                //班次开始时间
                schedule.put("start_time", timeStart);
                //班次结束时间
                schedule.put("end_time", timeStop);
                System.out.println("[schedule]" + schedule.toString());
                //班内序号
//                schedule.put("order_id", order_id);
                //将排班信息存放到数组中
                class_schedule_array.add(schedule);
            }
            //将排班结果保存到数据库中
            if (class_schedule_array != null && class_schedule_array.size() > 0) {
                System.out.println("[class_schedule_array]" + class_schedule_array.toJSONString());
                bandDao.addBatchSchedule(class_schedule_array);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
