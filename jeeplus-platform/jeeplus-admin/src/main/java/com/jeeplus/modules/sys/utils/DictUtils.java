package com.jeeplus.modules.sys.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.jeeplus.modules.sys.entity.Industry;
import com.jeeplus.modules.sys.entity.IndustryStructure;
import com.jeeplus.modules.sys.service.IndustryService;
import com.jeeplus.modules.sys.service.IndustryStructureService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.modules.sys.entity.DictType;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.service.DictTypeService;

/**
 * 字典工具类
 */
public class DictUtils {


	/** 字典数据缓存 */
	public static final String CACHE_DICT_MAP = "dictMap";
	/** 国民经济行业分类缓存 */
	public static final String CACHE_INDUSTRY_MAP = "industryMap";
	/** 产业结构调整指导目录缓存 */
	public static final String CACHE_INDUSTRY_STRUCTURE_MAP = "industryStructureMap";

	public static String getDictLabel(String value, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
			for (DictValue dictValue : getDictList(type)){
				if (value.equals(dictValue.getValue())){
					return dictValue.getLabel();
				}
			}
		}
		return defaultLabel;
	}

	public static String getDictLabels(String values, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)){
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")){
				valueList.add(getDictLabel(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	public static String getDictValue(String label, String type, String defaultLabel){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)){
			for (DictValue dictValue : getDictList(type)){
				if (label.equals(dictValue.getLabel())){
					return dictValue.getValue();
				}
			}
		}
		return defaultLabel;
	}

	public static String getDictValues(String labels, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(labels)){
			List<String> labelList = Lists.newArrayList();
			for (String label : StringUtils.split(labels, ",")){
				labelList.add(getDictValue(label, type, defaultValue));
			}
			return StringUtils.join(labelList, ",");
		}
		return defaultValue;
	}

	public static List<DictValue> getDictList(String type){
		@SuppressWarnings("unchecked")
		Map<String, List<DictValue>> dictMap = (Map<String, List<DictValue>>)CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap==null){
			dictMap = Maps.newHashMap();
			for (DictType dictType : SpringUtil.getBean(DictTypeService.class).list()){
				List<DictValue> dictList = dictMap.get(dictType.getType());
				dictType = SpringUtil.getBean(DictTypeService.class).get(dictType.getId());
				if (dictList != null){
					dictList.addAll(dictType.getDictValueList());
				}else{
					dictMap.put(dictType.getType(), Lists.newArrayList(dictType.getDictValueList()));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		List<DictValue> dictList = dictMap.get(type);
		if (dictList == null){
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	public static Map<String, List<DictValue>> getDictMap() {
		@SuppressWarnings("unchecked")
		Map<String, List<DictValue>> dictMap = (Map<String, List<DictValue>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			List<DictType> dict = SpringUtil.getBean(DictTypeService.class).getDict();
			for (DictType dictType : dict) {
				dictMap.put(dictType.getType(), dictType.getDictValueList());
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}

		return dictMap;
	}

	/**
	 * 获取开放型数据字典数据缓存Map
	 *
	 * @return
	 */
	public static Map<String, List<DictValue>> getOpenDictMap() {
		List<String> dictTypeList = SpringUtil.getBean(DictTypeService.class).lambdaQuery()
				.eq(DictType::getOpen, "1")
				.list()
				.stream()
				.map(DictType::getType)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(dictTypeList)) {
			return MapUtil.newHashMap();
		}
		@SuppressWarnings("unchecked")
		Map<String, List<DictValue>> dictMap = (Map<String, List<DictValue>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			List<DictType> dict = SpringUtil.getBean(DictTypeService.class).getDict();
			for (DictType dictType : dict) {
				dictMap.put(dictType.getType(), dictType.getDictValueList());
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		return MapUtil.filter(dictMap, dictTypeList.toArray(new String[0]));
	}

	/**
	 * 获取国民经济行业分类
	 *
	 */
	@SuppressWarnings("unchecked")
	public static List<Industry> getIndustryMap() {
		List<Industry> industryList = (List<Industry>) CacheUtils.get(CACHE_INDUSTRY_MAP);
		if (CollectionUtils.isEmpty(industryList)) {
			industryList = SpringUtil.getBean(IndustryService.class).lambdaQuery().orderByAsc(Industry::getId).list();
			CacheUtils.put(CACHE_INDUSTRY_MAP, industryList);
		}
		return industryList;
	}

	public static List<IndustryStructure> getIndustryStructureMap() {
		List<IndustryStructure> industryStructureList = (List<IndustryStructure>) CacheUtils.get(CACHE_INDUSTRY_STRUCTURE_MAP);
		if (CollectionUtils.isEmpty(industryStructureList)) {
			industryStructureList = SpringUtil.getBean(IndustryStructureService.class).lambdaQuery()
					.orderByAsc(IndustryStructure::getId)
					.list();
			CacheUtils.put(CACHE_INDUSTRY_STRUCTURE_MAP, industryStructureList);
		}
		return industryStructureList;
	}

	/*
	 * 反射根据对象和属性名获取属性值
	 */
	public static Object getValue(Object obj, String filed) {
		try {
			Class clazz = obj.getClass();
			PropertyDescriptor pd = new PropertyDescriptor(filed, clazz);
			Method getMethod = pd.getReadMethod();//获得get方法

			if (pd != null) {

				Object o = getMethod.invoke(obj);//执行get方法返回一个Object
				return o;

			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}
}
