package com.jeeplus.modules.biz.openapi;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.DictType;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.entity.Industry;
import com.jeeplus.modules.sys.entity.IndustryStructure;
import com.jeeplus.modules.sys.service.DictTreeValueService;
import com.jeeplus.modules.sys.service.DictTypeService;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.vo.DictTypeVO;
import com.jeeplus.modules.sys.vo.DictValueVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 开放字典相关数据接口
 *
 * TODO: 后续优化需要增加缓存，提升性能
 *
 * @author FEAR
 */
@RestController
@RequestMapping("/openApi/v2/dict")
public class DictApiController extends BaseController {

    @Autowired
    private DictTypeService dictTypeService;
    @Autowired
    private DictTreeValueService dictTreeValueService;

    /**
     *
     * @param type 字典类型
     * @return
     */
    @GetMapping("getItemByType")
    public AjaxJson dict(String type) {
        DictType dictType = dictTypeService.lambdaQuery().eq(DictType::getType, type).one();
        if (dictType == null) {
            return AjaxJson.error("该字典项不存在");
        }
        if ("2".equals(dictType.getOpen())) {
            return AjaxJson.error("该字典项不对外开放");
        }
        return AjaxJson.success().put("data",
                DictUtils.getDictList(type)
                        .stream()
                        .map(DictValueVO::new)
                        .collect(Collectors.toList()));
    }

    /**
     * 获取开放字典类型列表
     *
     * @return
     */
    @GetMapping("getTypeList")
    public AjaxJson getDictTypeList() {
        List<DictTypeVO> dictTypeList = dictTypeService.lambdaQuery()
                .eq(DictType::getOpen, "1")
                .list().stream().map(DictTypeVO::new)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dictTypeList)) {
            return AjaxJson.success().put("data", dictTypeList);
        }
        return AjaxJson.success().put("data", Collections.emptyList());
    }

    /**
     * TODO: 字典Map接口开发
     *
     * @return
     */
    @GetMapping("getDictMap")
    public AjaxJson getDictMap() {
        Map<String, List<DictValue>> dictMap = DictUtils.getOpenDictMap();
        if (MapUtils.isNotEmpty(dictMap)) {
            Map<String, List<DictValueVO>> resultMap = new HashMap<>();
            dictMap.forEach((key, value) -> {
                resultMap.put(key, value.stream().map(DictValueVO::new).collect(Collectors.toList()));
            });
            return AjaxJson.success().put("data", resultMap);
        }
        return AjaxJson.success();
    }

    /**
     * 获取列表型国民经济行业分类数据
     *
     * @return AjaxJson
     */
    @GetMapping("/industry/getList")
    public AjaxJson getIndustryList() {
        List<Industry> list = DictUtils.getIndustryMap();
        return AjaxJson.success().put("data", CollectionUtils.isNotEmpty(list) ? list : Collections.emptyList());
    }

    /**
     * 获取树型国民经济行业分类数据
     *
     * @return AjaxJson
     */
    @GetMapping("/industry/getTreeList")
    public AjaxJson getIndustryMap() {
        List<Industry> result = new ArrayList<>();
        List<Industry> list = DictUtils.getIndustryMap();
        if (CollectionUtils.isNotEmpty(list)) {
            result = TreeUtils.tree(list, "0");
        }
        return AjaxJson.success().put("data", result);
    }

    /**
     * 获取列表型产业结构调整指导目录数据
     *
     * @return AjaxJson
     */
    @GetMapping("/industryStructure/getList")
    public AjaxJson getIndustryStructureList() {
        List<IndustryStructure> list = DictUtils.getIndustryStructureMap();
        return AjaxJson.success().put("data", CollectionUtils.isNotEmpty(list) ? list : Collections.emptyList());
    }

    /**
     * 获取树型产业结构调整指导目录数据
     *
     * @return AjaxJson
     */
    @GetMapping("/industryStructure/getTreeList")
    public AjaxJson getIndustryStructureMap(@RequestParam(value = "type", required = false) String type) {
        List<IndustryStructure> result = new ArrayList<>();
        List<IndustryStructure> list = DictUtils.getIndustryStructureMap();
        if (CollectionUtils.isNotEmpty(list)) {
            if (StringUtils.isNotBlank(type)) {
                result = TreeUtils.tree(list.stream()
                        .filter(it -> it.getType().equals(type))
                        .collect(Collectors.toList()), "0");
            } else {
                result = TreeUtils.tree(list, "0");
            }
        }
        return AjaxJson.success().put("data", result);
    }

}
