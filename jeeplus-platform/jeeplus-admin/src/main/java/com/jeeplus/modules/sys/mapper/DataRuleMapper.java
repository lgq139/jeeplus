package com.jeeplus.modules.sys.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jeeplus.modules.sys.entity.DataRule;
import com.jeeplus.modules.sys.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;;

/**
 * 数据权限MAPPER接口
 */
@Mapper
@Repository
public interface DataRuleMapper extends BaseMapper<DataRule> {

	void deleteRoleDataRule(DataRule dataRule);

	List<DataRule> findByUserId(User user);
}
