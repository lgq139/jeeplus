package com.jeeplus.modules.theme.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.catalog.controller.ConCataImportFileController;
import com.jeeplus.modules.theme.entity.JTheme;
import com.jeeplus.modules.theme.service.JThemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 主题表;(j_theme)表控制层
 *
 * @author : http://www.chiner.pro
 * @date : 2022-7-2
 */
@Api(tags = "主题表对象功能接口")
@RestController
@RequestMapping("/sys/jTheme")
public class JThemeController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ConCataImportFileController.class);

    @Autowired
    private JThemeService jThemeService;

    /**
     * @description:
     * @param: jTheme
     * request
     * response
     * model
     * @return: com.jeeplus.common.json.AjaxJson
     * @author DELL
     * @date: 2022/7/2 16:51
     */
    @GetMapping(value = "list")
    public AjaxJson list(JTheme jTheme, HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        LambdaQueryChainWrapper<JTheme> query = jThemeService.lambdaQuery();
        //if (StringUtils.isNotBlank(cataMaintain.getBaseCode())) {
        //    query.eq(ConCataMaintain::getBaseCode, cataMaintain.getBaseCode());
        //}
        //if (StringUtils.isNotBlank(cataMaintain.getCataName())) {
        //    query.like(ConCataMaintain::getCataName, cataMaintain.getCataName());
        //}
        //if (StringUtils.isNotBlank(cataMaintain.getCataType())) {
        //    query.eq(ConCataMaintain::getCataType, cataMaintain.getCataType());
        //}
        //query.eq(JTheme::getMaxVersion, "1");
        //query.orderByDesc(JTheme::getUpdateDate);
        IPage<JTheme> page = query.page(new Page<>(Long.parseLong(pageNo), Long.parseLong(pageSize)));
        System.out.println(page.getRecords());
        return AjaxJson.success().data(page);
    }

    /**
     * 更新数据
     *
     * @param jTheme 实例对象
     * @return 实例对象
     */
    @ApiOperation("更新数据")
    @PutMapping
    public AjaxJson update(JTheme jTheme) {
        jThemeService.updateById(jTheme);
        return AjaxJson.success("主题修改成功");
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @ModelAttribute
    public JTheme get(@RequestParam(required = false) String id) {
        JTheme entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = jThemeService.getById(id);
        }
        if (entity == null) {
            entity = new JTheme();
        }
        return entity;
    }


    /**
     * 新增数据
     *
     * @param jTheme 实例对象
     * @return 实例对象
     */
    @ApiOperation("新增数据")
    @PostMapping
    public AjaxJson save(JTheme jTheme) {
        jThemeService.save(jTheme);
        return AjaxJson.success("主题保存成功");
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @ApiOperation("通过主键删除数据")
    @DeleteMapping
    public AjaxJson deleteById(String id) {
        return AjaxJson.success("主题删除成功");
    }
}
