package com.jeeplus.modules.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MenuMapper extends BaseMapper<Menu> {

	Menu get(String id);

	List<Menu> getChildren(String id);

	List<Menu> findAllList();

	List<Menu> findByUserId(Menu menu);

	List<Menu> findByParentIdsLike(String parentIds);

	List<Menu> findAllDataRuleList(Menu menu);

}
