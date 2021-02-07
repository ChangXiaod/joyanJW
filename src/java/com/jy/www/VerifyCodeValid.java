/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.www;

import com.alibaba.fastjson.JSONObject;
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
@WebServlet(name = "VerifyCodeValid", urlPatterns = {"/VerifyCodeValid"})
public class VerifyCodeValid extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject obj = new JSONObject();
        try {
            HttpSession session = req.getSession();
            //获取前端参数
            String verify_code = req.getParameter("checkCode");
            //判断验证码
            if (verify_code == null) {
                obj.put("code", 1);
                obj.put("info", "对不起，验证码为空！");
            } else {
                String vcode = (String) session.getAttribute("validateCode");
                //check verify code
                if (!verify_code.equalsIgnoreCase(vcode)) {
                    System.out.println("verify_code:" + verify_code + ",vcode:" + vcode);
                    obj.put("code", 2);
                    obj.put("info", "对不起，您输入的验证码有误!");
                } else {
                    obj.put("code", 0);
                    obj.put("info", "验证码正确!");
                }
            }
        } catch (Exception ex) {
            obj.put("code", 3);
            obj.put("info", "对不起，服务器发生内部错误!");
        } finally {
            out.write(obj.toJSONString());
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
