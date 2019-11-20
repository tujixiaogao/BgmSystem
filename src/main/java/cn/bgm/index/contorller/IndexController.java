package cn.bgm.index.contorller;

import cn.bgm.index.common.Constant;
import cn.bgm.index.util.CookieUtil;
import cn.bgm.index.util.RedisUtil;
import cn.bgm.index.util.VerifyCode;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/bgmSys")
public class IndexController {



    private static  String getCheckCode=null;//获取checkCode验证码

    private static String  verid=null; //保存到redis的验证码的key

    @Autowired
    RedisUtil redisUtil;

    /**
     * 跳转登录到主页面
     * @return
     */
    @RequestMapping(value = "/main",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> thymeleaf(HttpServletRequest request,
                            HttpServletResponse response,
                            @RequestParam("account") String account,
                            @RequestParam("password") String password,
                            @RequestParam("checkCode") String checkCode ){

        System.out.println("-------------执行登录校验【开始】...index----------------------");

        //执行登录后台验证
        Map<String, Object> map = new HashMap<>();
        map.put("code",Constant.SUCCESS_CODE);
        map.put("returnMsg",Constant.RETURN_MSG_1); //返回成功

        if(StringUtils.isEmpty(account)){
            map.put("code",Constant.ACCOUNT_NULL);
            map.put("returnMsg","用户名为空！");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("code",Constant.PASSWORD_NULL);
            map.put("returnMsg","请输入密码！");
            return map;
        }

        if(StringUtils.isEmpty(checkCode)){
            map.put("code",Constant.CHECKCODE_NULL);
            map.put("returnMsg","请输入验证码！");
            return map;
        }

        //执行验证码是否超时输入是否正确验证
        String uid = CookieUtil.getUid(request, "verid"); //从cookie中获取验证码key
        String getVcode = (String) redisUtil.get(uid);  //从redis获取验证码
        redisUtil.del(new String[]{verid}); //把验证码从redis中删除掉
        if(StringUtils.isEmpty(uid)||StringUtils.isEmpty(getVcode)){
            map.put("code",Constant.CHECKCODE_TIMEOUT);
            map.put("returnMsg","验证码超时，请重新刷新验证码！");
            return map;
        }

        if(!checkCode.equalsIgnoreCase(getVcode)){
            map.put("code",Constant.CHECKCODE_ERROR);
            map.put("returnMsg","验证码输入错误！");
            return map;
        }

        //执行数据库查询校验 start---------


        //执行数据库查询校验 end---------
        System.out.println("-------------执行登录校验【结束】...index----------------------");
        //跳转到主页页面
/*        RequestDispatcher dispatcher = request.getRequestDispatcher("/templates/test.html");
        try {
            dispatcher .forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return map;
    }

    /**
     * 登录页面跳转
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/login")
    public String goLogin(Model model,HttpServletRequest request,HttpServletResponse response){
        System.out.println("。。。跳转登录页面。。。");
        redisUtil.set("name","gaozhulin1");
        String name = (String) redisUtil.get("name");
        System.out.println("获取的参数是:"+name);
        return "login";
    }


    /**
     * ajax异步校验用户是否存在
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/checkUser",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Map<String, Object> checkAccout(@RequestBody JSONObject params){

        Map<String, Object> result = new HashMap<String, Object>();
        //System.out.println("获取的账号是" +params.get("account"));
        //执行后台查询逻辑
        String account =  params.get("account").toString();
        if("User1".equals(account)){
            result.put("returnMsg","True");
        }else
            result.put("returnMsg","False");


        return result;
    }

    /**
     * 异步校验用户输入验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/checkCode",method = RequestMethod.POST)
    public Map<String, Object> checkCode(HttpServletRequest request,@RequestBody JSONObject params){

        String checkCode = params.get("checkCode").toString();

        System.out.println("获取的参数是checkCode：==============="+checkCode);

        Map<String, Object> result = new HashMap<String, Object>();

        String getVerid= CookieUtil.getUid(request,"verid"); //从cookie中获取验证码key id
        String vcode = (String) redisUtil.get(getVerid); //从redis获取验证码

        System.out.println("获取的验证码参数是【2】getCheckCode=:"+vcode);



        if(vcode.equalsIgnoreCase(checkCode)){
            result.put("returnMsg","True");
        }else
            result.put("returnMsg","False");
        return result;
    }


    /* 获取验证码图片*/
    @RequestMapping(value="/getVerifyCode",method = RequestMethod.GET)
    public void getVerificationCode(HttpServletResponse response, HttpServletRequest request) {
        System.out.println("...======================================================...!");
        try {
            int width=200;
            int height=69;
            BufferedImage verifyImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            //生成对应宽高的初始图片
            String randomText = VerifyCode.drawRandomText(width,height,verifyImg);
            getCheckCode=randomText; //设置验证码参数
            System.out.println("获取的验证码参数是1:"+getCheckCode);
            verid = UUID.randomUUID().toString().replaceAll("-", "");
            System.out.println("生成的UUID参数是："+verid);

            Cookie cookie = new Cookie("verid", verid); //将UUID存入cookie
            response.addCookie(cookie);
            redisUtil.set(verid,getCheckCode,60);//将验证码存入redis key：verid value=验证码

            //单独的一个类方法，出于代码复用考虑，进行了封装。
            //功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符
            request.getSession().setAttribute("verifyCode", randomText);
            response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别
            OutputStream os = response.getOutputStream(); //获取文件输出流
            ImageIO.write(verifyImg,"png",os);//输出图片流
            os.flush();
            os.close();//关闭流
        } catch (IOException e) {
          //  this.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }


}
