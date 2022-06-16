package com.jeeplus.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jeeplus.core.ext.persistence.TreeEntity;

/**
 * 树形字典Entity
 *
 * @author FEAR
 * @version 2021-08-20
 */
@TableName("sys_dict_tree_value")
public class DictTreeValue extends TreeEntity<DictTreeValue> {

    private static final long serialVersionUID = 1L;
    /** 类型 */
    private String dictType;
    /** 类型ID */
    private String dictTypeId;
    /** 标签名 */
    private String label;
    /** 键值 */
    private String value;


    public DictTreeValue() {
        super();
    }

    public DictTreeValue(String id) {
        super(id);
    }

    public String getDictTypeId() {
        return dictTypeId;
    }

    public void setDictTypeId(String dictTypeId) {
        this.dictTypeId = dictTypeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public DictTreeValue getParent() {
        return parent;
    }

    @Override
    public void setParent(DictTreeValue parent) {
        this.parent = parent;
    }
}
