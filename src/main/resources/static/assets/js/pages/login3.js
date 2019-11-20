//------------- login3.js -------------//
$(document).ready(function() {

    /**
     * 定义公共常量
     * @type {string}
     */
    var errorMsg1="请输入登录账号";
    var errorMsg2="请输入登录密码";
    var errorMsg3="请输入验证码";
    var curCount=60; //计数常量
    /**
     * 获取父类节点
     * @type {jQuery}
     */
    var accountP=$("#account").parent();
    var passowrdP=$("#password").parent();
    var checkCodeP=$("#checkCode").parent();
    $("#checkCode").val("");

    //设置验证码倒计时显示值
   // $('#flusCode').html("注意("+curCount +")秒后刷新验证码");

    //启动定时器
    //window.setInterval(SetRemainTimes, 1000); //启动定时器

    var submitFlag=1; //登录校验标志
    var checkAccountFlag=1;//账号校验标志
    var checkCodeFlag=1//验证码校验标志

    //--------输入焦点失去实时监控-------------------------【start】-----------------------
    //校验输入账号
    $("#account").blur(function () {
        accountP.find(".msg").remove();
        var account=$('#account').val();//获取账号
        if(isNull(account)){
            accountP.append("<span class='msg onError' >" + errorMsg1 + "</span>");
        }else{
            //执行后台校验是否存在该账号
            checkAccountFunction(account);
        }
    });
    //校验输入密码
    $("#password").blur(function () {
        passowrdP.find(".msg").remove();
        var password=$('#password').val();//获取密码
        if(isNull(password)){
            passowrdP.append("<span class='msg onError' >" + errorMsg2 + "</span>");
        }
    });
    //校验验证码
    $("#checkCode").blur(function () {
        checkCodeP.find(".msg").remove();
        var checkCode=$('#checkCode').val();//获取输入验证码
        if(isNull(checkCode)){
            checkCodeP.append("<span class='msg onError' >" + errorMsg3 + "</span>");
        }else{
            //校验验证码是否正确
            checkCodeFunction(checkCode);
        }
    });

    //--------输入焦点失去实时监控-------------------------【end】-----------------------

    //--------登录按钮点击校验-------------------------【start】-------------------------
    $("#loginB").click(function () {
         accountP.find(".msg").remove();
         passowrdP.find(".msg").remove();
         checkCodeP.find(".msg").remove();

        var account=$('#account').val();//获取账号
        var password=$('#password').val();//获取密码
        var checkCode=$('#checkCode').val();//获取输入验证码

/*
        if(isNull(account)&&isNull(password)&&isNull(checkCode)){
            accountP.append("<span class='msg onError' >" + errorMsg1 + "</span>");
            passowrdP.append("<span class='msg onError' >" + errorMsg2 + "</span>");
            checkCodeP.append("<span class='msg onError' >" + errorMsg3 + "</span>");
            submitFlag=0;
        }else{
            submitFlag=1;
        }
        if(isNull(account)){
            accountP.append("<span class='msg onError' >" + errorMsg1 + "</span>");
            checkAccountFlag=0;
        }else{
            checkAccountFunction(account);
        }

         if(isNull(password)){
            passowrdP.append("<span class='msg onError' >" + errorMsg2 + "</span>");
            submitFlag=0;
        }

         if(isNull(checkCode)){
            checkCodeP.append("<span class='msg onError' >" + errorMsg3 + "</span>");
            checkCodeFlag=0;
        }else{
             checkCodeFunction(checkCode);
         }
*/

         //执行后台校验

        $.ajax({
            type:"POST",
            url:"/bgmSys/main",
            datatype:"JSON",
            contentType: "application/json",
            data:JSON.stringify({"account":account,"password":password,"checkCode":checkCode}),
            success:function (msg) {
               alert("获取的数据是："+msg.code+"=="+msg.returnMsg);
            }
        })


/*         if(submitFlag==1&&checkAccountFlag==1&&checkCodeFlag==1){
             accountP.find(".msg").remove();
             passowrdP.find(".msg").remove();
             checkCodeP.find(".msg").remove();
             $('#loginForm').submit();
         }*/

        //$('#loginForm').submit();

       // $('#loginForm').submit();


    });
    //--------登录按钮点击校验-------------------------【end】-------------------------

    //--------------------------------验证码获取----------------【start】--------------------

    $("#resetB").click(function () {
        $("#loginForm").find(".msg").remove();//清除所有错误数据
        changeCode();
    });

    $("#changeImg").click(function () {
        $("#checkCode").val("");
        changeCode();
    })

    //--------------------------------验证码获取----------------【end】--------------------
    //判空方法
    function isNull(str) {
        if ( str == ""||str.length==0 ) return true;
        var regu = "^[ ]+$";
        var re = new RegExp(regu);
        return re.test(str);
    }
    //调用图片生成方法
    function changeCode() {
        checkCodeP.find(".msg").remove();//清除验证码提示信息
        checkCodeFlag=0; //设置为未登录
        curCount=61;
        var src = "getVerifyCode?"+new Date().getTime(); //加时间戳，防止浏览器利用缓存
        $('.verifyCode').attr("src",src);                  //jQuery写法采用class选择器，jQuery的attr()函数，向Servlet发出请求
    }
    //设置定时器方法
/*    function SetRemainTimes() {
        //alert("当前的数值是1:"+curCount);
        if(curCount == 1) {
            curCount=60;
            changeCode();//刷新验证码
        } else {
            curCount--;
            $('#flusCode').html("注意("+curCount +")秒后刷新验证码");
        }
    }*/
    //校验账号是否存在
    function checkAccountFunction(str) {
        $.ajax({
            type:"POST",
            url:"/bgmSys/checkUser",
            datatype:"JSON",
            contentType: "application/json",
            data:JSON.stringify({"account":str}),
            success:function (msg) {
                if(msg.returnMsg=="True"){
                    checkAccountFlag=1;
                    accountP.append("<span class='msg onSuccess' >" + "√用户存在" + "</span>");
                }else{
                    checkAccountFlag=0;
                    accountP.append("<span class='msg onError' >" + "☹该用户不存在" + "</span>");
                }
            }
        })
    }
    //校验验证码是否输入正确
    function checkCodeFunction(str) {
        $.ajax({
            type:"POST",
            url:"/bgmSys/checkCode",
            datatype:"JSON",
            contentType: "application/json",
            data:JSON.stringify({"checkCode":str}),
            success:function (msg) {
                if(msg.returnMsg=="True"){
                    checkCodeFlag=1
                    checkCodeP.append("<span class='msg onSuccess' >" + "√验证码输入正确" + "</span>");
                }else{
                    checkCodeFlag=0;
                    checkCodeP.append("<span class='msg onError' >" + "☹验证码输入错误" + "</span>");
                    curCount=60;
                    var src = "getVerifyCode?"+new Date().getTime(); //加时间戳，防止浏览器利用缓存
                    $('.verifyCode').attr("src",src);                  //jQuery写法采用class选择器，jQuery的attr()函数，向Servlet发出请求
                }
            }
        })
    }

});