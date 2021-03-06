package com.jeeplus.modules.catalog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.catalog.entity.ConCataMaintain;
import com.jeeplus.modules.catalog.service.ConCataInventoryService;
import com.jeeplus.modules.catalog.service.ConCataMaintainService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 便民目录维护、认领
 */
@RestController
@RequestMapping(value = "/convience/catalog/maintain")
public class ConCataMaintainController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ConCataMaintainController.class);

    @Autowired
    private ConCataMaintainService cataMaintainService;

    @Autowired
    private ConCataInventoryService cataInventoryService;

    public ConCataMaintain get(@RequestParam(required = false)String id) {
        ConCataMaintain entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = cataMaintainService.getById(id);
        }
        if (entity == null) {
            entity = new ConCataMaintain();
        }
        return entity;
    }

    /**
     * 目录维护、目录认领查看
     * @param conCataMaintain
     * @return
     */
    @GetMapping(value = "getDetil")
    public AjaxJson getDetil(ConCataMaintain conCataMaintain) {
        List<ConCataMaintain> list = cataMaintainService.lambdaQuery()
                .eq(ConCataMaintain::getBaseCode,conCataMaintain.getBaseCode())
                .orderByDesc(ConCataMaintain::getCataVersion)
                .list();
        return AjaxJson.success().data(list);
    }

    /**
     * 便民目录维护列表数据
     * @param cataMaintain
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "list")
    public AjaxJson list(ConCataMaintain cataMaintain, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ConCataMaintain> query = cataMaintainService.lambdaQuery();
        if (StringUtils.isNotBlank(cataMaintain.getBaseCode())) {
            query.eq(ConCataMaintain::getBaseCode,cataMaintain.getBaseCode());
        }
        if (StringUtils.isNotBlank(cataMaintain.getCataName())) {
            query.like(ConCataMaintain::getCataName,cataMaintain.getCataName());
        }
        if (StringUtils.isNotBlank(cataMaintain.getCataType())) {
            query.eq(ConCataMaintain::getCataType,cataMaintain.getCataType());
        }
        query.eq(ConCataMaintain::getMaxVersion,"1");
        query.orderByDesc(ConCataMaintain::getUpdateDate);
        IPage<ConCataMaintain> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }


    /**
     * 目录维护新增
     * @param conCataMaintain
     * @return
     */
    @PostMapping(value = "save")
    public AjaxJson save(ConCataMaintain conCataMaintain) {
        String errMsg = beanValidator(conCataMaintain);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        boolean flag = cataMaintainService.lambdaQuery().eq(ConCataMaintain::getBaseCode, conCataMaintain.getBaseCode())
                .eq(ConCataMaintain::getCataVersion,conCataMaintain.getCataVersion())
                .exists();
        if (flag) {
            return AjaxJson.error("基本编码："+conCataMaintain.getBaseCode()+",该版本数据已存在");
        }
        conCataMaintain.setImportStatus("1");
        cataMaintainService.saveCata(conCataMaintain);
        return AjaxJson.success("保存成功");
    }

    /**
     * 目录变更
     * @param conCataMaintain
     * @return
     */
    @PostMapping(value = "edit")
    public AjaxJson edit(ConCataMaintain conCataMaintain) {
        cataMaintainService.updateById(conCataMaintain);
        return AjaxJson.success("变更成功");
    }


    /**
     * 便民目录认领列表数据
     * @param cataMaintain
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "claimList")
    public AjaxJson claimList(ConCataMaintain cataMaintain, HttpServletRequest request, HttpServletResponse response, Model model) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<ConCataMaintain> query = cataMaintainService.lambdaQuery();
        if (StringUtils.isNotBlank(cataMaintain.getBaseCode())) {
            query.eq(ConCataMaintain::getBaseCode,cataMaintain.getBaseCode());
        }
        if (StringUtils.isNotBlank(cataMaintain.getCataName())) {
            query.like(ConCataMaintain::getCataName,cataMaintain.getCataName());
        }
        if (StringUtils.isNotBlank(cataMaintain.getCataType())) {
            query.eq(ConCataMaintain::getCataType,cataMaintain.getCataType());
        }
        query.eq(ConCataMaintain::getMaxVersion,"1");
        query.eq(ConCataMaintain::getCataLevel,UserUtils.getGradeByRegionCode(cataMaintain.getCurrentUser().getRegionCode()));
        query.orderByDesc(ConCataMaintain::getUpdateDate);
        List<Object> inventory = cataInventoryService.getBaseCodeList(UserUtils.getUser().getOrgCode());
        query.notIn(inventory.size()>0,ConCataMaintain::getBaseCode,inventory);
        IPage<ConCataMaintain> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        return AjaxJson.success().data(page);
    }

    /**
     * 目录认领
     * @param cataMaintain
     * @return
     */
    @PostMapping(value = "cataClaim")
    public AjaxJson cataClaim(ConCataMaintain cataMaintain) {
        cataInventoryService.saveCata(cataMaintain);
        return AjaxJson.success("认领成功");
    }




}
