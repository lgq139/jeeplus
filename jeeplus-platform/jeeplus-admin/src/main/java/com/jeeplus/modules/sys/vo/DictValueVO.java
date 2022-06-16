package com.jeeplus.modules.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeeplus.modules.sys.entity.DictValue;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DictValueVO implements Serializable {
    private String label;
    private String value;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DictValueVO> children;

    public DictValueVO() {

    }

    public DictValueVO(DictValue dictValue) {
        this.label = dictValue.getLabel();
        this.value = dictValue.getValue();
        this.children = transformDictTreeValue(dictValue.getChildren());
    }

    public String getLabel() {
        return label;
    }

    public DictValueVO setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getValue() {
        return value;
    }

    public DictValueVO setValue(String value) {
        this.value = value;
        return this;
    }

    public List<DictValueVO> getChildren() {
        return children;
    }

    public DictValueVO setChildren(List<DictValueVO> children) {
        this.children = children;
        return this;
    }

    public static List<DictValueVO> transformDictTreeValue(List<DictValue> dictValues) {
        if (CollectionUtils.isNotEmpty(dictValues)) {
            List<DictValueVO> list = new ArrayList<>();
            for(DictValue dictValue : dictValues) {
                list.add(new DictValueVO().setLabel(dictValue.getLabel())
                        .setValue(dictValue.getValue())
                        .setChildren(transformDictTreeValue(dictValue.getChildren())));
            }
            return list;
        }
        return null;
    }
}
