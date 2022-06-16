package com.jeeplus.modules.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.biz.entity.ItemBasicCode;
import com.jeeplus.modules.biz.service.ItemBasicCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 四级四同目录Controller
 *
 * @author FEAR
 * @version 2021-11-09
 */
@RestController
@RequestMapping(value = "/baseconfig/item/itemBasicCode")
public class ItemBasicCodeController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ItemBasicCodeController.class);

    @Autowired
    private ItemBasicCodeService itemBasicCodeService;

    @ModelAttribute
    public ItemBasicCode get(@RequestParam(required = false) String id) {
        ItemBasicCode entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = itemBasicCodeService.getById(id);
        }
        if (entity == null) {
            entity = new ItemBasicCode();
        }
        return entity;
    }

    /**
     * 四级四同目录列表数据
     */
    @GetMapping(value = "list")
    public AjaxJson list(ItemBasicCode itemBasicCode, HttpServletRequest request, HttpServletResponse response) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ItemBasicCode> query = itemBasicCodeService.lambdaQuery();
        if (StringUtils.isNotBlank(itemBasicCode.getCode())) {
            query.eq(ItemBasicCode::getCode, itemBasicCode.getCode());
        }
        if (StringUtils.isNotBlank(itemBasicCode.getName())) {
            query.like(ItemBasicCode::getName, itemBasicCode.getName());
        }
        if (StringUtils.isNotBlank(itemBasicCode.getSuperviseOrgName())) {
            query.like(ItemBasicCode::getSuperviseOrgName, itemBasicCode.getSuperviseOrgName());
        }
        IPage<ItemBasicCode> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

    @GetMapping("/dict")
    public AjaxJson listOf() {
        LambdaQueryChainWrapper<ItemBasicCode> wrapper = itemBasicCodeService.lambdaQuery();
        wrapper.orderByAsc(ItemBasicCode::getName);
        return AjaxJson.success().data(Optional.of(wrapper.list()).orElse(Collections.emptyList()));
    }

    /**
     * 列出事项目录及标准事项
     *
     * @param itemBasicCode
     * @return
     */
    @GetMapping("listOfStandardItem")
    public AjaxJson listOfStandardItem(ItemBasicCode itemBasicCode, HttpServletRequest request, HttpServletResponse response) {
        LambdaQueryWrapper<ItemBasicCode> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(ItemBasicCode::getName);
        List<ItemBasicCode> list = itemBasicCodeService.getBaseMapper().selectBasicCodeStandardItem(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return AjaxJson.success().data(list);
        }
        return AjaxJson.success().data(Collections.emptyList());
    }

    /**
     * 保存四级四同目录
     */
    @RequiresPermissions(value = {
            "baseconfig:item:itemBasicCode:add",
            "baseconfig:item:itemBasicCode:edit"
    }, logical = Logical.OR)
    @PostMapping(value = "save")
    public AjaxJson save(ItemBasicCode itemBasicCode) {
        String errMsg = beanValidator(itemBasicCode);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        itemBasicCodeService.saveOrUpdate(itemBasicCode);
        return AjaxJson.success("保存四级四同目录成功");
    }


    /**
     * 批量删除四级四同目录
     */
    @RequiresPermissions("baseconfig:item:itemBasicCode:del")
    @PostMapping(value = "delete")
    public AjaxJson delete(String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split(",");
            itemBasicCodeService.removeByIds(Arrays.asList(idArray));
        }
        return AjaxJson.success("删除四级四同目录成功");
    }

    @GetMapping(value = "detail")
    public ItemBasicCode detail(String id) {
        return itemBasicCodeService.getById(id);
    }

}
