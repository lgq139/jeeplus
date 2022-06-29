package com.jeeplus.modules.item.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.item.common.entity.ItemInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ItemInfoMapper extends BaseMapper<ItemInfo> {

}
