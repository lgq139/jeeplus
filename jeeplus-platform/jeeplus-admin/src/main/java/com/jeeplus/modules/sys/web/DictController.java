/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.DictTreeValue;
import com.jeeplus.modules.sys.entity.DictType;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.entity.Industry;
import com.jeeplus.modules.sys.service.*;
import com.jeeplus.modules.sys.utils.DictUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 字典Controller
 * @author jeeplus
 * @version 2017-05-16
 */
@RestController
@RequestMapping("/sys/dict")
public class DictController extends BaseController {

	@Autowired
	private DictTypeService dictTypeService;
	@Autowired
	private DictValueService dictValueService;
	@Autowired
	private DictTreeValueService dictTreeValueService;
	@Autowired
	private IndustryService industryService;
	@Autowired
	private IndustryStructureService industryStructureService;

	@RequiresPermissions("sys:dict:list")
	@GetMapping("type/list")
	public AjaxJson data(DictType dictType, HttpServletRequest request, HttpServletResponse response, Model model) {
		String pageNo = request.getParameter("pageNo");
		String pageSize = request.getParameter("pageSize");
		LambdaQueryChainWrapper<DictType> wrapper = dictTypeService.lambdaQuery();
		wrapper.orderByDesc(DictType::getCreateDate);
		if (StringUtils.isNotBlank(dictType.getType())) {
			wrapper.like(DictType::getType, dictType.getType());
		}
		if (StringUtils.isNotBlank(dictType.getDescription())) {
			wrapper.like(DictType::getDescription, dictType.getDescription());
		}
		if (StringUtils.isNotBlank(dictType.getDictGroup())) {
			wrapper.eq(DictType::getDictGroup, dictType.getDictGroup());
		}
		if (StringUtils.isNotBlank(dictType.getLayout())) {
			wrapper.eq(DictType::getLayout, dictType.getLayout());
		}
		if (StringUtils.isNotBlank(dictType.getOpen())) {
			wrapper.eq(DictType::getOpen, dictType.getOpen());
		}
		IPage<DictType> page = wrapper.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
		return AjaxJson.success().put("page",page);
	}

	@RequiresPermissions("sys:dict:list")
	@GetMapping("type/allList")
	public AjaxJson allData(DictType dictType, HttpServletRequest request, HttpServletResponse response) {
		LambdaQueryChainWrapper<DictType> wrapper = dictTypeService.lambdaQuery();

		List<DictType> list = wrapper.list();
		return AjaxJson.success().put("data",list);
	}

	@RequiresPermissions(value={"sys:dict:view","sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(DictType dictType) {
		return AjaxJson.success().put("dictType", dictType);
	}

	@RequiresPermissions(value={"sys:dict:view","sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@GetMapping("queryDictValue")
	public AjaxJson queryDictValue(String dictValueId) {
		return AjaxJson.success().put("dictValue", dictValueService.getById(dictValueId));
	}


	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(DictType dictType) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		String errMsg = beanValidator(dictType);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		if (StringUtils.isBlank(dictType.getId())) {
			if (!Objects.isNull(dictTypeService.lambdaQuery().eq(DictType::getType, dictType.getType()).one())) {
				return AjaxJson.error("已经存在字典类型编码：" + dictType.getType());
			}
		}
		dictTypeService.saveOrUpdate(dictType);
		return AjaxJson.success("保存字典类型'" + dictType.getDescription() + "'成功！");
	}

	@PostMapping("refresh")
	public AjaxJson refreshCache() {
		dictTypeService.refreshCache();
		return AjaxJson.success("缓存刷新成功");
	}

	@RequiresPermissions("sys:dict:list")
	@GetMapping("getDictValue")
	public AjaxJson getDictValue(String dictTypeId) {
		if (StringUtils.isNotBlank(dictTypeId)) {
			return AjaxJson.success().put("data", dictValueService.treeData(dictTypeId));
		}
		return AjaxJson.success().put("data", Collections.emptyList());
	}

	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("saveDictValue")
	public AjaxJson saveDictValue(DictValue dictValue) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		String errMsg = beanValidator(dictValue);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		dictValueService.saveOrUpdate(dictValue);
		return AjaxJson.success("保存键值'" + dictValue.getLabel() + "'成功！");
	}


	@RequiresPermissions("sys:dict:edit")
	@PostMapping("deleteDictValue")
	public AjaxJson deleteDictValue(String ids) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		if (StringUtils.isNotBlank(ids)) {
			dictValueService.removeByIds(Arrays.asList(ids.split(",")));
		}
		return AjaxJson.success("删除键值成功！");
	}

	/**
	 * 批量删除
	 */
	@RequiresPermissions("sys:dict:del")
	@PostMapping("delete")
	public AjaxJson delete(String ids) {
		if(jeePlusProperites.isDemoMode()){
			return AjaxJson.error("演示模式，不允许操作！");
		}
		if (StringUtils.isNotBlank(ids)) {
			dictTypeService.removeByIds(Arrays.asList(ids.split(",")));
		}
		return AjaxJson.success("删除字典成功！");
	}



	@GetMapping("listData")
	public AjaxJson listData(@RequestParam(required=false) String type) {
		return AjaxJson.success().put("list", dictTypeService.lambdaQuery().eq(DictType::getType, type).list());
	}

	@GetMapping("getDictMap")
	public AjaxJson getDictMap() {
		return AjaxJson.success().put("dictList", DictUtils.getDictMap());
	}

	@RequiresPermissions("sys:dict:list")
	@GetMapping("/getDictTreeValue")
	public AjaxJson listDictTreeValue(String dictTypeId) {
		List<DictTreeValue> list = dictTreeValueService.treeData(dictTypeId);
		return AjaxJson.success().put("data", list);
	}

	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("/saveDictTreeValue")
	public AjaxJson saveDictTreeValue(DictTreeValue dictTreeValue) {
		String errMsg = beanValidator(dictTreeValue);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		dictTreeValueService.saveOrUpdate(dictTreeValue);
		return AjaxJson.success("保存键值'" + dictTreeValue.getLabel() + "'成功！");
	}

	@RequiresPermissions("sys:dict:del")
	@PostMapping("/deleteDictTreeValue")
	public AjaxJson deleteDictTreeValue(String ids) {
		if (StringUtils.isNotBlank(ids)) {
			for (String id : ids.split(",")) {
				DictTreeValue dictTreeValue = dictTreeValueService.getById(id);
				if (dictTreeValue != null) {
					dictTreeValueService.lambdaUpdate()
							.eq(DictTreeValue::getDictTypeId, dictTreeValue.getDictTypeId())
							.likeRight(DictTreeValue::getParentIds, dictTreeValue.getParentIds())
							.remove();
				}
			}
		}
		return AjaxJson.success("删除字典成功！");
	}

	@RequiresPermissions("sys:dict:list")
	@GetMapping("industry/list")
	public AjaxJson listIndustry() {
		return AjaxJson.success().put("data", industryService.treeData());
	}

	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("industry/save")
	public AjaxJson saveIndustry(Industry industry) {
		industryService.saveOrUpdate(industry);
		return AjaxJson.success("保存行业分类数据成功");
	}

	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("industry/refresh")
	public AjaxJson refreshIndustry() {
		industryService.refreshCache();
		return AjaxJson.success("刷新行业分类缓存数据成功");
	}

	@RequiresPermissions("sys:dict:list")
	@GetMapping("industryStructure/list")
	public AjaxJson listIndustryStructure() {
		return AjaxJson.success().put("data", industryStructureService.treeData());
	}

	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@PostMapping("industryStructure/refresh")
	public AjaxJson refreshIndustryStructure() {
		industryStructureService.refreshCache();
		return AjaxJson.success("刷新产业结构调整指导目录缓存数据成功");
	}

}
