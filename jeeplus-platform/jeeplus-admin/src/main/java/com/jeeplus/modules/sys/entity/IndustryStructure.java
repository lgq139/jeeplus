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
 * 产业结构调整指导目录
 */
@TableName("sys_industry_structure")
public class IndustryStructure implements Tree<IndustryStructure>, Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String parentId;
    private String name;
    /** 1:第一类 鼓励类  2：第二类 限制类 */
    private String type;
    private Integer sort;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private List<IndustryStructure> children = new ArrayList<>();

    public IndustryStructure() {
        this.sort = 10;
    }

    @Override
    public String getId() {
        return id;
    }

    public IndustryStructure setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    public IndustryStructure setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getName() {
        return name;
    }

    public IndustryStructure setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getSort() {
        return sort;
    }

    public IndustryStructure setSort(Integer sort) {
        this.sort = sort;
        return this;
    }

    public String getType() {
        return type;
    }

    public IndustryStructure setType(String type) {
        this.type = type;
        return this;
    }

    public List<IndustryStructure> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<IndustryStructure> children) {
        this.children = children;
    }
}
