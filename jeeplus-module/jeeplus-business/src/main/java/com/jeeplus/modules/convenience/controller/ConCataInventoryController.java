package com.jeeplus.modules.convenience.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.convenience.entity.ConCataInventory;
import com.jeeplus.modules.convenience.service.ConCataInventoryService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 便民目录清单
 */
@RestController
@RequestMapping(value = "/convience/catalog/inventory")
public class ConCataInventoryController extends BaseController {

    @Autowired
    private ConCataInventoryService cataInventoryService;

    public ConCataInventory get(@RequestParam(required = false)String id) {
        ConCataInventory entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = cataInventoryService.getById(id);
        }
        if (entity == null) {
            entity = new ConCataInventory();
        }
        return entity;
    }

    /**
     * 目录清单查看
     * @param cataInventory
     * @return
     */
    @GetMapping(value = "getDetil")
    public AjaxJson getDetil(ConCataInventory cataInventory) {
        List<ConCataInventory> list = cataInventoryService.lambdaQuery()
                .eq(ConCataInventory::getBaseCode,cataInventory.getBaseCode())
                .list();
        return AjaxJson.success().data(list);
    }

    /**
     * 数据列表
     * @param cataInventory
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "list")
    public AjaxJson list(ConCataInventory cataInventory, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ConCataInventory> query = cataInventoryService.lambdaQuery();
        if (StringUtils.isNotBlank(cataInventory.getBaseCode())) {
            query.eq(ConCataInventory::getBaseCode,cataInventory.getBaseCode());
        }
        if (StringUtils.isNotBlank(cataInventory.getCataName())) {
            query.like(ConCataInventory::getCataName,cataInventory.getCataName());
        }
        if (StringUtils.isNotBlank(cataInventory.getCataType())) {
            query.eq(ConCataInventory::getCataType,cataInventory.getCataType());
        }
        query.eq(ConCataInventory::getOrgCode, UserUtils.getUser().getOrgCode());
        query.orderByDesc(ConCataInventory::getUpdateDate);
        IPage<ConCataInventory> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }
}
