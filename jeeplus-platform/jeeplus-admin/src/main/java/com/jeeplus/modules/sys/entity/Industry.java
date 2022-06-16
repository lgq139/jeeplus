package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeeplus.core.persistence.Tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 国民经济行业分类 2017
 */
@TableName("sys_industry")
public class Industry implements Tree<Industry>, Serializable {

    @TableId(type = IdType.INPUT)
    private String id;

    private String parentId;

    private String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private List<Industry> children = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    public Industry setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public Industry setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Industry setName(String name) {
        this.name = name;
        return this;
    }

    public List<Industry> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<Industry> children) {
        this.children = children;
    }
}
