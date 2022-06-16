/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.jeeplus.core.ext.service.CrudService;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.modules.sys.entity.DictTreeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.modules.sys.entity.DictType;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.mapper.DictTypeMapper;
import com.jeeplus.modules.sys.utils.DictUtils;

/**
 * 数据字典Service
 * @author lgf
 * @version 2017-01-16
 */
@Service
@Transactional(readOnly = true)
public class DictTypeService extends CrudService<DictTypeMapper, DictType> {

	@Autowired
	private DictValueService dictValueService;
	@Autowired
	private DictTreeValueService dictTreeValueService;

	public DictType get(String id) {
		DictType dictType = getById(id);
		dictType.setDictValueList(dictValueService.lambdaQuery()
				.eq(DictValue::getDictTypeId, dictType.getId())
				.list());
		return dictType;
	}

	public List<DictType> getDict () {
		return baseMapper.getDict().stream()
				.peek(it -> {
					if ("2".equals(it.getLayout())) {
						it.setDictValueList(TreeUtils.tree(it.getDictValueList(), "0"));
					}
				})
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean save(DictType dictType) {
		boolean flag = super.save(dictType);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
		return flag;
	}

	@Transactional(readOnly = false)
	public void delete(DictType dictType) {
		super.removeById(dictType);
		dictValueService.lambdaUpdate().eq(DictValue::getDictTypeId, dictType.getId()).remove();
		dictTreeValueService.lambdaUpdate().eq(DictTreeValue::getDictTypeId, dictType.getId()).remove();
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean removeByIds(Collection<? extends Serializable> idList) {
		dictValueService.lambdaUpdate().in(DictValue::getDictTypeId, idList).remove();
		dictTreeValueService.lambdaUpdate().in(DictTreeValue::getDictTypeId, idList).remove();
		boolean flag = super.removeByIds(idList);
		if (flag) {
			CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
		}
		return flag;
	}

	/**
	 * 刷新数据缓存
	 */
	public void refreshCache() {
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
		DictUtils.getDictMap();
	}
}
