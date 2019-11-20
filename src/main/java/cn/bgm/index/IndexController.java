package cn.bgm.index;

import cn.bgm.index.util.VerifyCode;
import net.sf.json.JSONObject;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/bgmSys")
public class IndexController {



    private static  String getCheckCode=null;//获取checkCode

    /**
     * 跳转登录到主页面
     * @return
     */
    @RequestMapping("/main")
    //public String thymeleaf(String account,String password,String checkCode,Model model){
    public String thymeleaf(@RequestBody String params ){

        System.out.println("-------------index----------------------");
        System.out.println("获取的参数是：");


        return "test";
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
    public Map<String, Object> checkCode(@RequestBody JSONObject params){

        String checkCode = params.get("checkCode").toString();

        System.out.println("获取的参数是checkCode：==============="+checkCode);

        Map<String, Object> result = new HashMap<String, Object>();

        System.out.println("获取的验证码参数是【2】getCheckCode=:"+getCheckCode);

        if(getCheckCode.equalsIgnoreCase(checkCode)){
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
