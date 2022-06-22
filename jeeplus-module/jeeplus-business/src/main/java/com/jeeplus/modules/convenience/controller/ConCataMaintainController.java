package com.jeeplus.modules.convenience.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.convenience.entity.ConCataMaintain;
import com.jeeplus.modules.convenience.service.ConCataMaintainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
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
        boolean flag = cataMaintainService.lambdaQuery()
                .exists("SELECT * FROM con_cata_maintain WHERE base_code=? AND cata_version=?",conCataMaintain.getBaseCode(),conCataMaintain.getCataVersion())
                .exists();
        if (flag) {
            return AjaxJson.success("基本编码："+conCataMaintain.getBaseCode()+",该版本数据已存在");
        }
        ConCataMaintain entity = cataMaintainService.lambdaQuery()
                .eq(ConCataMaintain::getBaseCode,conCataMaintain.getBaseCode())
                .eq(ConCataMaintain::getMaxVersion,"1")
                .getEntity();
        if (entity != null) {
            if (entity.getCataVersion() > conCataMaintain.getCataVersion()) {
                conCataMaintain.setMaxVersion("0");
            }else {
                entity.setMaxVersion("0");
                cataMaintainService.updateById(entity);
                conCataMaintain.setMaxVersion("1");
            }
        }else {
            conCataMaintain.setMaxVersion("1");
        }
        conCataMaintain.setEnableStatus("1");
        cataMaintainService.save(conCataMaintain);
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
        return AjaxJson.success();
    }




}
