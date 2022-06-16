package com.jeeplus.modules.sys.vo;

import com.jeeplus.modules.sys.entity.DictType;

import java.io.Serializable;

public class DictTypeVO implements Serializable {
    private String type;
    private String description;

    public DictTypeVO() {
    }

    public DictTypeVO(DictType dictType) {
        this.type = dictType.getType();
        this.description = dictType.getDescription();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
