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
@WebServlet(name = "DeleteDataServlet", urlPatterns = {"/DeleteDataServlet"})
public class DeleteDataServlet extends HttpServlet {

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
        String sid = "";
        JSONObject info_obj = null;
        try {
            //输出流
            out = response.getWriter();
            //执行结果对象
            res_obj = new JSONObject();
            //获取服务编号
            sid = request.getParameter("sid");
            //获取查询条件之间的与或非关系
            String relation = " and ";
            String relation_str = request.getParameter("relation");
            if (relation_str != null) {
                relation = relation_str;
            }
            //获取条件数据
            String where = "";
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
            System.out.println("[debug]where:" + where);
            //将数据保存到数据库表中
            int num = sdd.deleteData(sid, where);
            if (num > 0) {
                //记录操作日志                    
//                    if (info_obj != null) {
//                        sdd.addLog(info_obj.getIntValue("id"), info_obj.getString("userName"), info_obj.getString("realName"), CONSTANTS.DELETE, where.toString(), sid, true);
//                    }
                //删除数据成功
                res_obj.put("code", CONSTANTS.SUCCESS);
                res_obj.put("info", CONSTANTS.INFO_DELETE_SUCCESS);
            } else {
                res_obj.put("code", CONSTANTS.ERROR_DELETE_DATA);
                res_obj.put("info", CONSTANTS.INFO_DELETE_FAIL);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据异常，请联系管理员！");
            //服务器发生异常情况
            res_obj.put("code", CONSTANTS.ERROR_SERVER);
            res_obj.put("info", CONSTANTS.INFO_SERVER_FAIL);
            res_obj.put("stacktrace", e.getMessage());
        } finally {
            if (info_obj != null) {
                sdd.addLog(info_obj.getIntValue("id"), info_obj.getString("userName"), info_obj.getString("realName"), CONSTANTS.DELETE, res_obj.toString(), sid, true);
            }
            if (out != null) {
                System.out.println(res_obj.toString());
                //将执行结果发送到前端
                out.write(res_obj.toJSONString());
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
