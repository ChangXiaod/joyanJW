/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.www;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jy.dao.ServletDataDao;
import com.jy.utility.CONSTANTS;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "QueryDataServlet", urlPatterns = {"/QueryDataServlet"})
public class QueryDataServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = null;
        JSONObject res_obj = null;
        ServletDataDao sdd = new ServletDataDao();
        //获取该用户的登录信息
//        String info = (String) session.getAttribute("sessioninfo");
//        System.out.println("[debug]info:" + info);
//        JSONObject info_obj = null;
        String sid = "";
        String variables = "";
        try {
//            info_obj = JSON.parseObject(info);
            //输出流
            out = response.getWriter();
            //执行结果对象
            
            res_obj = new JSONObject();
            //获取该用户的登录信息
//            String info = (String) session.getAttribute("sessioninfo");
//            System.out.println("[debug]info:" + info);
            //如果session不为空，允许进行操作
//            if (info != null) {
            //获取前端参数
            Enumeration em = request.getParameterNames();
            while (em.hasMoreElements()) {
                String param_name = em.nextElement().toString();
                System.out.println("[request]" + param_name + ":" + request.getParameter(param_name));
            }

            //获取服务编号
            sid = request.getParameter("sid");
            String sampling = request.getParameter("sampling");//是否抽样取数，避免前端曲线图过载
            //如果前端传递了变量参数，则采用定制sql查询
            variables = request.getParameter("variables");

            //获取查询字段列表
            String cols = request.getParameter("cols");
            //获取条件数据,如果用户没有定义conditions字段，那么查询条件就取condition
            //关键字，如果设置了conditions查询字段，那么，查询条件从conditions对象中生成
            String where = "";
            //获取查询条件之间的与或非关系
            String relation = " and ";
            String relation_str = request.getParameter("relation");
            if (relation_str != null) {
                relation = relation_str;
            }
            //获取条件数组
            String condz = request.getParameter("conditions");
            System.out.println("[debug]conditions:" + condz);
            //解析条件数组，优先使用条件数组
            if (condz != null) {
                JSONObject condz_obj = JSON.parseObject(condz);
                //解析查询条件
                int sz = condz_obj.size();
                //如果条件对象大于零，则解析并生成查询条件
                if (sz > 0) {
                    //获取所有关键字集合
                    Set set = condz_obj.keySet();
                    //获取关键字集合的迭代器
                    Iterator itr = set.iterator();
                    //逐个扫描迭代器，查询关键字对应的值
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        if (key != null) {
                            where += key + "='" + condz_obj.getString(key) + "'" + relation;
                        }
                    }
                    //去除最后一个关系符号
                    where = where.substring(0, where.length() - relation.length());
                }
            }
            //如果没有设置conditions字段，则采用condtion字段作为查询条件
            if (where.equalsIgnoreCase("")) {
                where = request.getParameter("condition");
            }
            //获取关键字
            String key = request.getParameter("key");
            //聚合字段
            String groupby = request.getParameter("group");
            //获取排序
            String orderby = request.getParameter("order");
            //获取是否降序
            String desc = request.getParameter("desc");
            //获取分页信息
            String page = request.getParameter("page");
            String rows = request.getParameter("rows");
            System.out.println("[debug]page = " + page + "; rows = " + rows + ";select list:" + cols + ", key = " + key + ", where = " + where + "; groupby = " + groupby + "; sid = " + sid + "; order=" + orderby);
            int ipage = 1, irows = 200;
            if (page != null) {
                ipage = Integer.parseInt(page);
                irows = Integer.parseInt(rows);
            }
            //是否为复杂查询：sql语句结合条件子句进行查询
            String complex = request.getParameter("complex");

//                JSONObject where_obj = JSON.parseObject(where);
            //从数据库查询数据
            JSONObject res = null;
            //有限使用关键字查询，如果没有定义关键字，则使用condition条件进行查询
            if (variables != null) {
                //从数据库查询数据
                String var_list[] = variables.split(",");
                for (int i = 0; i < var_list.length; i++)
                System.out.println("[debug var list]" + i + ":" +  var_list[i]);
                res = sdd.queryDataBySql(sid, ipage, irows, var_list);
            } else if (complex != null && complex.equals("true")) {
                res = sdd.queryDataByComplexSql(sid, where);
            } else if (key != null && !key.equals("")) {
                res = sdd.queryDataByKey(sid, cols, key, groupby, orderby, desc, ipage, irows);
            } else {
                res = sdd.queryData(sid, cols, where, groupby, orderby, desc, ipage, irows);
            }
            if (res != null) {
                //查询数据成功
                res_obj.put("code", CONSTANTS.SUCCESS);
                res_obj.put("info", CONSTANTS.INFO_QUERY_SUCCESS);
                res_obj.put("result", res);
            } else {
                res_obj.put("code", CONSTANTS.ERROR_QUERY_DATA);
                res_obj.put("info", CONSTANTS.INFO_QUERY_FAIL);
                res_obj.put("result", "");
            }
//            } else {
//                //如果session已经过期，不能进行任何数据操作
//                res_obj.put("code", CONSTANTS.ERROR_SESSION);
//                res_obj.put("info", CONSTANTS.INFO_SESSION_FAIL);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据异常，请联系管理员！");
            //服务器发生异常情况
            res_obj.put("code", CONSTANTS.ERROR_SERVER);
            res_obj.put("info", CONSTANTS.INFO_SERVER_FAIL);
            res_obj.put("stacktrace", e.getMessage());
        } finally {
            if (out != null) {
//                if (info_obj != null) {
//                    sdd.addLog(info_obj.getIntValue("id"), info_obj.getString("userName"), info_obj.getString("realName"), CONSTANTS.QUERY, res_obj.toString(), sid, false);
//                }

                //将执行结果发送到前端
//                System.out.println(res_obj.toString());
                out.write(res_obj.toString());
                //关闭输出流
                out.close();
            }
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

}
