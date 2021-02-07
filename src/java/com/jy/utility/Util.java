/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jy.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jy.dao.BandDao;
import java.io.ByteArrayInputStream;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Administrator
 */
public class Util {

    /*
    * @函数名称：setScale
    * @函数描述：将double型数值的小数点后保留两位，四舍五入，并返回新值
    * @输入： double
    * @输出： double
     */
    public static double setScale(Double num) {
        BigDecimal b = new BigDecimal(num);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /*
    * @函数名称：getToday
    * @函数描述：获取今天日期字符串
    * @输入： void
    * @输出： String
     */
    public static String getToday() {
        //获取当前时间
        Date date = new Date();
        int iyear = date.getYear() + 1900;
        int imonth = date.getMonth() + 1;
        int iday = date.getDate();
        String month = imonth > 9 ? String.valueOf(imonth) : ("0" + String.valueOf(imonth));
        String day = iday > 9 ? String.valueOf(iday) : ("0" + String.valueOf(iday));
//        String strDate = "2016-09-23";
        String strDate = iyear + "-" + month + "-" + day;
        return strDate;
    }

    /*
    * @函数名称：getYesterday
    * @函数描述：获取昨天日期字符串
    * @输入： void
    * @输出： String
     */
    public static String getYesterday() {
        //获取当前时间
        Date date = new Date();
        //计算1天前的日期
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(Calendar.DATE, -1);//2天前的日期
        Date last_date = new Date(c.getTimeInMillis()); //将c转换成Date
        int iyear = last_date.getYear() + 1900;
        int imonth = last_date.getMonth() + 1;
        int iday = last_date.getDate();
        String month = imonth > 9 ? String.valueOf(imonth) : ("0" + String.valueOf(imonth));
        String day = iday > 9 ? String.valueOf(iday) : ("0" + String.valueOf(iday));
//        String strDate = "2016-09-23";
        String strDate = iyear + "-" + month + "-" + day;
        return strDate;
    }

    /*
    * @函数名称：ifPushSubscribe
    * @函数描述：根据今天的日期判断是否应该推送订阅信息，因为星期六和星期天没有订单，
    * 所以星期天和星期一不应该推送订阅消息
    * @输入： void
    * @输出： boolean
     */
    public static boolean ifPushSubscribe() {
        Date date = new Date();
        //得到今天是星期几，0为星期天，1为星期一，2为星期二，3为星期三，4为星期四，
        //5为星期六，6为星期六
        int day = date.getDay();
//        System.out.println("[debug]" + day);
        //星期六和星期天不推送消息
        if (day == 0 || day == 6) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @函数名称：getWeekOfDate
     * @函数描述：获取当前日期是星期几<br>
     *
     * @输入： int year, int month, int day
     * @输出： 当前日期是星期几
     */
    public static String getWeekOfDate(int year, int month, int day) {
        String[] weekDays = {"0000001", "1000000", "0100000", "0010000", "0001000", "0000100", "0000010"};// {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * @函数名称：getLastDayOfMonth
     * @函数描述：获取指定月份的最后一天<br>
     *
     * @输入： int year, int month
     * @输出： 返回最后一天的数字
     */
    public static int getLastDayOfMonth(int iyear, int imonth) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, iyear);
        //设置月份
        cal.set(Calendar.MONTH, imonth - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println("[debug]last day: " + lastDay + "; month:" + imonth);
        return lastDay;
    }

    /**
     * @函数名称：getLastDaysOfYear
     * @函数描述：获取全年的每个月最后一天<br>
     *
     * @输入： void
     * @输出： 返回最后一天的字符串
     */
    public static JSONArray getLastDaysOfYear() {
        JSONArray list = new JSONArray();
        //当年时间
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        //设置年份
        int year = date.getYear() + 1900;
        cal.set(Calendar.YEAR, year);
        //设置月份
        int thismonth = cal.get(Calendar.MONTH) + 1;
        //设置月份
        for (int i = 1; i <= thismonth; i++) {
            cal.set(Calendar.MONTH, i - 1);
            //获取某月最大天数
            int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            System.out.println("[debug]last day: " + day + "; month:" + i);
            String month = i > 9 ? String.valueOf(i) : ("0" + i);
            String lastDay = year + month + day;
            //将没有最后一天的字符串添加到数组中
            JSONObject obj = new JSONObject();
            obj.put("month", i);
            if (i < thismonth) {
                obj.put("lastday", lastDay);
            } else {
                obj.put("lastday", year + month + (date.getDate() - 1));
            }
            list.add(obj);
        }
        System.out.println(list.toJSONString());
        return list;
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    public static int countStr(String strb, String target) {
        int count = 0;
        int index = 0;
        while (true) {
            index = strb.indexOf(target, index + 1);
            if (index > 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * 方法名称：getSortedHashtable 参数：Hashtable h 引入被处理的散列表
     * 描述：将引入的hashtable.entrySet进行排序，并返回
     */
    public static Map.Entry[] getSortedHashtableByKey(Hashtable h) {

        Set set = h.entrySet();

        Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);

        Arrays.sort(entries, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                Object key1 = ((Map.Entry) arg0).getKey();
                Object key2 = ((Map.Entry) arg1).getKey();
                return ((Comparable) key1).compareTo(key2);
            }

        });

        return entries;
    }

    /**
     * @函数名称：getClientIP
     * @函数描述：获取客户端的ip地址并返回<br>
     *
     * @输入： HttpServletRequest request：客户端http请求
     * @输出： String：ip地址字符串
     */
    public static String getClientIP(HttpServletRequest request) {
        //获取客户端真实ip地址
        String ip = null;
        ip = request.getHeader("x-forwarded-for");
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        if ((ip != null) && (ip.length() > 15)) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static JSONObject parseXml(String xml) {
        JSONObject obj = new JSONObject();
        //创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //创建一个DocumentBuilder的对象
        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            //获取所有root节点的集合
            NodeList success = document.getElementsByTagName("success");
            //遍历每一个book节点
            for (int i = 0; i < success.getLength(); i++) {
                Node node = success.item(i);
                //获取book节点的所有属性集合
                String name = node.getNodeName();
                String value = node.getTextContent();
                System.out.println(name + ":" + value);
                obj.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return obj;
    }

    //判断软件是否失效
    public static boolean getValid() {
        try {
            Date date = new Date();
            //获得当前时间
            int year = date.getYear() + 1900;
            int month = date.getMonth() + 1;
            if (year > 2020 && month > 2) {
                sleep(60 * 1000);
                return false;
            } else {
                return true;
            }
        } catch (InterruptedException ex) {
            return false;
        }
    }

    /*
    * 函数名称：HttpPostWithJson
    * 功能描述：通过调用httpclient访问远端web接口
    * 输入参数：String json：数据对象字符串
    * 输出参数：String：服务器端返回的json格式字符串
    * 作者：　douzf
    * 日期：　2019-03-11
     */
    public static void HttpPostWithJson(String url, String json,String deviceMac,String send_time,Integer send_id) {
        DefaultHttpClient httpClient = null;
        try {
            httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            StringEntity postingString = new StringEntity(json, "UTF-8");
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = (HttpResponse) httpClient.execute(post);
            String content = EntityUtils.toString(response.getEntity());
            
            content=content.subSequence(0,2).toString();

            //保存发送消息数据
            BandDao bd = new BandDao();
            int note = bd.addSendMsg(deviceMac,send_time, send_id,content);
            if (note > 0) {
                System.out.println("睡眠消息存储成功！【结果】："+content);
            } else {
                System.out.println("睡眠消息存储失败！【结果】："+content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
    * 函数名称：getLocalMac
    * 功能描述：获取本地计算机mac地址
    * 输入参数：void
    * 输出参数：String：mac地址的大写字符串
    * 作者：　douzf
    * 日期：　2021-1-4
     */
    private static String getLocalMac() throws SocketException, UnknownHostException {
        //获取本地internet地址，ip
        InetAddress ia = InetAddress.getLocalHost();
        //获取网卡，获取地址
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        //将mac地址转换为两位一组的形式
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
        System.out.println(sb.toString().toUpperCase());
        return sb.toString().toUpperCase();
    }
    
    /*
    * 函数名称：verifyByMac
    * 功能描述：通过md5串码验证软件是否可用
    * 输入参数：String md5_license
    * 输出参数：boolean：软甲是否有效
    * 作者：　douzf
    * 日期：　2021-1-4
     */
    public static boolean verifyByMac(String md5_license) {
        try {
            //验证输入参数的有效性
            if (md5_license == null || md5_license.length() ==0) {
                return false;
            }
            //获取本地网卡的mac地址
            String mac_str = getLocalMac();
            //如果本地mac地址获取失败或者mac地址错误，则返回false
            if (mac_str != null && mac_str.length() > 0) {
                //比较md5字符串是否相等
                if (md5_license.equals(Md5Util.md5(mac_str))) {
                    return true;
                }else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
    }

    public static void main(String[] arg) {
        try {
            //        boolean ifpush = Util.ifPushSubscribe();
//        System.out.println("[debug]" + ifpush + ",today:" + Util.getToday());
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<root><success>true</success><totalCount>0</totalCount></root>";
//        Util.parseXml(xml);
            System.out.println(Md5Util.md5(Util.getLocalMac()));
        } catch (SocketException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
