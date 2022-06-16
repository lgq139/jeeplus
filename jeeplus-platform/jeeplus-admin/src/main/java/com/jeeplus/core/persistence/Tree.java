package com.jeeplus.core.persistence;

import java.util.List;

public interface Tree<T> {

    String getId();

    String getParentId();

    void setChildren(List<T> children);

}
