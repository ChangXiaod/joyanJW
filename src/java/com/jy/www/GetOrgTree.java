/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.www;

import com.alibaba.fastjson.JSONArray;
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
@WebServlet(name = "GetOrgTree", urlPatterns = {"/GetOrgTree"})
public class GetOrgTree extends HttpServlet {

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
        try {
            //输出流
            out = response.getWriter();
            //执行结果对象
            res_obj = new JSONObject();
            //获取前端参数
            //获取节点编号
            String id = request.getParameter("id");
            //获取节点名称
            String name = request.getParameter("name");
            //获取节点层级
            String level = request.getParameter("level");
            System.out.println("[tree]id:" + id);
//            System.out.println("[tree]name:" + name);
            System.out.println("[tree]level:" + level);
            //将从前端获得数据转为json对象
            if (id != null && level != null) {
                //根据父节点编号和层级编号查询组织结构
                JSONArray orgs = sdd.queryOrgTree(Integer.valueOf(id), Integer.valueOf(level));
                if (orgs != null && orgs.size() > 0) {
                    //查询组织数据成功
                    res_obj.put("code", CONSTANTS.SUCCESS);
                    //将数据格式组织成前端需要的格式
                    for (int i = 0; i < orgs.size(); i++) {
                        JSONObject obj = orgs.getJSONObject(i);
                        obj.put("pId", id);
                        obj.put("open", true);
                    }
                    res_obj.put("org", orgs.toString());
                    res_obj.put("info", CONSTANTS.INFO_ORG_SUCCESS);
                } else {
                    res_obj.put("code", CONSTANTS.ERROR_ORG_DATA);
                    res_obj.put("info", CONSTANTS.INFO_ORG_FAIL);
                }

            } else {
                //转换为json失败
                res_obj.put("code", CONSTANTS.ERROR_INPUT_PARAMETER);
                res_obj.put("info", CONSTANTS.INFO_INPUT_PARAMETER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据异常，请联系管理员！");
            //服务器发生异常情况
            res_obj.put("code", CONSTANTS.ERROR_SERVER);
            res_obj.put("info", CONSTANTS.INFO_SERVER_FAIL);
            res_obj.put("stacktrace", e.getMessage());
        } finally {
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
