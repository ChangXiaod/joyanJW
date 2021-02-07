/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class ServletDataDao extends BaseDao {

    private static final String QUERY_TB_SQL = "SELECT TB_NAME, SQL_STR FROM tb_sid_map WHERE SID = ?";
    private static final String ADD_DATA_PREFIX = "INSERT INTO ";
    private static final String REPLACE_DATA_PREFIX = "REPLACE INTO ";
    private static final String UPDATE_DATA_PREFIX = "UPDATE ";
    private static final String DELETE_DATA_PREFIX = "DELETE FROM ";
    private static final String SELECT_DATA_PREFIX = "SELECT * FROM ";
    private static final String COUNT_DATA_PREFIX = "SELECT COUNT(1) AS TOTAL FROM ";

    public ServletDataDao() {
        super();
    }

    /*
     *函数名称：addData
     *函数功能：向数据表添加数据
     *输入参数：String sid, JSONObject data_obj
     *输出参数：int
     */
    public int addLog(int user_id, String user_name, String real_name, String operation, String log, String sid, boolean isUpdate) {
        //插入前先判断是否存在tb_log_today表
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String currentDay = sdf.format(new Date());
//        //判断是否存在对应的日表，不存在则建立表
//        String createSql = "create table if not exists tb_log_" + currentDay +
//             "( `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录编号', " +
//            " `user_id` int(10) unsigned DEFAULT NULL COMMENT '用户编号', "+
//            " `user_name` varchar(100) DEFAULT NULL COMMENT '用户名'," +
//            "  `real_name` varchar(100) DEFAULT NULL COMMENT '真实姓名'," +
//            "  `operation` varchar(100) DEFAULT NULL COMMENT '操作：增删改'," +
//            "  `sid` varchar(40) DEFAULT NULL COMMENT 'web服务编号'," +
//            "  `updateStatus` int(10) unsigned NOT NULL COMMENT '是否更新操作', " +
//            "  `log` text COMMENT '执行的sql语句'," +
//            "  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '执行时间'," +
//            "  PRIMARY KEY (`id`)" +
//            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
//        
//        int num = super.insert(createSql);
        int updateStatus;
        if (isUpdate == true) {
            updateStatus = 1;
            String insert_sql = "insert into tb_log " + "(user_id, user_name, real_name, operation, log, sid, updateStatus, time) values (?, ?, ?, ?, ?, ?, ?, now())";
            return super.insert(insert_sql, user_id, user_name, real_name, operation, log, sid, updateStatus);

        } else {
            updateStatus = 0;
            String insert_sql = "insert into tb_log " + "(user_id, user_name, real_name, operation, sid, updateStatus, time) values (?, ?, ?, ?, ?, ?, now())";
            return super.insert(insert_sql, user_id, user_name, real_name, operation, sid, updateStatus);

        }
//        String insert_sql = "insert into tb_log_"+ currentDay + "(user_id, user_name, real_name, operation, log, sid, updateStatus, time) values (?, ?, ?, ?, ?, ?, ?, now())";

    }

    /*
     *函数名称：addData
     *函数功能：向数据表添加数据
     *输入参数：String sid, JSONObject data_obj
     *输出参数：int
     */
    public int addData(String sid, JSONObject data_obj) {
        String tb_name = queryTbName(sid);
        String sql = ADD_DATA_PREFIX + tb_name + " ";
        String field_str = "(";
        String values_str = "values (";
        //根据数据参数动态生成sql语句
        Set keys = data_obj.keySet();
        Iterator itr = keys.iterator();
        //迭代所有的关键字，生成field和value字符串
        while (itr.hasNext()) {
            String field = (String) itr.next();
            if (field != null && checkField(tb_name, field)) {
                //处理所有的单引号和双引号
                String mdata = data_obj.getString(field);
                if (mdata != null && !mdata.equals("")) {
                    //添加field列表
                    field_str += field + ",";
                    //添加value列表
                    mdata = mdata.replace("'", "\\'");
                    values_str += "'" + mdata + "',";
                }
            }
        }
        //去除字符串最后一个逗号
        field_str = field_str.substring(0, field_str.length() - 1);
        field_str += ") ";
        values_str = values_str.substring(0, values_str.length() - 1);
        values_str += ")";
        //拼接最终的sql语句
        sql += field_str + values_str;
        System.out.println("[debug]" + sql);
        //执行最终sql语句
        int num = super.insert(sql);
        return num;
    }

    /*
     *函数名称：replaceData
     *函数功能：向数据表更新数据
     *输入参数：String sid, JSONObject data_obj
     *输出参数：int
     */
    public int replaceData(String sid, JSONObject data_obj) {
        String tb_name = queryTbName(sid);
        String sql = REPLACE_DATA_PREFIX + tb_name + " ";
        String field_str = "(";
        String values_str = "values (";
        //根据数据参数动态生成sql语句
        Set keys = data_obj.keySet();
        Iterator itr = keys.iterator();
        //迭代所有的关键字，生成field和value字符串
        while (itr.hasNext()) {
            String field = (String) itr.next();
            if (field != null && checkField(tb_name, field)) {
                field_str += field + ",";
                values_str += "'" + data_obj.getString(field) + "',";
            }
        }
        //去除字符串最后一个逗号
        field_str = field_str.substring(0, field_str.length() - 1);
        field_str += ") ";
        values_str = values_str.substring(0, values_str.length() - 1);
        values_str += ")";
        //拼接最终的sql语句
        sql += field_str + values_str;
        System.out.println("[debug]" + sql);
        //执行最终sql语句
        int num = super.update(sql);
        return num;
    }

    /*
     *函数名称：addBatchData
     *函数功能：向数据表批量添加数据
     *输入参数：String sid, JSONArray data_set
     *输出参数：int
     */
    public int addBatchData(String sid, JSONArray data_set) {
        String tb_name = queryTbName(sid);
        String sql = REPLACE_DATA_PREFIX + tb_name + " ";
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

    /*
     *函数名称：updateBatchData
     *函数功能：向数据库表批量更新数据，如果数据库表中存在相同条件的数据，
     *则执行update操作，如果不存在符合条件的记录，则执行insert操作
     *输入参数：String sid：服务编号, JSONObject data_set：需要更新的数据,JSONArray exclude_columns:不更新字段
     *输出参数：int：更新记录条数
     *作者：douzf
     *时间：2020-08-21
     */
    public int updateBatchData(String sid, JSONArray data_set, JSONArray exclude_columns) {
        int total = 0;
        //输入参数检查
        if (sid == null || data_set == null || data_set.size() == 0) {
            return -1;
        }
        String key_sql = "SELECT COLUMN_NAME  FROM information_schema.`COLUMNS` "
                + "WHERE column_key = 'PRI' AND table_schema = 'db_jw' AND TABLE_NAME = ?";
        String tb_name = queryTbName(sid);
        //先查询数据表的主键字段
        JSONArray keys = queryArray(key_sql, tb_name);
        //如果查询结果不为空并且结果集记录数大于0，表示该表设置了主键,
        //如果该表没有设置主键，直接执行insert操作
        if (keys == null || keys.size() == 0) {
            int num = addBatchData(sid, data_set);
            return num;
        }
        //逐行判断输入的数据集，如果已经有相同主键的数据，则执行更新操作，
        //如果没有，则执行插入操作
        for (int i = 0; i < data_set.size(); i++) {
            String where = "";
            String query_sql = "select *  from " + tb_name;
            //提取一行数据
            JSONObject line = data_set.getJSONObject(i);
            //逐行读取数据，生成查询及更新条件
            for (int j = 0; j < keys.size(); j++) {
                JSONObject column = keys.getJSONObject(j);
                String column_name = column.getString("COLUMN_NAME");
                where += column_name + "='" + line.getString(column_name) + "' AND ";
            }
            //删除where后面的多于的AND
            if (where.length() > 0) {
                where = where.substring(0, where.length() - 4);
                System.out.println("[debug where]" + where);
                query_sql = query_sql + " where " + where;
                System.out.println("[debug query_sql]" + query_sql);
            }
            //查询表中是否存在主键相同的数据记录
            JSONArray res = queryArray(query_sql);
            if (res != null && res.size() > 0) {
                //执行update操作
                int num = updateData(sid, line, where, exclude_columns);
                total += num;
            } else {
                //执行insert操作
                int num = addData(sid, line);
                total += num;
            }
        }
        //返回修改记录总数
        return total;
    }

    /*
     *函数名称：update_or_add
     *函数功能：向数据库表更新数据，如果数据库表中存在相同条件的数据，
     *则执行update操作，如果不存在符合条件的记录，则执行insert操作
     *输入参数：String sid：服务编号, JSONObject data_set：需要更新的数据,JSONArray exclude_columns:不更新字段
     *输出参数：int：更新记录条数
     *作者：douzf
     *时间：2020-08-21
     */
    public int update_or_add(String sid, JSONObject data_obj, JSONArray exclude_columns) {
        int total = 0;
        //输入参数检查
        if (sid == null || data_obj == null) {
            return -1;
        }
        String key_sql = "SELECT COLUMN_NAME  FROM information_schema.`COLUMNS` "
                + "WHERE column_key = 'PRI' AND table_schema = 'db_coal' AND TABLE_NAME = ?";
        String tb_name = queryTbName(sid);
        //先查询数据表的主键字段
        JSONArray keys = queryArray(key_sql, tb_name);
        //如果查询结果不为空并且结果集记录数大于0，表示该表设置了主键,
        //如果该表没有设置主键，直接执行insert操作
        if (keys == null || keys.size() == 0) {
            int num = addData(sid, data_obj);
            return num;
        }
        //逐行判断输入的数据集，如果已经有相同主键的数据，则执行更新操作，
        //如果没有，则执行插入操作
        String where = "";
        String query_sql = "select *  from " + tb_name;
        //逐行读取数据，生成查询及更新条件
        for (int j = 0; j < keys.size(); j++) {
            JSONObject column = keys.getJSONObject(j);
            String column_name = column.getString("COLUMN_NAME");
            where += column_name + "='" + data_obj.getString(column_name) + "' AND ";
        }
        //删除where后面的多于的AND
        if (where.length() > 0) {
            where = where.substring(0, where.length() - 4);
            System.out.println("[debug where]" + where);
            query_sql = query_sql + " where " + where;
            System.out.println("[debug query_sql]" + query_sql);
        }
        //查询表中是否存在主键相同的数据记录
        JSONArray res = queryArray(query_sql);
        if (res != null && res.size() > 0) {
            //执行update操作
            int num = updateData(sid, data_obj, where, exclude_columns);
            total += num;
        } else {
            //执行insert操作
            int num = addData(sid, data_obj);
            total += num;
        }
        //返回修改记录总数
        return total;
    }


    /*
     *函数名称：replaceBatchData
     *函数功能：向数据表批量更新数据
     *输入参数：String sid, JSONArray data_set
     *输出参数：int
     */
    public int replaceBatchData(String sid, JSONArray data_set) {
        String tb_name = queryTbName(sid);
        String sql = REPLACE_DATA_PREFIX + tb_name + " ";
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
                    if (field != null && checkField(tb_name, field)) {
                        if (i == 0) {
                            field_str += field + ",";
                        }
                        values_str += "'" + data_obj.getString(field) + "',";
                    } else {
                        continue;
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

    /*
     *函数名称：checkField
     *函数功能：判断指定字段是否在表中存在
     *输入参数：String tab_name, String field
     *输出参数：boolean:true表示该字段存在，false表示不存在
     */
    private boolean checkField(String tab_name, String field) {
        String sql = "SELECT COLUMN_NAME FROM information_schema.columns WHERE  table_schema=database() AND table_name = ? AND column_name = ?";
        JSONArray array = super.queryArray(sql, tab_name, field);
        //判断是否存在指定的列
        System.out.println("[debug]tab_name=" + tab_name + " field=" + field + " " + array.toJSONString());
        return array != null && array.size() > 0;
    }

    /*
     *函数名称：updateData
     *函数功能：修改符合条件的数据
     *输入参数：String sid, JSONObject data_obj, JSONObject where_obj
     *输出参数：int
     */
    public int updateData(String sid, JSONObject data_obj, String where, JSONArray exclude_columns) {
        String tb_name = queryTbName(sid);
        String sql = UPDATE_DATA_PREFIX + tb_name + " SET ";
        //根据数据参数动态生成sql语句
        Set keys = data_obj.keySet();
        Iterator itr = keys.iterator();
        //迭代所有的关键字，生成field和value字符串
        while (itr.hasNext()) {
            String field = (String) itr.next();
            //判断该字段是否在数据库中存在  
            if (field != null && checkField(tb_name, field)) {
                //判断该字段是否为排除字段
                boolean ifexclude = false;
                if (exclude_columns != null && exclude_columns.size() > 0) {
                    //将当前字段与排除字段数组中的元素一一比较，如果有相同的，
                    //则放弃更新这个字段
                    for (int i = 0; i < exclude_columns.size(); i++) {
                        String exclude_column = exclude_columns.getString(i);
                        System.out.println(exclude_column + "-" + field);
                        if (field.equalsIgnoreCase(exclude_column)) {
                            ifexclude = true;
                            continue;
                        }
                    }
                }
                //经判断，该字段属于排除字段，就不执行下面的代码，进入下一个字段的处理
                if (ifexclude == true) {
                    continue;
                }

                //处理所有的单引号和双引号
                String mdata = data_obj.getString(field);
                if (mdata != null) {
                    mdata = mdata.replace("'", "\\'");
                }
                //设置更新字段的值
                sql += field + " = '" + mdata + "',";

            } else {
                continue;
            }
        }
        //去除字符串最后一个逗号
        sql = sql.substring(0, sql.length() - 1);
        //追加条件部分
        if (where != null && where.length() > 0) {
            sql += " where " + where;
        }
        //去除字符串最后一个对于的and
//        sql = sql.substring(0, sql.length() - 4);
        //拼接最终的sql语句
        System.out.println("[debug update sql]" + sql);
        //执行最终sql语句
        int num = super.update(sql);
        return num;
    }

    /*
     *函数名称：updateData
     *函数功能：修改符合条件的数据
     *输入参数：String sid, String... args
     *输出参数：int
     */
    public int updateData(String sid, String... variables) {
        String sql = querySQL(sid);
        if (sql == null) {
            return 0;
        }

        //拼接最终的sql语句
        return super.update(sql, variables);
    }

    /*
     *函数名称：deleteData
     *函数功能：删除符合条件的数据
     *输入参数：String sid, JSONObject where_obj
     *输出参数：int
     */
    public int deleteData(String sid, String where) {
        String tb_name = queryTbName(sid);
        String sql = DELETE_DATA_PREFIX + tb_name;
        //根据数据参数动态生成sql语句
        if (where != null && where.length() > 0) {
            sql += " where " + where;
        }
        //去除字符串最后一个对于的and
//        sql = sql.substring(0, sql.length() - 4);
        //拼接最终的sql语句
        System.out.println("[debug]" + sql);
        //执行最终sql语句
        int num = super.update(sql);
        return num;
    }

    /*
     *函数名称：queryData
     *函数功能：查询符合条件的数据
     *输入参数：String sid, String where, String orderby, int page, int rows
     *输出参数：String
     */
    public JSONObject queryData(String sid, String cols, String where, String groupby, String orderby, String desc, int page, int rows) {
        String tb_name = queryTbName(sid);
        if (tb_name == null) {
            return null;
        }
        String sql = SELECT_DATA_PREFIX + tb_name;
        //如果查询字段列表不为空，则进行替换
        if (cols != null && !cols.equalsIgnoreCase("")) {
            sql = sql.replace("*", cols);
        }
        String tsql = COUNT_DATA_PREFIX + tb_name;
        //根据数据参数动态生成sql语句
        if (where != null && where.length() > 0) {
            sql += " where " + where;
            tsql += " where " + where;
        }
        //根据聚合字段生成上sql语句
        if (groupby != null && groupby.length() > 0) {
            sql += " group by " + groupby;
            tsql += " group by " + groupby;
        }
        //根据排序参数生成sql语句
        if (orderby != null && orderby.length() > 0) {
            if ("true".equalsIgnoreCase(desc)) {
                sql += " order by " + orderby + " desc";
            } else {
                sql += " order by " + orderby + " asc";
            }

        }

        //添加分页限制
        sql += " LIMIT " + (page - 1) * rows + "," + rows;
        //拼接最终的sql语句
        System.out.println("[debug]" + sql + "\n" + tsql);
        //执行最终sql语句
        JSONArray data = super.queryArray(sql);
        //查询总记录数
        JSONArray total = super.queryArray(tsql);
        int all_num = 0;
        //如果没有groupby语句，总数就是查询出来的total字段
        if (groupby == null) {
            for (int i = 0; i < total.size(); i++) {
                all_num += total.getJSONObject(i).getIntValue("TOTAL");
            }
        } else {
            //如果含有groupby子句，总数就是返回的记录条数
            all_num = total.size();
        }

        //返回查询结果
        JSONObject res = new JSONObject();
        res.put("data", data);
        res.put("total", all_num);
//        System.out.println("[query result]" + res.toString());
        return res;
    }

    /*
     *函数名称：queryDataByKey
     *函数功能：查询符合条件的数据
     *输入参数：String sid, String... varables
     *输出参数：String
     */
    public JSONObject queryDataBySql(String sid, int page, int rows, String... variables) {
        String sql = querySQL(sid);
        if (sql == null) {
            return null;
        }
        String tsql = COUNT_DATA_PREFIX + "(" + sql + ") a";
        System.out.println("[debug]tsql:" + tsql);
        //添加分页限制
        sql += " LIMIT " + (page - 1) * rows + "," + rows;
        //拼接最终的sql语句
        JSONArray data = super.queryArray(sql, variables);
        //查询总记录数
        JSONArray total = super.queryArray(tsql, variables);
        //返回查询结果
        JSONObject res = new JSONObject();
        //设置查询结果数据集，这是服务器端分页数据
        res.put("data", data);
        //设置数据总条数
        res.put("total", total.getJSONObject(0).getIntValue("TOTAL"));
        System.out.println("[query result]" + res.toString());
        return res;
    }

    /*
     *函数名称：queryDataByComplexSql
     *函数功能：查询符合条件的数据
     *输入参数：String sid, String where
     *输出参数：String
     */
    public JSONObject queryDataByComplexSql(String sid, String where) {
        String sql = querySQL(sid);
        if (sql == null) {
            return null;
        }

        //拼接最终的sql语句
        if (where != null) {
            sql = "select * from (" + sql + ") a where " + where;
            //去除可能会出现的分号，防止sql语句报错
            sql = sql.replaceAll(";", " ");
        }
        System.out.println("[complex sql]" + sql);
        JSONArray data = super.queryArray(sql);
        //返回查询结果
        JSONObject res = new JSONObject();
        res.put("data", data);
        res.put("total", data.size());
        System.out.println("[query result]" + res.toString());
        return res;
    }

    /*
     *函数名称：queryDataByKey
     *函数功能：查询符合条件的数据
     *输入参数：String sid, String cols, String key, String groupby, String orderby, String desc, int page, int rows
     *输出参数：String
     */
    public JSONObject queryDataByKey(String sid, String cols, String key, String groupby, String orderby, String desc, int page, int rows) {
        String tb_name = queryTbName(sid);
        if (tb_name == null) {
            return null;
        }
        String sql = SELECT_DATA_PREFIX + tb_name;
        //如果查询字段列表不为空，则进行替换
        if (cols != null && !cols.equalsIgnoreCase("")) {
            sql = sql.replace("*", cols);
        }
        String tsql = COUNT_DATA_PREFIX + tb_name;
        //根据数据参数动态生成sql语句
        ArrayList<String> field_list = queryColumns(tb_name);
        if (field_list != null && field_list.size() > 0) {
            String where = " where ";
            for (String field : field_list) {
                where += field + " like '%" + key + "%' or ";
            }
            //删除最后一个or
            where = where.substring(0, where.length() - 3);
            sql += where;
            tsql += where;
        }
        //根据聚合字段生成上sql语句
        if (groupby != null && groupby.length() > 0) {
            sql += " group by " + groupby;
            tsql += " group by " + groupby;
        }
        //根据排序参数生成sql语句
        if (orderby != null && orderby.length() > 0) {
            if ("true".equalsIgnoreCase(desc)) {
                sql += " order by " + orderby + " desc";
            } else {
                sql += " order by " + orderby + " asc";
            }

        }

        //添加分页限制
        sql += " LIMIT " + (page - 1) * rows + "," + rows;
        //拼接最终的sql语句
        System.out.println("[debug]" + sql + "\n" + tsql);
        //执行最终sql语句
        JSONArray data = super.queryArray(sql);
        //查询总记录数
        JSONArray total = super.queryArray(tsql);
        int all_num = 0;
        //如果没有groupby语句，总数就是查询出来的total字段
        if (groupby == null) {
            for (int i = 0; i < total.size(); i++) {
                all_num += total.getJSONObject(i).getIntValue("TOTAL");
            }
        } else {
            //如果含有groupby子句，总数就是返回的记录条数
            all_num = total.size();
        }

        //返回查询结果
        JSONObject res = new JSONObject();
        res.put("data", data);
        res.put("total", all_num);
        System.out.println("[query result]" + res.toString());
        return res;
    }

    /*
     *函数名称：queryColumns
     *函数功能：查找指定表的所有列
     *输入参数：String tb_name
     *输出参数：JSONArray
     */
    public JSONArray queryFields(String tb_name) {
        String sql = "show fields from ";
        //查询该服务对应的表格名称
        //生成查询语句
        sql += tb_name;
        //查询表的完整定义
        return super.queryArray(sql);
    }

    /*
     *函数名称：queryColumns
     *函数功能：查找指定表的所有列
     *输入参数：String tb_name
     *输出参数：ArrayList
     */
    public ArrayList queryColumns(String tb_name) {
        String sql = "show full fields from ";
        //查询该服务对应的表格名称
        //生成查询语句
        sql += tb_name;
        //查询表的完整定义
        JSONArray data = super.queryArray(sql);
        System.out.println("[commments]" + data.toJSONString());
        if (data != null && data.size() > 0) {
            //将查询结果存到数组中
            ArrayList columns = new ArrayList();
            for (int i = 0; i < data.size(); i++) {
                JSONObject obj = data.getJSONObject(i);
                //like字段只与varchar字段进行比较
                if (obj != null && obj.getString("Type").startsWith("varchar")) {
                    columns.add(obj.getString("Field"));
                }
            }
            //返回列名数组
            return columns;
        } else {
            return null;
        }
    }

    /*
     *函数名称：queryComment
     *函数功能：查找指定表的注释列
     *输入参数：String sid
     *输出参数：JSONObject
     */
    public HashMap queryComment(String sid) {
        String sql = "show full fields from ";
        //查询该服务对应的表格名称
        String tb_name = queryTbName(sid);
        if (tb_name == null) {
            return null;
        } else {
            //生成查询语句
            sql += tb_name;
        }
        //查询表的完整定义
        JSONArray data = super.queryArray(sql);
        System.out.println("[commments]" + data.toJSONString());
        if (data != null && data.size() > 0) {
            //将查询结果映射到哈希表中
            HashMap hm = new HashMap();
            for (int i = 0; i < data.size(); i++) {
                JSONObject obj = data.getJSONObject(i);
                if (obj != null) {
                    hm.put(obj.getString("Field"), obj.getString("Comment"));
                }
            }
            //返回映射表
            return hm;
        } else {
            return null;
        }
    }

    /*
     *函数名称：queryColsData
     *函数功能：查询符合条件的数据
     *输入参数：String sid, String[] cols, String where, String orderby, int page, int rows
     *输出参数：JSONObject
     */
    public JSONObject queryColsData(String sid, String cols, String where, String orderby, int page, int rows) {
        String tb_name = queryTbName(sid);
        if (tb_name == null) {
            return null;
        }
        String sql = SELECT_DATA_PREFIX + tb_name;
        String tsql = COUNT_DATA_PREFIX + tb_name;
        //添加输出字段
        if (cols != null && !"".equalsIgnoreCase(cols)) {
            sql = sql.replace("*", cols);
        }
        //根据数据参数动态生成sql语句
        if (where != null && where.length() > 0) {
            sql += " where " + where;
            tsql += " where " + where;
        }
        //根据排序参数生成sql语句
        if (orderby != null && orderby.length() > 0) {
            sql += " order by " + orderby + " desc";
        }

        //添加分页限制
        sql += " LIMIT " + (page - 1) * rows + "," + rows;
        //拼接最终的sql语句
        System.out.println("[debug]" + sql + "\n" + tsql);
        //执行最终sql语句
        JSONArray data = super.queryArray(sql);
        //查询总记录数
        JSONArray total = super.queryArray(tsql);

        //返回查询结果
        JSONObject res = new JSONObject();
        res.put("data", data);
        res.put("total", total.getJSONObject(0).getIntValue("TOTAL"));

        return res;
    }

    /*
     *函数功能：从数据库查询服务编号对应数据表名称
     *输入参数：String sid
     *输出参数：String
     */
    private String queryTbName(String sid) {
        String tb_name = null;
        //从数据库查询服务编号对应数据表名称
        JSONArray array = super.queryArray(QUERY_TB_SQL, sid);
        System.out.println("[QUERY_TB_SQL]" + QUERY_TB_SQL + " " + sid + ";obj = " + array.toJSONString());
        if (array != null && array.size() > 0) {
            tb_name = array.getJSONObject(0).getString("TB_NAME");
        }
        System.out.println("[tb_name]" + tb_name);
        return tb_name;
    }

    /*
     *函数功能：从数据库查询服务编号对应的sql语句
     *输入参数：String sid
     *输出参数：String
     */
    private String querySQL(String sid) {
        String sql = null;
        //从数据库查询服务编号对应数据表名称
        JSONArray array = super.queryArray(QUERY_TB_SQL, sid);
        System.out.println("[QUERY_TB_SQL]" + QUERY_TB_SQL + " " + sid + ";obj = " + array.toJSONString());
        if (array != null && array.size() > 0) {
            sql = array.getJSONObject(0).getString("SQL_STR");
        }
        System.out.println("[sql]" + sql);
        return sql;
    }

    /*
     *函数名称：queryOrgTree
     *函数功能：查询组织结构树
     *输入参数：String sid, JSONObject data_obj
     *输出参数：JSONArray
     */
    public JSONArray queryOrgTree(int id, int level) {
        String sql = "";
        //根据输入的编号组织sql语句
        if (id > 0 && level == 0) {
            //level== 0 查询集团公司下面的单位,单位总人数和在线人数
            //sql = "select id as real_id, " + id * 10000 + " + id as id, team_name as name, 'images/org.png' as icon from tb_teams where organ_id = " + id;
            // sql = "select tempTm.id as real_id, " + id * 10000 + " + tempTm.id as id, tempTm.team_name as name, 'images/org.png' as icon, IFNULL(tempWk.sum, 0) workerNum from (select * from tb_teams where organ_id = "+ id+ ") tempTm LEFT JOIN (select team_id, count(*) sum from tb_worker  group BY team_id) tempWk on tempTm.id = tempWk.team_id";
            sql = "select a.id as real_id, " + id * 10000 + " + a.id as id, a.team_name as name, 'images/org.png' as icon, a.workerNum, IFNULL(b.onLineNum,0) as onlineNum from (SELECT tempTm.id, tempTm.team_name,IFNULL(tempWk.sum, 0) workerNum FROM(SELECT * FROM tb_teams WHERE organ_id  = " + id + ") tempTm "
                    + "LEFT JOIN (SELECT team_id, count(*) sum FROM tb_worker GROUP BY team_id) tempWk ON tempTm.id = tempWk.team_id) a LEFT JOIN (SELECT team_id, worker_id, NAME, count(team_id) onlineNum FROM tb_rt_point where time > date_sub(now(),interval 5 MINUTE) GROUP BY team_id) b ON a.id = b.team_id";

            System.out.println(sql);
        } else if (level == 1) {
            //level == 1 查询单位下面的施工人员
            //sql = "select id as real_id, " + id * 10000 + " + id as id, real_name as name, 'images/username.png' as icon from tb_worker where team_id = " + id;
            sql = "select id as real_id, " + id * 10000 + " + id as id, real_name as name, team_id, 'images/username.png' as icon from tb_worker where team_id = " + id;
        } else {
            //查询集团公司
            sql = "select id as real_id, id, org_name as name,'images/org.png' as icon from tb_organization ";
        }
        //执行最终sql语句
        JSONArray array = super.queryArray(sql);
        System.out.println(sql + array.toString());
        return array;
    }

    public static void main(String[] args) {
        ServletDataDao sdd = new ServletDataDao();
        Date date = new Date(System.currentTimeMillis());
    }
}
