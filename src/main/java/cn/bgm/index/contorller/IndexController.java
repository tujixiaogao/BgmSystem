package cn.bgm.index.contorller;

import cn.bgm.index.common.Constant;
import cn.bgm.index.util.CookieUtil;
import cn.bgm.index.util.RedisUtil;
import cn.bgm.index.util.VerifyCode;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
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

    //加入日志记录

    private final static Logger logger =LoggerFactory.getLogger(IndexController.class);

    /**
     * 主页登录跳转
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/main")
    public String goMain(Model model,HttpServletRequest request,HttpServletResponse response){
        //logger.debug("开始获取员工[{}] [{}]年基本薪资",employee,year);
        logger.info("===============登录成功执行跳转index主页面========================");

        return "test";
    }

    /**
     * 访问登录页面跳转
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/login")
    public String goLogin(Model model,HttpServletRequest request,HttpServletResponse response){
        logger.info("====================访问登录页面【start】===========================");
        return "login";
    }
    /**
     * 登录后台校验
     * @return
     *     //@ResponseBody
     */
    @RequestMapping(value = "/loginCheck",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> thymeleaf(@RequestBody Map<String,String> params,
                                         HttpServletRequest request,
                                         HttpServletResponse response){

        Boolean loginFlag=true; //登录验证标志

        String account=params.get("account").toString();
        String password=params.get("password").toString();
        String checkCode=params.get("checkCode").toString();
        logger.info("==========================执行登录验证开始=======【start】============================");
        //执行登录后台验证
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isEmpty(account)){
            map.put("code2",Constant.ACCOUNT_NULL);
            map.put("returnMsg2","用户名为空！");
            loginFlag=false;
        }

        if(StringUtils.isEmpty(password)){
            map.put("code3",Constant.PASSWORD_NULL);
            map.put("returnMsg3","请输入密码！");
            loginFlag=false;
        }

        if(StringUtils.isEmpty(checkCode)){
            map.put("code4",Constant.CHECKCODE_NULL);
            map.put("returnMsg4","请输入验证码！");
            loginFlag=false;
        }else{
            //判断是否超时或者输入错误
            String uid = CookieUtil.getUid(request, "verid"); //从cookie中获取验证码key
            String getVcode = (String) redisUtil.get(uid);  //从redis获取验证码
            redisUtil.del(new String[]{verid}); //把验证码从redis中删除掉
            if(StringUtils.isEmpty(uid)||StringUtils.isEmpty(getVcode)){
                map.put("code5",Constant.CHECKCODE_TIMEOUT);
                map.put("returnMsg5","验证码超时，请重新刷新验证码！");
                loginFlag=false;
            }else{
                if(!checkCode.equalsIgnoreCase(getVcode)){
                    map.put("code6",Constant.CHECKCODE_ERROR);
                    map.put("returnMsg6","验证码输入错误！");
                    loginFlag=false;
                }
            }
        }

        //执行数据库查询校验 start---------


        //执行数据库查询校验 end---------

        logger.info("=======登录校验返回信息为：{}。=====================",map);

        if(loginFlag){
            map.put("code1",Constant.SUCCESS_CODE); //返回成功代码
        }
        logger.info("==========================执行登录验证结束=======【end】============================");
        return map;
    }




    /**
     * ajax异步校验用户是否存在
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/checkUser",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Map<String, Object> checkAccout(@RequestBody JSONObject params){

        Map<String, Object> result = new HashMap<String, Object>();
        String account =  params.get("account").toString();
        logger.debug("==============异步校验当前输入用户：{}=====================",account);
        //执行后台查询逻辑
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
        Map<String, Object> result = null;
        try {
            //获取输入验证码
            String checkCode = params.get("checkCode").toString();
            logger.debug("==============异步校验当前输入验证码：{}=======================",checkCode);
            //设置返回参数
            result = new HashMap<String, Object>();
            //从cookie中获取验证码的key id
            String getVerid= CookieUtil.getUid(request,"verid");
            //获取验证码
            String vcode = (String) redisUtil.get(getVerid); //从redis获取验证码
            logger.debug("==============异步校验获取redise验证码：{}=======================",vcode);
            //获取验证码为空判断为超时
            if(StringUtils.isEmpty(vcode)){
                result.put("returnMsg","timeOut");
                return result;
            }else{
                //执行验证码数据校验
                if(vcode.equalsIgnoreCase(checkCode)){
                    result.put("returnMsg","True");
                }else
                    result.put("returnMsg","False");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /* 获取验证码图片*/
    @RequestMapping(value="/getVerifyCode",method = RequestMethod.GET)
    public void getVerificationCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        OutputStream os=null;
        try {
            int width=200;
            int height=69;
            BufferedImage verifyImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            //生成对应宽高的初始图片
            String randomText = VerifyCode.drawRandomText(width,height,verifyImg);
            //设置验证码参数
            getCheckCode=randomText;
            logger.debug("==============验证码生成参数：{}=======================",getCheckCode);
            verid = UUID.randomUUID().toString().replaceAll("-", "");
            logger.debug("==============验证码UUID成参数：{}=======================",verid);
            //将UUID存入cookie
            Cookie cookie = new Cookie("verid", verid);
            response.addCookie(cookie);
            redisUtil.set(verid,getCheckCode,10);//将验证码存入redis key：verid value=验证码

            //单独的一个类方法，出于代码复用考虑，进行了封装。
            //功能是生成验证码字符并加上噪点，干扰线，返回值为验证码字符
            request.getSession().setAttribute("verifyCode", randomText);
            response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别
             os = response.getOutputStream(); //获取文件输出流
            ImageIO.write(verifyImg,"png",os);//输出图片流

        } catch (IOException e) {
             this.logger.error("++++++++++++++++++++++++验证码生错错误：{}+++++++++++++++++++++",e.getMessage());
            e.printStackTrace();
        }finally {
            try {
                os.flush();
                os.close();//关闭流
            } catch (IOException e) {
                this.logger.error("++++++++++++++++++++++++验证码输出流失败：{}+++++++++++++++++++++",e.getMessage());
            }

        }
    }


}
