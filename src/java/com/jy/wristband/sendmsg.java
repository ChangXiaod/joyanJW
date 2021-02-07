/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.wristband;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 作者：田刚 时间：2021年1月3日 类名称：sendmsg 类功能：实现手环震动报警信息回调 请求访问路径："/sendmsg"
 */
@WebServlet(name = "sendmsg", urlPatterns = {"/sendmsg"})
public class sendmsg extends HttpServlet {

    /**
     * 作者：田刚 时间：2021年1月3日 方法名称：processRequest 方法功能：实现请求数据接收 入参;
     * HttpServletRequest request:请求参数 HttpServletResponse response：返回参数 出参：无
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置返回数据格式
        response.setContentType("text/html;charset=UTF-8");
        BufferedInputStream buffer = new BufferedInputStream(request.getInputStream());
        int cLen = 0, bufLen = 0;
        byte[] result = new byte[4096];
        byte[] buf = new byte[4096];

        while ((bufLen = buffer.read(buf)) > 0) {
            if (bufLen > result.length - cLen) {
                byte[] tmp = new byte[result.length + 4096];
                System.arraycopy(result, 0, tmp, 0, cLen);
                result = tmp;
            }
            System.arraycopy(buf, 0, result, cLen, bufLen);
            cLen += bufLen;
        }
        //设置转化接收数据格式
        StringBuilder body = new StringBuilder(new String(Arrays.copyOf(
                result, cLen), "UTF-8"));

        String bodyStr = body.toString();
        if (bodyStr != null && !bodyStr.equals("")) {
            System.out.println("[消息响应]" + bodyStr);
        } else {
            System.out.println("[消息响应]" + "请求体文本流为空");
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
