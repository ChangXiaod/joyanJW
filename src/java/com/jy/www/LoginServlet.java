/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.www;

import com.alibaba.fastjson.JSONObject;
import com.jy.dao.UserDao;
import com.jy.utility.Md5Util;
import com.jy.utility.Util;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

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
        System.out.println("i am login...");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
//        String code_s = (String) session.getAttribute("validateCode");
        JSONObject json = new JSONObject();
        //获取前端参数
        String loginName = request.getParameter("name");
        String pwd = request.getParameter("pwd");
        try {
            //获取客户端真实ip地址
            String ip = Util.getClientIP(request);
            System.out.println("登录用户IP：" + ip);
            UserDao userService = new UserDao();
            JSONObject sessionInfo = userService.queryByName(loginName);
            System.out.println("[debug]loginName=" + loginName + ";result:" + sessionInfo);
            if (sessionInfo == null || sessionInfo.isEmpty()) {
                //用户不存在
                json.put("msg", "用户不存在！");
                json.put("success", false);
                return;
            } else {
                //数据库中经md5加密的密码
                String password = sessionInfo.getString("passWord");
                if ((!password.equals(Md5Util.md5(pwd))) && (!password.equals(pwd))) {
                    //密码错误
                    json.put("msg", "密码错误！");
                    json.put("success", false);
                    return;
                }
                /**
                 * 向日志表中增加操作记录
                 */
                JSONObject json_obj = new JSONObject();
                json_obj.put("name", sessionInfo.getString("realName"));
                json_obj.put("account", sessionInfo.getString("userName"));
                json_obj.put("ip", ip);

//                sdd.addLog(sessionInfo.getString("account"), sessionInfo.getString("userName"), sessionInfo.getString("realName"), CONSTANTS.LOGIN, sessionInfo.toString(), CONSTANTS.LOGIN, true);
                //添加到Session
                session.setAttribute("account", sessionInfo.getString("userName"));
                session.setAttribute("password", sessionInfo.getString("passWord"));
                //缓存会话信息
                session.setAttribute("sessioninfo", sessionInfo.toString());

                json.put("msg", "登陆成功！");
                json.put("success", true);
                json.put("account", sessionInfo.getString("userName"));
//                    json.put("password", sessionInfo.getString("passWord"));
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            json.put("msg", "服务器内部错误：" + e.getMessage());
            json.put("success", false);
        } finally {
            System.out.println(json.toString());
            out.write(json.toString());
            //关闭输出流
            if (out != null) {
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
