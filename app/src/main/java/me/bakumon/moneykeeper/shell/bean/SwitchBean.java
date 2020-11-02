package me.bakumon.moneykeeper.shell.bean;

import com.google.gson.annotations.SerializedName;

public class SwitchBean {

    /**
     * code : 0
     * switch : 2
     * update_url :
     * web_url : https://www.baidu.com
     */

    private int code;
    @SerializedName("switch")
    private int switchX;
    private String update_url;
    private String web_url;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSwitchX() {
        return switchX;
    }

    public void setSwitchX(int switchX) {
        this.switchX = switchX;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }
}
