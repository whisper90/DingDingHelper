package com.ucmap.dingdinghelper.entity;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class AccountEntity {

    private String account = "";
    private String password = "";

    private String time = "8:45";

    public String getnTime() {
        return nTime;
    }

    public void setnTime(String nTime) {
        this.nTime = nTime;
    }

    private String nTime = "18:45";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    @Override
    public String toString() {
        return "AccountEntity{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", time='" + time + '\'' +
                ", nTime='" + nTime + '\'' +
                '}';
    }
}
