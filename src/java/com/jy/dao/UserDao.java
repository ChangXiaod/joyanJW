/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author Administrator
 */
public class UserDao extends BaseDao {

    private static final int FINGER_SIM_THRESHOLD = 50;

    //构造函数
    public UserDao() {
        super();
    }

    /*
    *函数名称：passwordCheck
    *函数描述：账号密码验证函数
    *输入参数：String account：用户账号
              String password：用户密码，默认是md5码
    *输出参数：boolean：返回查询结果，如果验证通过，则返回true，否则为false
    *作者：douzf
    *创建日期：2019-08-01
     */
    public boolean passwordCheck(String account, String password) {
        //查询账号密码的sql语句
        String sql = "select userName from tb_user where userName = ? and password = ?";
        //输入参数合法性检查
        if (account == null || password == null) {
            return false;
        }
        //执行sql语句
        JSONArray array = super.queryArray(sql, account, password);
        //如果查询到符合条件的记录，则认为账号密码验证通过
        if (array != null && array.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
    *函数名称：passwordApproval
    *函数描述：账号密码验证函数
    *输入参数：String account：用户账号
              String password：用户密码，默认是md5码
    *输出参数：boolean：返回查询结果，如果验证通过，则返回true，否则为false
    *作者：douzf
    *创建日期：2019-11-14
     */
    public boolean passwordApproval(String account, String password) {
        try {
            //查询账号密码的sql语句
            String sql = "select userName, job_name from tb_user where userName = ? and password = ?";
            //输入参数合法性检查
            if (account == null || password == null) {
                return false;
            }
            //执行sql语句
            JSONArray array = super.queryArray(sql, account, password);
            //如果查询到符合条件的记录，则认为账号密码验证通过
            if (array != null && array.size() > 0) {
                //检查是否有审批权限
                JSONObject obj = array.getJSONObject(0);

                int job_name = obj.getIntValue("job_name");
                //只有班长以上的职位采用审批权限，普通员工不可以审批
                if (job_name > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            //如果职位解析识别，就返回错误提示
            return false;
        }

    }

    /*
    *函数名称：updateLoginInfo
    *函数描述：修改登录信息中的指纹识别结果
    *输入参数：String ip: 用户端的ip地址
             int recog_status：指纹识别结果，默认为0,表示没有识别，1为已经正确识别指纹
    *输出参数：int：返回修改结果
    *作者：douzf
    *创建日期：2019-06-22
     */
    public int updateLoginInfo(String ip, int recog_status) {
        //验证参数的合法性
        if (recog_status < 0) {
            return 0;
        }
        //数据查询语句
        String sql = "update  tb_login_info set = ?  where ip = ?";
        //执行更新操作并返回结果
        return update(sql, recog_status, ip);
    }


    /*
    *函数名称：queryByName
    *函数描述：查询用户信息
    *输入参数：String userName：用户账号
    *输出参数：JSONObject：用户信息
    *作者：douzf
    *创建日期：2019-06-22
     */
    public JSONObject queryByName(String userName) {
        //验证参数的合法性
        if (userName == null || userName.length() <= 0) {
            return null;
        }
        //查询语句
        String sql = "SELECT a.id,"
                + "a.userName,"
                + "a.realName,"
                + "a.name,"
                + "a.passWord,"
                + "a.createTime,"
                + "a.departSID,"
                + "b.org_name, "
                + "a.roleIDS,"
                + "a.isActive,"
                + "a.finger,"
                + "a.createTime "
                + "FROM tb_user a, tb_organization b "
                + "WHERE a.isActive = 1 and a.userName = ? and a.orgCode = b.org_code";
//        String company_sql = "select org_name from tb_organization where id = ?";
        //执行查询操作
        JSONArray rs = queryArray(sql, userName);
        JSONObject obj = null;
        if (rs != null && rs.size() > 0) {
            obj = rs.getJSONObject(0);
        }
        //返回查询结果
        if (obj != null) {
            System.out.println("[query name]" + obj.toJSONString());
            return obj;
        } else {
            return null;
        }
    }

    /*
    *函数名称：queryAddressById
    *函数描述：通过ip查询对应的位置
    *输入参数：ip：IP地址
    *输出参数：JsonObject:期别和地址
    *作者：douzf
    *创建日期：2019-09-24
     */
    public JSONObject queryAddressById(String ip) {
        String sql = "select qibie, address from tb_ip where ip = ?";
        //执行查询操作
        JSONArray array = queryArray(sql, ip);
        if (array != null && array.size() > 0) {
//            System.out.println(array.toString());
            return array.getJSONObject(0);
        } else {
            return null;
        }
    }

    /*
    *函数名称：queryMenuByRoleId
    *函数描述：通过角色编号和ip查询对应的菜单
    *输入参数：String role_id：角色编码
    *输出参数：JSONArray：菜单字符串，如果没有则为空
    *作者：douzf
    *创建日期：2019-07-11
     */
    public JSONArray queryMenuByRoleId(String role_id) {
        System.out.println(role_id);
        String sql = "select resource from tb_role where id in (";
        JSONArray idz = JSONArray.parseArray(role_id);
        if (idz.size() > 0) {
            for (int i = 0; i < idz.size(); i++) {
                sql += idz.getString(i);
                if (i < idz.size() - 1) {
                    sql += ",";
                }
            }
            sql += ")";
            System.out.println("[debug]" + sql);
            //执行查询操作
            return queryArray(sql);
        } else {
            return null;
        }
    }
  
    /*
    *函数名称：updatePasswordFinger
    *函数描述：更新用户密码以及指纹图片
    *输入参数：String username：用户账号
             String newpwd：用户新密码
             String finger_file：指纹图片的存放路径
    *输出参数：int：大于0表示修改成功，否则表示修改失败
    *作者：douzf
    *创建日期：2019-07-12
     */
    public int updatePasswordFinger(String username, String newpwd, String finger_file) {
        String sql = "update tb_user set passWord = ?, finger = ? where userName = ?";
        return update(sql, newpwd, finger_file, username);
    }

    /*
    *函数名称：updatePassword
    *函数描述：更新用户密码，用户在未登录的情况下，调用这个接口
    *输入参数：String username：用户账号
             String oldpwd：用户旧密码
             String newpwd：用户新密码
    *输出参数：int：大于0表示修改成功，否则表示修改失败
    *作者：douzf
    *创建日期：2019-06-22
     */
    public int updatePassword(String username, String oldpwd, String newpwd) {
        String sql = "update tb_user set passWord = ? where userName = ? and passWord = ?";
        return update(sql, newpwd, username, oldpwd);
    }

    /*
    *函数名称：updatePassword
    *函数描述：更新用户密码，用户登录的情况下直接调用这个接口
    *输入参数：String username：用户账号
             String newpwd：用户新密码
    *输出参数：int：大于0表示修改成功，否则表示修改失败
    *作者：douzf
    *创建日期：2019-06-22
     */
    public int updatePassword(String username, String newpwd) {
        String sql = "update tb_user set passWord = ? where userName = ?";
        return update(sql, newpwd, username);
    }

    /*
    *函数名称：queryEmailsByUserNames
    *函数描述：根据账号列表查询电子邮件列表
    *输入参数：String username：用户账号
    *输出参数：JSONArray：邮件列表信息
    *作者：douzf
    *创建日期：2019-06-22
     */
    public JSONArray queryEmailsByUserNames(String usernames) {
        JSONArray emails = null;
        //验证参数的合法性
        if (usernames == null || usernames.isEmpty()) {
            return emails;
        }
        //去掉；符号，可以一次查询多个账号的电子邮件地址
        usernames = usernames.replace(";", ",");
        String sql = "SELECT realName, email FROM tb_user WHERE userName in " + usernames;
        //执行查询并返回查询结果
        return queryArray(sql);
    }

    /*
    *函数名称：saveJiaojiebanLog
    *函数描述：记录交接班日志
    *输入参数：String jiaobanren_account：交班人账号
            String jiaobanren_name：交班人姓名
            String jiaoban_type：交班方式:指纹或者密码
            String jiebanren_account：接班人账号
            String jiebanren_name：接班人姓名
            String jieban_type：接班方式
            String location：接交班岗位：集控室，采制样间，翻车机监控室等
            String ip：交接班主机ip
            String zhici：值次：一班二班...六班
            String banci：班次：早班中午晚班
    *输出参数：int 如果保存交接班日志成功，返回1，否则，返回0
    *作者：douzf
    *创建日期：2019-11-11
     */
    public int saveJiaojiebanLog(String jiaobanren_account,//交班人账号
            String jiaobanren_name,//交班人姓名
            String jiaoban_type,//交班方式:指纹或者密码
            String jiebanren_account,//接班人账号
            String jiebanren_name,//接班人姓名
            String jieban_type,//接班方式
            String location,//接交班岗位：集控室，采制样间，翻车机监控室等
            String ip,//交接班主机ip
            String zhici,//值次：一班二班...六班
            String banci) {//班次：早班中午晚班
        String sql = "insert into tb_jiaojieban_log(jiaobanren_account,"
                + "jiaobanren_name,"
                + "jiaoban_type,"
                + "jiebanren_account,"
                + "jiebanren_name,"
                + "jieban_type,"
                + "location,"
                + "ip,"
                + "zhici,"
                + "banci,"
                + "update_time)values(?,?,?,?,?,?,?,?,?,?,now())";
        return update(sql,
                jiaobanren_account,//交班人账号
                jiaobanren_name,//交班人姓名
                jiaoban_type,//交班方式:指纹或者密码
                jiebanren_account,//接班人账号
                jiebanren_name,//接班人姓名
                jieban_type,//接班方式
                location,//接交班岗位：集控室，采制样间，翻车机监控室等
                ip,//交接班主机ip
                zhici,//值次：一班二班...六班
                banci//班次：早班中午晚班
        );
    }
}
