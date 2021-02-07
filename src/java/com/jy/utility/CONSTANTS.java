/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.utility;

import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class CONSTANTS {
    
    //蓝牙短信地址
    public static final String BLE_MSG_URL = "http://8.131.103.186:8080/bi/education/api/sendmsg";
    //蓝牙短信发送回调接口
    public static final String BLE_RES_URL = "http://39.107.67.162:8080/jw/sendmsg";

    //oracle数据库驱动字符串
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    //mysql数据库驱动字符串
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    //sqlserver2005数据库驱动字符串
//    public static final String SQLSERVER_DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    //sqlserver2008数据库驱动字符串
    public static final String SQLSERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    //上传文件目录
    public static final String UPLOAD_DIR = "d:\\temp";
    public static final String VUPLOAD_DIR = "d:\\temp";
    //批量导出excel是每行最大列数限制
    public static final int MAX_EXCEL_COLUMN_NUM = 18;
    //导出excel文件保存目录
    public static final String EXCEL_DIR = "files";

    //操作成功
    public static final int SUCCESS = 0;
    public static final int RET_NULL_VERIFY_CODE = 1;
    public static final int RET_NULL_ACCOUNT = 2;
    public static final int RET_NULL_PASSWORD = 3;
    public static final int RET_NO_ACCOUNT = 4;
    public static final int RET_ERROR_VERIFY_CODE = 5;
    public static final int RET_ERROR_PASSWORD = 6;
    
    public static final String INFO_ADD_SUCCESS = "恭喜您，添加数据成功！";
    public static final String INFO_UPDATE_SUCCESS = "恭喜您，更新数据成功！";
    public static final String INFO_DELETE_SUCCESS = "恭喜您，删除数据成功！";
    public static final String INFO_QUERY_SUCCESS = "恭喜您，查询数据成功！";
    public static final String INFO_EMAIL_SUCCESS = "恭喜您，发送邮件成功！";
    public static final String INFO_CHGPWD_SUCCESS = "恭喜您，密码修改成功！";
    public static final String INFO_CMD_SUCCESS = "恭喜您，设置腕表终端成功！";
    public static final String INFO_ORG_SUCCESS = "恭喜您，查询组织结构数据成功！";
    public static final String INFO_POINT_SUCCESS = "恭喜您，查询位置数据成功！";
    public static final String INFO_EXPORT_SUCCESS = "恭喜您，导入地图数据成功！";
    //前端传入参数有误
    public static final int ERROR_INPUT_PARAMETER = -1;
    public static final String INFO_INPUT_PARAMETER = "对不起，前端传入参数有误！";
    //添加数据失败
    public static final int ERROR_ADD_DATA = -2;
    public static final String INFO_ADD_FAIL = "对不起，添加数据失败！";
    public static final int ERROR_UPDATE_DATA = -3;
    public static final String INFO_UPDATE_FAIL = "对不起，更新数据失败！";
    public static final int ERROR_SEND_EMAIL = -4;
    public static final String INFO_EMAIL_FAIL = "对不起，发送邮件失败！";
    public static final int ERROR_DELETE_DATA = -5;
    public static final String INFO_DELETE_FAIL = "对不起，删除数据失败！";
    public static final int ERROR_QUERY_DATA = -6;
    public static final String INFO_QUERY_FAIL = "对不起，查询数据失败！";
    public static final int ERROR_ORG_DATA = -7;
    public static final String INFO_ORG_FAIL = "对不起，查询组织结构失败！";
    //会话信息过去或者未登录
    public static final int ERROR_SESSION = -8;
    public static final String INFO_SESSION_FAIL = "对不起，会话信息已过期！";
    //服务器发生内部错误
    public static final int ERROR_SERVER = -9;
    public static final String INFO_SERVER_FAIL = "对不起，服务器发生内部错误！";
    public static final String INFO_DUPLICATE_FAIL = "对不起，已存在相同数据！";
    //命令下发服务失效
    public static final int ERROR_NETTY = -10;
    public static final String INFO_NETTY_FAIL = "对不起，命令下发服务器发生错误！";
    //设置终端命令超时
    public static final int ERROR_CMD = -11;
    public static final String INFO_CMD_FAIL = "对不起，设置腕表终端失败！";
    //查询位置信息失败
    public static final int ERROR_POINT = -12;
    public static final String INFO_POINT_FAIL = "对不起，查询位置信息失败！";
    
    //导入地图失败
    public static final int ERROR_EXPORT = -13;
    public static final String INFO_EXPORT_FAIL = "导出地图数据失败！";
    
    //日志各类操作
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String QUERY = "QUERY";
    public static final String REPLACE = "REPLACE";
    public static final String LOGIN = "LOGIN";
}
