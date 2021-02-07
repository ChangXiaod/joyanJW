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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "AddDataServlet", urlPatterns = {"/AddDataServlet"})
public class AddDataServlet extends HttpServlet {

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
//        CommonService comserv = new CommonService(true);
        String sid = "";
        JSONObject info_obj = null;

        try {
            //输出流
            out = response.getWriter();
            //执行结果对象
            res_obj = new JSONObject();
            //如果session不为空，允许进行操作
//            if (info != null) {
            //获取前端参数
            //获取服务编号
            sid = request.getParameter("sid");
            System.out.println("[debug]sid:" + sid);
            //获取服务数据
            String data = request.getParameter("data");

            System.out.println("[debug]data:" + data);
            //将从前端获得数据转为json对象
            JSONObject data_obj = JSON.parseObject(data);
            if (data_obj != null) {
                //将数据保存到数据库表中
                int num = sdd.addData(sid, data_obj);
                if (num > 0) {
                    //记录操作日志                    
                    if (info_obj != null) {
                        sdd.addLog(info_obj.getIntValue("id"), info_obj.getString("userName"), info_obj.getString("realName"), CONSTANTS.INSERT, data.toString(), sid, true);
                    }
                    //增加数据成功
                    res_obj.put("code", CONSTANTS.SUCCESS);
                    //查询自增id
                    res_obj.put("id", num);
                    res_obj.put("info", CONSTANTS.INFO_ADD_SUCCESS);
                } else {
                    res_obj.put("code", CONSTANTS.ERROR_ADD_DATA);
                    res_obj.put("info", CONSTANTS.INFO_ADD_FAIL);
                }

            } else {
                //转换为json失败
                res_obj.put("code", CONSTANTS.ERROR_INPUT_PARAMETER);
                res_obj.put("info", CONSTANTS.INFO_INPUT_PARAMETER);
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
            String message = e.getMessage();
            if (message.startsWith("Duplicate entry")) {
                res_obj.put("info", CONSTANTS.INFO_DUPLICATE_FAIL);
            } else {
                res_obj.put("info", e.getMessage());
            }
            res_obj.put("stacktrace", e.getMessage());
        } finally {
//            if (info_obj != null) {
//                sdd.addLog(info_obj.getIntValue("id"), info_obj.getString("userName"), info_obj.getString("realName"), CONSTANTS.INSERT, res_obj.toString(), sid, true);
//            }
            if (out != null) {
                //将执行结果发送到前端
                System.out.println(res_obj.toString());
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
