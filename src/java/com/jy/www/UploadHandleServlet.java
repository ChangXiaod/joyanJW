package com.jy.www;

import com.alibaba.fastjson.JSONObject;
import com.jy.utility.Upload;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UploadHandleServlet", urlPatterns = {"/UploadHandleServlet"})
public class UploadHandleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = null;
        JSONObject res_obj = null;
        try {
            //输出流
            out = response.getWriter();
            //执行结果对象
            res_obj = new JSONObject();
            //调用公用函数，上传图片
            JSONObject obj = Upload.upload(request);
            if (obj != null) {
                //错误的代码
                res_obj.put("code", obj.getIntValue("code"));
                //错误信息
                res_obj.put("info", obj.getString("info"));
            } else {
                //错误的代码
                res_obj.put("code", -9);
                //错误信息
                res_obj.put("info", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //错误的代码
            res_obj.put("code", -10);
            //错误信息
            res_obj.put("info", e.getLocalizedMessage());
        } finally {
            System.out.println("[debug]finally...");
            if (out != null) {
                //将执行结果发送到前端
                System.out.println("[debug]response:" + res_obj.toJSONString());
                out.write(res_obj.toJSONString());
                //关闭输出流
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }	
}
