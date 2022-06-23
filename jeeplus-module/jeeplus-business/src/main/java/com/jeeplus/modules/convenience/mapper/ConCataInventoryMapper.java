package com.jeeplus.modules.convenience.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.convenience.entity.ConCataInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ConCataInventoryMapper extends BaseMapper<ConCataInventory> {

    @Select("SELECT base_code FROM con_cata_inventory WHERE org_code = #{orgCode} ")
    List<Object> getBaseCodeList(@Param("orgCode")String orgCode);
}
