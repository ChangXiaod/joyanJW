package com.jy.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Iterator;
import java.util.Set;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：BandDao 类功能：实现数据导入数据库
 */
public class BandDao extends BaseDao {

    public BandDao() {
        super();
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addSleepData 方法功能：睡眠数据插入数据库 入参; 第一个参数睡眠状态
     * 第二个手环MAC地址 第三个参数时间戳 第四个参数参数睡眠时段 第五个参数睡眠时长分钟
     */
    public int addSleepData(String status, String bandMac, String timestamp, String bandtimestamp, Double timespan) {
        String sql = "insert into tb_sleep_history(status,band_mac,update_time,band_timestamp,timespan) values(?,?,?,?,?)";
        return update(sql, status, bandMac, timestamp, bandtimestamp, timespan);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addSportData 方法功能：运动数据插入数据库 入参;第一个参数步数 第二个参数睡眠时段
     * 第三个参数距离 第四个参数卡路里 第五个参数心率 第六个参数手环地址
     */
    public int addSportData(Integer step, String timestamp, Integer distance, Integer calory, Integer heartrat, String bandMac, Integer bloodOxygen) {
        String sql = "insert into tb_sport_history(step,update_time,distance,calory,heart_rate,band_mac,bloodOxygen) values(?,?,?,?,?,?,?)";
        return update(sql, step, timestamp, distance, calory, heartrat, bandMac, bloodOxygen);

    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addCollectData 方法功能：采集数据插入数据库 入参; 第一个参数信号轻度 第二个心率
     * 第三个参数卡路里 第四个参数模型名称 第五个参数步数 第六个参数路由地址 第七个参数时间戳 第八个参数手环地址 出参：int 返回成功序号
     */
    public int addCollectData(Integer rssi, Integer heartrate, Integer calorie, String model, Integer step, String routemac, String timestamp, String bandMac) {
        String sql = "insert into tb_data_collect(rssi, heart_rate,calorie,model,step,route_mac,update_time, band) values(?,?,?,?,?,?,?,?)";
        return update(sql, rssi, heartrate, calorie, model, step, routemac, timestamp, bandMac);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addSleepMonitor 方法功能：睡眠监控数据插入数据库 入参; 第一个参数手环名称
     * 第二个手环MAC地址 第三个参数路由地址 第四个参数当前时间戳 出参：int 返回序号
     */
    public int addSleepMonitor(String baseName, String deviceMac, String hubMac, String timestamp, String warn_type, String warn_note, String roomID, String username, Integer waringNumber) {
        String sql = "insert into tb_sleep_monitor(base_name, device_mac,hub_mac,update_time,warn_type,warn_note,room_number,userName,waring_number) values(?,?,?,?,?,?,?,?,?)";
        return update(sql, baseName, deviceMac, hubMac, timestamp, warn_type, warn_note, roomID, username, waringNumber);

    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addHeartMonitor 方法功能：心率数据插入数据库 入参;第一个参数手环名称
     * 第二个手环MAC地址 第三个参数路由地址 第四个参数当前时间戳 第三个参数心率 第四个参数采集时间戳 出参：int 成功序号
     */
    public int addHeartMonitor(String baseName, String deviceMac, String hubMac, String timestamp, Integer heartrate, String hearttimestamp, String roomID, String username, String sex) {

        String sql = "insert into tb_heart_monitor(base_name, device_mac,hub_mac,update_time,heart_timestamp,heart_rate,room_number,police_name,sex) values(?,?,?,?,?,?,?,?,?)";
        return update(sql, baseName, deviceMac, hubMac, timestamp, hearttimestamp, heartrate, roomID, username, sex);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addHeartMonitor 方法功能：心率数据插入数据库 入参;第一个手环MAC地址
     * 出参：JSONObject 结果数据
     */
    public JSONObject searchSleepMonitor(String deviceMac) {
        /**
         * 需要改update_time
         */
        String sql = "SELECT update_time,waring_number FROM tb_sleep_monitor WHERE device_mac=? ORDER BY update_time DESC LIMIT 1";
        return queryObject(sql, deviceMac);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：searchBandMacAndRoomId 方法功能：查询房间号 入参;第一个手环MAC地址
     * 出参：JSONObject 结果数据
     */
    public JSONObject searchBandMacAndRoomId(String deviceMac) {
        /**
         * 需要改update_time
         */
        String sql = "SELECT room_number,userName,sex,receive_time FROM tb_return WHERE band_number=? ORDER BY receive_time DESC LIMIT 1";
        return queryObject(sql, deviceMac);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addHeartMonitor 方法功能：心率数据插入数据库 入参;第一个手环MAC地址
     * 出参：JSONObject 结果数据
     */
    public int addSendMsg(String deviceMac, String timestamp, Integer number, String return_msg) {
        String sql = "insert into tb_sendmsg(band_mac,send_time,send_id,return_msg) values(?,?,?,?)";
        return update(sql, deviceMac, timestamp, number, return_msg);
    }

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：addschedule 方法功能：插入值班数据
     */
    public int addschedule(String time, String peoples, Integer group, Integer clas, Integer room_id) {
        String sql = "insert into tb_schedule(time,peoples,group_id,class,room_id) values(?,?,?,?,?)";
        return update(sql, time, peoples, group, clas, room_id);
    }

    /**
     *
     * 方法名称：searchCountOfClass 方法功能：查询组里的所有班 入参;第一个手环MAC地址 出参：JSONObject 结果数据 *
     * 作者：田刚 时间：2021年1月3日
     */
    public JSONArray searchCountOfClass(Integer groupID) {
        /**
         * 需要改update_time
         */
        String sql = "SELECT  *  FROM tb_class WHERE group_no=?";
        return queryArray(sql, groupID);
    }

    public  JSONArray searchAlarmPeople(String policeID) {
        /**
         * 需要改update_time
         */
        String sql = "SELECT  *  FROM tb_alarm WHERE police_id=? ORDER BY update_time DESC LIMIT 0,1";
        return queryArray(sql, policeID);
    }

    /**
     *
     * 方法名称：searchCountOfClass 方法功能：查询组里的所有班 入参;第一个手环MAC地址 出参：JSONObject 结果数据 *
     * 作者：田刚 时间：2021年1月3日
     */
    public int addBatchSchedule(JSONArray data_set) {
        System.out.println("[input]" + data_set);
        String sql = "replace into  tb_schedule ";
        String field_str = "(";
        String values_str = "values ";
        if (data_set != null && data_set.size() > 0) {
            Iterator itr = null;
            for (int i = 0; i < data_set.size(); i++) {
                JSONObject data_obj = data_set.getJSONObject(i);
                //根据数据参数动态生成sql语句
                //为了使批量插入的数据与列名顺序一致，以第一行数据为准
                Set keys = data_set.getJSONObject(0).keySet();
                itr = keys.iterator();
                //迭代所有的关键字，生成field和value字符串
                values_str += "(";
                while (itr.hasNext()) {
                    String field = (String) itr.next();
//                    if (field != null && checkField(tb_name, field)) {
                    if (field != null) {
                        if (i == 0) {
                            field_str += field + ",";
                        }
                        values_str += "'" + data_obj.getString(field) + "',";
                    }
                }
                //去除字符串最后一个逗号
                if (i == 0) {
                    field_str = field_str.substring(0, field_str.length() - 1);
                    field_str += ") ";
                }
                values_str = values_str.substring(0, values_str.length() - 1);
                if (i < data_set.size() - 1) {
                    values_str += "),";
                } else {
                    values_str += ")";
                }

            }
        }

        //拼接最终的sql语句
        sql += field_str + values_str;
        System.out.println("[debug]batch:" + sql);
        //执行最终sql语句
        int num = super.update(sql);
        return num;
    }
}
