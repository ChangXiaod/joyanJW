/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Administrator
 */
public class Upload {
    public static JSONObject upload(HttpServletRequest request) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        //保存目录
        String savePath = "c:\\excel";
        File file = null;
        try {
            file = new File(savePath);
        } catch (Exception ex) {
//            file = new File(savePath);
            if (!file.exists() && !file.isDirectory()) {
                System.out.println("目录或文件不存在！");
                file.mkdir();
            }
        }
        
        //消息提示
        String message = "";
        int code = 0;
        //文件保存路径
        JSONArray file_info = new JSONArray();
        JSONObject res_obj = null;
        try {
            //执行结果对象
            res_obj = new JSONObject();
            //使用Apache文件上传组件处理文件上传步骤：
            //1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
            diskFileItemFactory.setSizeThreshold(1024 * 100);
            //设置上传时生成的临时文件的保存目录
            diskFileItemFactory.setRepository(file);
            
            //2、创建一个文件上传解析器
            ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
            //解决上传文件名的中文乱码
            fileUpload.setHeaderEncoding("UTF-8");
            //监听文件上传进度
            fileUpload.setProgressListener(new ProgressListener() {
                public void update(long pBytesRead, long pContentLength, int arg2) {
//                    filesize=pContentLength;
//                    System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
                }
            });
            
            //3、判断提交上来的数据是否是上传表单的数据
            if (!fileUpload.isMultipartContent(request)) {
                //按照传统方式获取数据
                System.out.println("[debug]not multipartcontent!");
                return null;
            }
            
            //设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
            fileUpload.setFileSizeMax(1024 * 1024 * 100);
            //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
            fileUpload.setSizeMax(1024 * 1024 * 500);
            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = fileUpload.parseRequest(request);
            System.out.println("[debug]list size:" + list.size());
            
            for (FileItem item : list) {
//                System.out.println("[debug]here!");
                //如果fileitem中封装的是普通输入项的数据
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    //解决普通输入项的数据的中文乱码问题
//                    String value = item.getString("UTF-8");
//                    String value1 = new String(name.getBytes("iso8859-1"), "UTF-8");
//                    System.out.println(name + "  " + value);
//                    System.out.println(name + "  " + value1);
                } else {
                    //如果fileitem中封装的是上传文件，得到上传的文件名称，
                    String fileName = item.getName();
                    //文件大小
                    long fileSize = item.getSize();

                    System.out.println(fileName);
                    if (fileName == null || fileName.trim().equals("")) {
                        continue;
                    }

                    //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                    //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                    String uploadname = fileName.substring(0, fileName.lastIndexOf("."));
                    //得到上传文件的扩展名
                    String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
//                    fileName = filename + "." + fileExtName;
                    if ("zip".equals(fileExtName) || "rar".equals(fileExtName) || "tar".equals(fileExtName) || "jar".equals(fileExtName)) {
                        message = "上传文件的类型不符合！";
                        code = 1;
                        return null;
                    }
                    //检查是否有相同的文件
//                    int num = sdd.queryAudioFile(uploadname);
//                    if (num > 0) {
//                        message = "对不起，已经有相同名称\"" + uploadname + "\"的媒体文件！";
//                        code = 2;
//                        return null;
//                    }
                    //如果需要限制上传的文件类型，那么可以通过文件的扩展名来判断上传的文件类型是否合法
                    System.out.println("上传文件的文件名为:" + fileName);
                    System.out.println("上传文件的扩展名为:" + fileExtName);
                    //获取item中的上传文件的输入流
                    InputStream fis = item.getInputStream();
                    //得到文件保存的名称
                    fileName = mkFileName(fileExtName);
                    //得到文件保存的路径

//                    String savePathStr = mkFilePath(CONSTANTS.UPLOAD_DIR, fileName);
                    String subdir = mkFilePath(savePath);
                    String savePathStr = savePath + "/" + subdir;
                    System.out.println("保存路径为:" + savePathStr);
                    //创建一个文件输出流
                    FileOutputStream fos = new FileOutputStream(savePathStr + File.separator + fileName);
                    //获取读通道
//                    FileChannel readChannel = ((FileInputStream)fis).getChannel();
                    //获取读通道
//                    FileChannel writeChannel = fos.getChannel();
                    //创建一个缓冲区
                    //创建一个缓冲区
                    byte buffer[] = new byte[1024];
                    //判断输入流中的数据是否已经读完的标识
                    int len = 0;
                    //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                    while ((len = fis.read(buffer)) > 0) {
                        //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                        fos.write(buffer, 0, len);
                    }
                    
                    //关闭输入流
                    fis.close();
                    //关闭输出流
                    fos.close();
                    //删除处理文件上传时生成的临时文件
                    item.delete();
                    message = "文件上传成功";
//                    String vpath = CONSTANTS.VUPLOAD_DIR + "/" + subdir + "/" + fileName;
//                    System.out.println("[debug]" + vpath);
                }
            }
        }catch(FileUploadBase.FileSizeLimitExceededException e){
            e.printStackTrace();
            message = "单个文件超出最大值！";
            code = 3;
        }catch (FileUploadBase.SizeLimitExceededException e) {
            e.printStackTrace();
            message = "上传文件的总的大小超出限制的最大值！";
            code = 4;
        } catch (FileUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            message = "文件上传失败";
            code = 5;
        }finally {
            //将执行结果发送到前端
            res_obj.put("code", code);
            res_obj.put("info", message);
            if (file_info.size() > 0) {
                res_obj.put("details", file_info);
            }
            //返回结果
            return res_obj;
        }
    }
    
    
    public static String mkFileName(String fileExtName) {
        return "AUDIO_" + UUID.randomUUID() + "." + fileExtName;
//        return fileName;
    }

    public static String mkFilePath(String savePath) {
        //得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
        Calendar cal = Calendar.getInstance();
        java.util.Date date = cal.getTime();
//        
//        int hashcode = fileName.hashCode();
//        int dir1 = hashcode & 0xf;
//        int dir2 = (hashcode & 0xf0) >> 4;
        //构造新的保存目录
        String subdir = (date.getYear() + 1900) + "_" + (date.getMonth() + 1) + "_" + date.getDate();
        String dir = savePath + "/" + subdir;
        //File既可以代表文件也可以代表目录
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return subdir;
    }

    public static int deleteFile(String filepath) {
        File file = new File(filepath);
        if (file.delete()) {
            return 1;
        }
        return 0;
    }
    
    
    
}
