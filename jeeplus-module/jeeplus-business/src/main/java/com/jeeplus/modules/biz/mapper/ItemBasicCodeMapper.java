package com.jeeplus.modules.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.jeeplus.modules.biz.entity.ItemBasicCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 四级四同目录MAPPER接口
 * @author FEAR
 * @version 2021-11-09
 */
@Mapper
@Repository
public interface ItemBasicCodeMapper extends BaseMapper<ItemBasicCode> {

    /**
     * 获取包含材料信息的事项目录列表
     *
     * @param ew 查询条件
     * @return
     */
    @Deprecated
    List<ItemBasicCode> findListOfItemMaterial(@Param(Constants.WRAPPER) Wrapper<ItemBasicCode> ew);

    /**
     * 获取包含标准事项信息的事项目录列表
     * @param ew 查询条件
     * @return
     */
    List<ItemBasicCode> selectBasicCodeStandardItem(@Param(Constants.WRAPPER) Wrapper<ItemBasicCode> ew);

}
