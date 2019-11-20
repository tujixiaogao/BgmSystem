package cn.bgm.index.util;

/**
 * 登录验证返回
 */
public class LoginResult {

    private String retrunCode=null;

    private String returnMsg=null;

    public LoginResult() {
    }

    public LoginResult(String retrunCode, String returnMsg) {
        this.retrunCode = retrunCode;
        this.returnMsg = returnMsg;
    }

    public String getRetrunCode() {
        return retrunCode;
    }

    public void setRetrunCode(String retrunCode) {
        this.retrunCode = retrunCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}
