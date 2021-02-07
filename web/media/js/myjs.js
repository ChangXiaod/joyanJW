function HashMap()
{
    /** Map 大小 **/
    var size = 0;
    /** 对象 **/
    var entry = new Object();
    /** 存 **/
    this.put = function (key, value)
    {
        if (!this.containsKey(key))
        {
            size++;
        }
        entry[key] = value;
    }

    /** 取 **/
    this.get = function (key)
    {
        return this.containsKey(key) ? entry[key] : null;
    }

    /** 删除 **/
    this.remove = function (key)
    {
        if (this.containsKey(key) && (delete entry[key]))
        {
            size--;
        }
    }

    /** 是否包含 Key **/
    this.containsKey = function (key)
    {
        return (key in entry);
    }

    /** 是否包含 Value **/
    this.containsValue = function (value)
    {
        for (var prop in entry)
        {
            if (entry[prop] == value)
            {
                return true;
            }
        }
        return false;
    }

    /** 所有 Value **/
    this.values = function ()
    {
        var values = new Array();
        for (var prop in entry)
        {
            values.push(entry[prop]);
        }
        return values;
    }

    /** 所有 Key **/
    this.keys = function ()
    {
        var keys = new Array();
        for (var prop in entry)
        {
            keys.push(prop);
        }
        return keys;
    }

    /** Map Size **/
    this.size = function ()
    {
        return size;
    }

    /* 清空 */
    this.clear = function ()
    {
        size = 0;
        entry = new Object();
    }
}

//检查浏览器类型和版本号
function checkBrowser() {
    var agent = navigator.userAgent.toLowerCase(),
            opera = window.opera,
            browser = {
                //检测当前浏览器是否为IE  
                ie: /(msie\s|trident.*rv:)([\w.]+)/.test(agent),
                //检测当前浏览器是否为Opera  
                opera: (!!opera && opera.version),
                //检测当前浏览器是否是webkit内核的浏览器  
                webkit: (agent.indexOf(' applewebkit/') > -1),
                //检测当前浏览器是否是运行在mac平台下  
                mac: (agent.indexOf('macintosh') > -1),
                //检测当前浏览器是否处于“怪异模式”下  
                quirks: (document.compatMode == 'BackCompat')
            };
    //检测当前浏览器内核是否是gecko内核  
    browser.gecko = (navigator.product == 'Gecko' && !browser.webkit && !browser.opera && !browser.ie);
    var version = 0;
    // Internet Explorer 6.0+  
    if (browser.ie) {
        var v1 = agent.match(/(?:msie\s([\w.]+))/);
        var v2 = agent.match(/(?:trident.*rv:([\w.]+))/);
        if (v1 && v2 && v1[1] && v2[1]) {
            version = Math.max(v1[1] * 1, v2[1] * 1);
        } else if (v1 && v1[1]) {
            version = v1[1] * 1;
        } else if (v2 && v2[1]) {
            version = v2[1] * 1;
        } else {
            version = 0;
        }
//检测浏览器模式是否为 IE11 兼容模式  
        browser.ie11Compat = document.documentMode == 11;
        //检测浏览器模式是否为 IE9 兼容模式  
        browser.ie9Compat = document.documentMode == 9;
        //检测浏览器模式是否为 IE10 兼容模式  
        browser.ie10Compat = document.documentMode == 10;
        //检测浏览器是否是IE8浏览器  
        browser.ie8 = !!document.documentMode;
        //检测浏览器模式是否为 IE8 兼容模式  
        browser.ie8Compat = document.documentMode == 8;
        //检测浏览器模式是否为 IE7 兼容模式  
        browser.ie7Compat = ((version == 7 && !document.documentMode) || document.documentMode == 7);
        //检测浏览器模式是否为 IE6 模式 或者怪异模式  
        browser.ie6Compat = (version < 7 || browser.quirks);
        browser.ie9above = version > 8;
        browser.ie9below = version < 9;
    }
// Gecko.  
    if (browser.gecko) {
        var geckoRelease = agent.match(/rv:([\d\.]+)/);
        if (geckoRelease) {
            geckoRelease = geckoRelease[1].split('.');
            version = geckoRelease[0] * 10000 + (geckoRelease[1] || 0) * 100 + (geckoRelease[2] || 0) * 1;
        }
    }
//检测当前浏览器是否为Chrome, 如果是，则返回Chrome的大版本号  
    if (/chrome\/(\d+\.\d)/i.test(agent)) {
        browser.chrome = +RegExp['\x241'];
    }
//检测当前浏览器是否为Safari, 如果是，则返回Safari的大版本号  
    if (/(\d+\.\d)?(?:\.\d)?\s+safari\/?(\d+\.\d+)?/i.test(agent) && !/chrome/i.test(agent)) {
        browser.safari = +(RegExp['\x241'] || RegExp['\x242']);
    }
// Opera 9.50+  
    if (browser.opera)
        version = parseFloat(opera.version());
    // WebKit 522+ (Safari 3+)  
    if (browser.webkit)
        version = parseFloat(agent.match(/ applewebkit\/(\d+)/)[1]);
    //检测当前浏览器版本号  
    browser.version = version;
    return browser;
}

//分解url获得数据部分  
function getUrlParams() {
    var search = window.location.search;
    // 写入数据字典   
    var tmparray = search.substr(1, search.length).split("&");
    var paramsArray = new Array;
    if (tmparray != null) {
        for (var i = 0; i < tmparray.length; i++) {
            var reg = /[=|^==]/; // 用=进行拆分，但不包括==   
            var set1 = tmparray[i].replace(reg, '&');
            var tmpStr2 = set1.split('&');
            var array = new Array;
            array[tmpStr2[0]] = tmpStr2[1];
            paramsArray.push(array);
        }
    }
// 将参数数组进行返回   
    return paramsArray;
}

// 根据参数名称获取参数值   
function getParamValue(name) {
    var paramsArray = getUrlParams();
    if (paramsArray != null) {
        for (var i = 0; i < paramsArray.length; i++) {
            for (var j in paramsArray[i]) {
                if (j == name) {
                    return paramsArray[i][j];
                }
            }
        }
    }
    return null;
}

//判断文件类型是否为doc或者pdf格式
function checkFileExt(filename)
{
    var flag = false; //状态
    var arr = ["doc", "docx", "pdf"];
    //取出上传文件的扩展名
    var index = filename.lastIndexOf(".");
    var ext = filename.substr(index + 1);
    //循环比较
    for (var i = 0; i < arr.length; i++)
    {
        if (ext == arr[i])
        {
            flag = true; //一旦找到合适的，立即退出循环
            break;
        }
    }
    //条件判断
    return flag;
}


//自动获取所有的输入参数
function getInputs() {
    var params = {};
    if ($("input,select").each(function (i, input) {
        var val = $("#" + input.id).val();
        if (val != "" && val != null && val != undefined) {
            //设置键值对
            params[input.id] = val;
            //如果遍历结束，则返回结果
            if (i == $("input,select").size() - 1) {
                return true;
            }
        }
    })) {
//返回参数值
        return params;
    } else {
        return null;
    }
}

//自动设置元素的值
function setInputs(item) {
    $("input,textarea").each(function (i, input) {
//从输入值中取出id相同的值
        var val = item[input.id];
        //判断值是否为空
        if (val != "" && val != null && val != undefined) {
            $("#" + input.id).val(val);
        }
    });
    $("select").each(function (i, input) {
//从输入值中取出id相同的值
        var val = item[input.id];
        //判断值是否为空
        if (val != "" && val != null && val != undefined) {
//            alert(input.id + "-" + val)
            $("#" + input.id).val(val);
            $("#" + input.id).find("option[value='" + val + "']").attr("selected", true);
        }
    });
}

//自动情况所有输入框中的数据
function clearInputs() {
    $("input").each(function (i, input) {
//判断值是否为空
        $("#" + input.id).val("");
    });
    $("select").each(function (i, input) {
//判断值是否为空
        $("#" + input.id).val("");
    });
}

//取出html格式
function delHtmlTag(str) {
    return str.replace(/<[^>]+>/g, ""); //去掉所有的html标记  
}

//imei正则验证 
function checkImei(imei) {
    var regImei = /^\d{15}$/;
    if (regImei.test(imei)) {
        return true;
    } else {
        return false;
    }
}
//判断是否无效的身份证编号
function checkIdNo(value) {
    var regNo = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if (regNo.test(value)) {
        return true;
    } else {
        return false;
    }
}

//用户名正则验证
function checkUserName(name) {
    var regName = /^[\u4E00-\u9FA5]{2,6}$/;
    if (regName.test(name)) {
        return true;
    } else {
        return false;
    }
}
//sim卡号验证
function checkSimNo(value) {
    var regSim = /^\d{13}$/;
    if (regSim.test(value)) {
        return true;
    } else {
        return false;
    }
}

//年龄正则验证（年龄只能0~99，两位数）
function checkUserAge(age)
{
    var regNum = /^[0-9]{1,2}$/;
    if (regNum.test(age)) {
        return true;
    } else {
        return false;
    }
}
//验证电话号码（手机或固定电话）
function checkPhoneNo(value) {
    var regPhone = /^((0\d{2,3}-\d{7,8})|(0\d{2,3}\d{7,8})|(\d{7,8})|(1[3584]\d{9}))$/;
    if (regPhone.test(value)) {
        return true;
    } else {
        return false;
    }
}

//4到16位（字母，数字，下划线，减号）
function checkLoginUser(value) {
    var regLogin = /^[a-zA-Z0-9_-]{4,16}$/;
    if (regLogin.test(value)) {
        return true;
    } else {
        return false;
    }
}
//邮箱验证
function checkEmail(value) {
    var emailPatten = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
    if (emailPatten.test(value)) {
        return true;
    } else {
        return false;
    }
}
//移动电话号码验证
function checkMobileNo(value) {
    var mPattern = /^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\d{8}$/;
    if (mPattern.test(value)) {
        return true;
    } else {
        return false;
    }
}
//固定电话验证
function checkTelNo(value) {
    var regTelNo = /(^0\d{2,3}-\d{7,8}$)|(^0\d{2,3}\d{7,8}$)|(^\d{7,8}$)/;
    //var regTelNo = /(^[0-9]{3,4}\-[0-9]{3,8}$)|(^[0-9]{3,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)/; 
    if (regTelNo.test(value)) {
        return true;
    } else {
        return false;
    }
}

//验证不能为空
function checkNull(value) {
    if (value == "" || value == null || value == undefined) {
        return true;
    } else {
        return false;
    }
}

//获得当前的日期时间，格式“yyyy-MM-dd HH:MM:SS”
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentDate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes() + seperator2 + date.getSeconds();
    return currentDate;
}

//显示提示消息
function showTip(content) {
    $('#tip_msg').text(content);
    $('#tip_dialog').modal('show');
}

//显示格式化时间
Date.prototype.format = function (format)
{
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1,
                (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(format))
            format = format.replace(RegExp.$1,
                    RegExp.$1.length == 1 ? o[k] :
                    ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}


//将rgba颜色转换为16进制颜色
function hexify(color) {
    var values = color
            .replace(/rgba?\(/, '')
            .replace(/\)/, '')
            .replace(/[\s+]/g, '')
            .split(',');
    var a = parseFloat(values[3] || 1),
            r = Math.floor(a * parseInt(values[0]) + (1 - a) * 255),
            g = Math.floor(a * parseInt(values[1]) + (1 - a) * 255),
            b = Math.floor(a * parseInt(values[2]) + (1 - a) * 255);
    return "#" +
            ("0" + r.toString(16)).slice(-2) +
            ("0" + g.toString(16)).slice(-2) +
            ("0" + b.toString(16)).slice(-2);
}

//获得一个随机数
function getRandom() {
    return Math.floor((Math.random() * 10000) + 1);
}

//初始化菜单
function initMenu() {
    var params = {};
    params.sid = "get_role";
    //默认为第一个职位
    var roles = JSON.parse(sessionStorage.getItem("role_id")); //获得保存的职位信息
    if (roles != null && roles != undefined && roles != "") {
//                    params.condition = "id in (";
//                    var i;
//                    for (i = 0; i < roles.length; i++) {
//                        params.condition += roles[i];
//                        if (i < roles.length - 1) {
//                            params.condition += ",";
//                        }
//                    }
        params.role_id = roles[0];
        //params.condition="id in(13,14)"
    } else {
        //如果没有职位定义，则返回
        return;
    }

    //调用查询服务  查询这个ID下应该显示什么菜单
    $.post("QueryMenu", params, function (mdata) {
//                    mdata这个职位下应该显示的菜单
        var data = JSON.parse(mdata);
        if (data.code == 0) {
            //默认加载第一个职位
            var items = JSON.parse(data.result[0].resource);
//                        alert(JSON.stringify(items));
            updateMenu(items); //更新左边菜单
        }
    });
}

//更新菜单
function updateMenu(items) {
    if (items != null && items != undefined) {
        //动态添加菜单
        var menu_html = "";
        var i = 0;
        //全局变量，暂时保存重新归并后的菜单项
        var menu_hash = resortMenu(items);
        for (; i < items.length; i++) {
            //单个node
            var item = items[i];
            //   alert(JSON.stringify(item));
            //判断node的类型，如果是一级菜单，直接生成菜单
            if (item.parentid == '0') {
                var menu = menu_hash.get(item.id);
                var submenu = menu.submenu;
                //有子菜单的情况
                if (submenu != null && submenu != undefined) {
                    menu_html += "<li class=\"dropdown\">\n\
                                                <a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\" style=\"color:white;font-size:10pt;\">\n\
                                                  " + item.name + "\n\
                                                  <span class=\"caret \"></span>\n\
                                                </a><ul class=\"dropdown-menu\">";
                    var j = 0;
                    for (; j < submenu.length; j++) {
                        var sub = submenu[j];
                        // alert(sub.url); 
                        menu_html += "<li>\n\
                                                    <a href=\"" + sub.url + "\" target=\"_self\">\n\
                                " + sub.name + "</a></li>";
                        // alert(menu_html);
                    }

                    menu_html += "</ul></li>";
                } else {

                    menu_html += "<li class=\"\">\n\
                                             <a href=\"" + (item.url == "" ? "" : item.url) + "\" target=\"_self\" \n\
                                             style=\"color:white;font-size:10pt;\">" + item.name + "</a></li>";
                    //  alert(menu_html);
                }
            }
        }
//                      alert(menu_html);
        document.getElementById("menu_list").innerHTML = menu_html;
    }
}

//对菜单数据进行归并处理
function resortMenu(items) {
    if (items != null && items != undefined) {
        var i = 0;
        //归并的主要目的是将菜单和子菜单集中到一起
        var menu_hash = new HashMap();
        for (; i < items.length; i++) {
            var item = items[i];
            if (item.parentid == 0) {//第一级菜单
                menu_hash.put(item.id, item);
            } else {
                var menu = menu_hash.get(item.parentid);
                if (menu != null && menu != undefined) {
                    //如果已经存在子菜单，就将这个子菜单添加进去
                    if (menu.submenu != null && menu.submenu != undefined) {
                        menu.submenu.push(item);
                    } else {//如果还没有子菜单，就新建一个子菜单数组
                        var submenu = [];
                        submenu.push(item);
                        menu.submenu = submenu;
                    }
                }
            }
        }
    }
    //返回归并后的菜单
    return menu_hash;
}

function logout() {
    $.post('Logout', function (data) {

        if (data == "success") {
            location.href = "index.html";
        } else {
            alert("网络连接失败，请重新退出！");
        }
    });
}

function initPage() {
    //初始化界面菜单
    App.init(); // initlayout and core plugins
//                if (document.all) { window.resizeTo(screen.availWidth,screen.availHeight);
    //显示登录信息
    $.post("QuerySessionServlet", null, function (data) {
//                    alert(data);
        var mdata = JSON.parse(data);
        if (mdata.code == 0) {
            sessionStorage.setItem("session_key", mdata.session.userName);
            sessionStorage.setItem("name", mdata.session.name);
            sessionStorage.setItem("login_depart", mdata.session.departSID);
            sessionStorage.setItem("org_name", mdata.session.org_name);
            sessionStorage.setItem("login_account", mdata.session.userName);
//                        sessionStorage.setItem("createTime", mdata.obj.createTime);
            sessionStorage.setItem("role_id", mdata.session.roleIDS);
            $('#show_account').text(mdata.session.userName);
            //调整页面高度
            document.getElementById("content").style.height = (window.innerHeight * 0.92) + 'px';
            $("#content").scrollTop(window.innerHeight);
            //根据用户权限，初始化左侧的导航菜单
            initMenu();
        } else {
            $.alert({
                title: '提示!',
                content: "对不起，会话失效，请重新登录！",
            });
            //转跳到登录页面
            location.href = "index.html";
        }
    });
}

//导出表格tableId的数据
function exportData(tableId) {
    var $trs = $('#' + tableId).find('tr');
    var str = '';
    for (var i = 0; i < $trs.length; i++) {
        var $tds = $trs.eq(i).find('td,th');
        for (var j = 0; j < $tds.length; j++) {
            var tdValue = $tds.eq(j).text();
            if (tdValue === '没有找到匹配的记录') {
                $.alert({
                    title: '提示!',
                    content: '列表数据为空！'
                });
                return;
            } else {
                str += tdValue.replace(',', '，') + ',';
            }
        }
        str += '\n';
    }

    var aaaa = 'data:text/csv;charset=utf-8,\ufeff' + str;
    var link = document.createElement('a');
    link.setAttribute('href', aaaa);
    var filename = date2str(new Date());
    link.setAttribute('download', filename + '.csv');
    link.click();
}
//当前时间转字符串
function date2str(objTime) {
    var myDate = objTime.getFullYear() + '-' + (objTime.getMonth() + 1) + '-' + objTime.getDate();
    var myTime = objTime.getHours() + ':' + objTime.getMinutes() + ':' + objTime.getSeconds();
    return myDate + ' ' + myTime;
}

/**
 * 
 * @param {type} checkstr
 * @returns {Boolean}
 * 验证输入内容
 */
function checkspace(checkstr) {
    var str = '';
    for (i = 0; checkstr.length > i; i++) {
        str = str + ' ';
    }
    return (str == checkstr);
}

//判断一个字符串是否为json格式，返回true或者false
function isJSON(str) {
    if (typeof str == 'string') {
        try {
            var obj = JSON.parse(str);
            if (typeof obj == 'object' && obj) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            return false;
        }
    }
}

//保留两位小数 
//功能：将浮点数四舍五入，取小数点后2位 
function toDecimal(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return x;
    }
    f = Math.round(f * 100) / 100;
    return f;
}

//保留三位小数 
//功能：将浮点数四舍五入，取小数点后3位 
function toDecimal3(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return x;
    }
    f = Math.round(f * 1000) / 1000;
    return f;
}
/**
 *判断是否是数字
 *
 **/

function isRealNum(val) {
    // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
//    if (val === "" || val == null) {
//        return true;
//    }
    if (!isNaN(val)) {
        //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,
        //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
        return true;
    } else {
        return false;
    }
}


//控制只能输入小数点后2位
function clearNoNum(value) {
    value = value.replace(/[^\d.]/g, ""); //清除“数字”和“.”以外的字符   
    value = value.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的   
    value = value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
    value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数   
    if (value.indexOf(".") < 0 && value != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额  
        value = parseFloat(value);
    }
    return value;
}

//返回当前时间
function get_current_time() {
    var today = new Date();
    var month = today.getMonth() + 1;
    month = month < 10 ? '0' + month : month;
    var day = today.getDate() < 10 ? '0' + today.getDate() : today.getDate();
    var hours = today.getHours() < 10 ? '0' + today.getHours() : today.getHours();
    var mins = today.getMinutes() < 10 ? '0' + today.getMinutes() : today.getMinutes();
    var secs = today.getSeconds() < 10 ? '0' + today.getSeconds() : today.getSeconds();
    var now1 = today.getFullYear() + '-' + month + '-' + day + " " + hours + ":" + mins + ":" + secs;
    return now1;
}

//返回当前日期
function get_current_day() {
    var today = new Date();
    var month = today.getMonth() + 1;
    month = month < 10 ? '0' + month : month;
    var day = today.getDate() < 10 ? '0' + today.getDate() : today.getDate();
    var hours = today.getHours() < 10 ? '0' + today.getHours() : today.getHours();
    var mins = today.getMinutes() < 10 ? '0' + today.getMinutes() : today.getMinutes();
    var secs = today.getSeconds() < 10 ? '0' + today.getSeconds() : today.getSeconds();
    var now1 = today.getFullYear() + '年' + month + '月' + day + '日';
    return now1;
}