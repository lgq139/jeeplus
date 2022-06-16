package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.ext.persistence.Node;
import com.jeeplus.core.utils.TreeUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.AreaAuth;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.AreaAuthService;
import com.jeeplus.modules.sys.utils.UserUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 区域Controller
 */
@RestController
@RequestMapping("/sys/areaAuth")
public class AreaAuthController extends BaseController {

    @Autowired
    private AreaAuthService areaAuthService;

    @ModelAttribute("area")
    public AreaAuth get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return areaAuthService.getById(id);
        } else {
            return new AreaAuth();
        }
    }

    /**
     * 获取授权区划
     *  authCatalog：1：授权创建场景 0：不授权创建场景
     *  code:
     *
     * @param areaAuth
     * @return
     */
    @GetMapping("listRegionNode")
    public AjaxJson listRegionNode(AreaAuth areaAuth) {
        LambdaQueryChainWrapper<AreaAuth> wrapper = areaAuthService.lambdaQuery();
        wrapper.eq(StringUtils.isNotBlank(areaAuth.getAuthCatalog()), AreaAuth::getAuthCatalog, areaAuth.getAuthCatalog());
        wrapper.eq(StringUtils.isNotBlank(areaAuth.getAuthDeploy()), AreaAuth::getAuthDeploy, areaAuth.getAuthDeploy());
        if (StringUtils.isBlank(areaAuth.getCode())) {
            User currentUser = UserUtils.getUser();
            areaAuth.setCode(currentUser.getRegionCode());
        }
        if (StringUtils.isNotBlank(areaAuth.getCode())) {
            if (!"140000".equals(areaAuth.getCode())) {
                String regionPrefix = areaAuth.getCode().substring(0, 4);
                wrapper.like(AreaAuth::getSortCode, regionPrefix);
            }
        }
        List<Node> result = wrapper.list().stream().map(it -> {
            Node node = new Node();
            node.setId(it.getCode());
            node.setSortCode(it.getSortCode());
            node.setText(it.getName());
            node.setParent(it.getParentId());
            return node;
        }).collect(Collectors.toList());
        return AjaxJson.success().put("data", result);
    }

    @GetMapping("query")
    public AjaxJson query(AreaAuth areaAuth) {
        LambdaQueryChainWrapper<AreaAuth> wrapper = areaAuthService.lambdaQuery();

        List<AreaAuth> list = wrapper.list();
        if (CollectionUtils.isNotEmpty(list)) {
            return AjaxJson.success().put("data", TreeUtils.tree(list, "#"));
        }
        return AjaxJson.success().put("data", Collections.emptyList());
    }

    @RequiresPermissions(value = {"sys:areaAuth:add", "sys:areaAuth:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(AreaAuth areaAuth, Model model) {
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(areaAuth);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }
        areaAuthService.saveOrUpdate(areaAuth);
        return AjaxJson.success();
    }

    @RequiresPermissions("sys:areaAuth:del")
    @PostMapping("delete")
    public AjaxJson delete(AreaAuth areaAuth) {
        areaAuthService.removeById(areaAuth);
        return AjaxJson.success();
    }

    @RequiresPermissions(value = {"sys:areaAuth:add", "sys:areaAuth:edit"}, logical = Logical.OR)
    @PostMapping("refresh")
    public AjaxJson refresh() {
        areaAuthService.refresh();
        return AjaxJson.success();
    }

}
