package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.sys.entity.DataRule;
import com.jeeplus.modules.sys.entity.RoleDataRuleRelation;
import com.jeeplus.modules.sys.mapper.DataRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据权限Service
 * @author lgf
 * @version 2017-04-02
 */
@Service
public class DataRuleService extends ServiceImpl<DataRuleMapper, DataRule> {

	@Autowired
	private DataRuleMapper dataRuleMapper;
	@Autowired
	private RoleDataRuleRelationService roleDataRuleRelationService;

	public DataRule get(String id) {
		return super.getById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(DataRule dataRule) {
		//解除数据权限角色关联
		roleDataRuleRelationService.lambdaUpdate()
				.in(RoleDataRuleRelation::getDataruleId, dataRule.getId())
				.remove();
		super.removeById(dataRule);
	}

}
