/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.www;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jy.dao.UserDao;
import com.jy.utility.CONSTANTS;
import com.jy.utility.Util;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "ChangePwdServlet", urlPatterns = {"/ChangePwdServlet"})
public class ChangePwdServlet extends HttpServlet {

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

        HttpSession session = request.getSession();
        PrintWriter out = null;
        JSONObject res_obj = null;
        try {
             //获取客户端真实ip地址
            String ip = Util.getClientIP(request);
            System.out.println("指纹扫描IP：" + ip);
            //输出流
            out = response.getWriter();
            //执行结果对象
            res_obj = new JSONObject();
            //获取前端参数
            String username = request.getParameter("username");
            //获取服务数据
            String newpwd = request.getParameter("pwd");
            //获取指纹字符串
            String input_finger = request.getParameter("finger");
            //将数据保存到数据库表中
            UserDao ud = new UserDao();
            int num = 0;
            String info = (String) session.getAttribute("sessioninfo");
            System.out.println("[change pwd]session info:" + info);
            JSONObject obj = JSON.parseObject(info);
            //如果是在已经登陆的状态下，要验证是不是正在修改自己的密码，不允许通过这个接口修改别人的密码
            if (info != null && obj != null && obj.getString("userName").equals(username)) {
                if (input_finger != null) {
                    num = ud.updatePasswordFinger(username, newpwd, input_finger);
                } else {
                    num = ud.updatePassword(username, newpwd);
                }
            }

            if (num > 0) {
                //更新数据成功
                res_obj.put("code", CONSTANTS.SUCCESS);
                res_obj.put("info", CONSTANTS.INFO_CHGPWD_SUCCESS);
            } else {
                res_obj.put("code", CONSTANTS.ERROR_UPDATE_DATA);
                res_obj.put("info", CONSTANTS.INFO_UPDATE_FAIL);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //服务器发生异常情况
            res_obj.put("code", CONSTANTS.ERROR_SERVER);
            res_obj.put("info", CONSTANTS.INFO_SERVER_FAIL);
        } finally {
            if (out != null) {
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
