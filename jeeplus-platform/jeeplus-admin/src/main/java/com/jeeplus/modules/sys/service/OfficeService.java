package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.mapper.OfficeMapper;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构Service
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends ServiceImpl<OfficeMapper, Office> {


	public Office get(String id) {

		return getById(id);
	}

	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll){
		if (isAll != null && isAll){
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList();
		}
	}

	public List<Office> findList(Office office){
		return lambdaQuery().like(StringUtils.isNotBlank(office.getParentIds()),
				Office::getParentIds, office.getParentIds())
				.list();
	}

	public Office getByCode(String code){
		return lambdaQuery().eq(Office::getCode, code).one();
	}

	public List<Office> getChildren(String parentId){
		return lambdaQuery().eq(Office::getParentId, parentId)
				.orderByAsc(Office::getSort)
				.list();
	}

	@Override
    @Transactional(rollbackFor = Exception.class)
	public boolean save(Office office) {
		boolean res = super.save(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
		return res;
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(Office office) {
		super.removeById(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

}
