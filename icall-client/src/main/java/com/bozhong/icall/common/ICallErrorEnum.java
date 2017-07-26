package com.bozhong.icall.common;

import com.alibaba.fastjson.JSON;

/**
 * Created by xiezg@317hu.com on 2017/4/25 0025.
 */
public enum ICallErrorEnum {

    E10001("E10001", "Login Failure Public Private Key Expire", "用户密码公钥私钥加密策略过期，请重新进入登录页面！"),
    E10002("E10002", "Group Empty!", "分组为空！"),
    E10003("E10003", "Request Url Path Not Matched!", "请求路径不匹配！"),
    E10004("E10004", "Request Parameter Not Matched!", "请求参数不匹配！");

    private String error;

    private String msg;

    private String cnMsg;

    ICallErrorEnum(String error, String msg, String cnMsg) {
        this.error = error;
        this.msg = msg;
        this.cnMsg = cnMsg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCnMsg() {
        return cnMsg;
    }

    public void setCnMsg(String cnMsg) {
        this.cnMsg = cnMsg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}