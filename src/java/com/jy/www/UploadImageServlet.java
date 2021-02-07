/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  com.jy.www;

import com.alibaba.fastjson.JSONObject;
import  com.jy.utility.UploadUtil;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "UploadImageServlet", urlPatterns = {"/UploadImageServlet"})
public class UploadImageServlet extends HttpServlet {

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
            JSONObject obj = UploadUtil.upload(request);
            if (obj != null) {
                res_obj.put("code", obj.getIntValue("error"));
                res_obj.put("url", obj.getString("url"));
            } else {
                res_obj.put("code", -1);
                res_obj.put("url", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res_obj.put("code", -2);
            res_obj.put("url", null);
            return;
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

    //生成上传文件的文件名，文件名以：uuid+"_"+文件的原始名称
    public String mkFileName(String fileName) {
//        return UUID.randomUUID() + "_" + fileName;
        return fileName;
    }

    public String mkFilePath(String savePath, String fileName) {
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        Calendar cal = Calendar.getInstance();
        java.util.Date date = cal.getTime();
//        
//        int hashcode = fileName.hashCode();
//        int dir1 = hashcode & 0xf;
//        int dir2 = (hashcode & 0xf0) >> 4;
        //构造新的保存目录
        String subdir = (date.getYear() + 1900) + "_" + (date.getMonth() + 1) + "_" + date.getDate();
        String dir = savePath + "\\" + subdir;
        //File既可以代表文件也可以代表目录
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return subdir;
    }
}
