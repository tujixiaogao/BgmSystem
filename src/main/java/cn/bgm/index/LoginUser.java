package cn.bgm.index;

import java.io.Serializable;

/**
 * 登录账号
 */
public class LoginUser implements Serializable {

    private String account;
    private String password;
    private String checkCode;

    public LoginUser(String account, String password, String checkCode) {
        this.account = account;
        this.password = password;
        this.checkCode = checkCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
