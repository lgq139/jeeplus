package com.jeeplus.modules.sys.entity.enums;

public enum  LogType {
    LOGIN("1", "登录日志"),
    ACCESS("2", "访问日志"),
    EXCEPTION("3", "异常日志");

    private String type;
    private String label;

    private LogType(String type, String label){
        this.type = type;
        this.label = label;
    }


    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }
}
