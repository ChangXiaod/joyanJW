/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.www;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author zhqm
 */
@WebServlet(name = "VerifyCode", urlPatterns = {"/VerifyCode"})
public class VerifyCode extends HttpServlet {

     /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final long serialVersionUID = 1L;
 
    /**
     * 验证码图片的宽度。
     */
    private int width = 100;
 
    /**
     * 验证码图片的高度。
     */
    private int height =40;
 
    /**
     * 验证码字符个数
     */
    private int codeCount = 4;
 
    /**
     * xx
     */
    private int xx = 0;
 
    /**
     * 字体高度
     */
    private int fontHeight;
 
    /**
     * codeY
     */
    private int codeY;
 
    /**
     * codeSequence
     */
     String[] codeSequence = { "1" , "2" , "3" , "4" , "5" ,"6","7","8","9","A","a","B","b","c","C"
             ,"D","d","E","e","F","f","G","g","z","X","Q","v"};
 
    /**
     * 初始化验证图片属性
     */
    public void init() throws ServletException {
        // 从web.xml中获取初始信息
        // 宽度
        String strWidth =width+"";
        // 高度
        String strHeight = height+"";
        // 字符个数
        String strCodeCount = codeCount+"";
 
        // 将配置的信息转换成数值
        try {
            if (strWidth != null && strWidth.length() != 0) {
                width = Integer.parseInt(strWidth);
            }
            if (strHeight != null && strHeight.length() != 0) {
                height = Integer.parseInt(strHeight);
            }
            if (strCodeCount != null && strCodeCount.length() != 0) {
                codeCount = Integer.parseInt(strCodeCount);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
 
        xx = width / (codeCount +2);  //生成随机数的水平距离
        fontHeight = height - 10;      //生成随机数的数字高度
        codeY = height - 8;            //生成随机数的垂直距离
    }
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
       resp.setContentType("text/html;charset=UTF-8");
        init();
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D gd = buffImg.createGraphics();
 
        // 创建一个随机数生成器类
        Random random = new Random();
 
        // 将图像填充为自定义颜色
        Color selfColor = new Color(0x59ced8);
        gd.setColor(selfColor);
        gd.fillRect(0, 0, width, height);
 
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
        // 设置字体。
        gd.setFont(font);
 
        // 画边框。
        //gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);
 
        // 随机产生4条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }
 
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
 
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(27)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(125);
            green = random.nextInt(255);
            blue = random.nextInt(200);
 
            // 用随机产生的颜色将验证码绘制到图像中。
            //gd.setColor(new Color(red, green, blue));
            gd.setColor(Color.WHITE);
            gd.drawString(strRand+" ", (i + 1) * xx, codeY);
 
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        // 将四位数字的验证码保存到Session中。
        HttpSession session = req.getSession();       
        session.setAttribute("validateCode", randomCode.toString());
        System.out.println("[validateCode]" + randomCode.toString());
        
       // //System.out.println("debug[verifyCode]:::code=="+randomCode);
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
 
        resp.setContentType("image/jpeg");
 
        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
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
