/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.wristband;

import com.alibaba.fastjson.JSONObject;
import com.jy.utility.sortClassUtils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author L
 */
@WebServlet(name = "toSortScheduls", urlPatterns = {"/toSortScheduls"})
public class ToSortScheduls extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        JSONObject result = null;
        try {
            out = response.getWriter();
            //开始时间
            String startTime = request.getParameter("startTime");
            //结束时间
            String stopTime = request.getParameter("stopTime");
            //几小时一班
            String duration = request.getParameter("duration");
            //组ID
            String groupID = request.getParameter("groupID");
            //房间号
            String roomID = request.getParameter("roomID");
            //房间名称
            String roomName = request.getParameter("roomName");
            result = new JSONObject();
            /**
             * 开始时间，结束时间，小时轮换，分组，房间号
             */
            if (startTime != null && stopTime != null && duration != null && groupID != null && roomID != null) {
                boolean ret = sortClassUtils.sortGroupToClass(startTime, stopTime, Integer.valueOf(duration),
                        Integer.valueOf(groupID), Integer.valueOf(roomID),roomName);
                if (ret == true) {
                    result.put("code", 0);
                    result.put("info", "恭喜您，排班成功！");
                } else {
                    result.put("code", -3);
                    result.put("info", "对不起，排班失败！");
                }
            } else {
                result.put("code", -1);
                result.put("info", "对不起，缺少输入参数！");
            }
        } catch (Exception e) {
            result.put("code", -2);
            result.put("info", e.getMessage());
        } finally {
            out.write(result.toString());
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
