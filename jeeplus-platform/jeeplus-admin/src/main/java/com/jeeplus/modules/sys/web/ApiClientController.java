package com.jeeplus.modules.sys.web;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.ApiClient;
import com.jeeplus.modules.sys.service.ApiClientService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/sys/apiClient")
public class ApiClientController extends BaseController {

    @Autowired
    private ApiClientService apiClientService;

    @RequiresPermissions("sys:apiclient:list")
    @GetMapping("/list")
    public AjaxJson list(ApiClient apiClient) {
        LambdaQueryChainWrapper<ApiClient> wrapper = apiClientService.lambdaQuery();
        if (StringUtils.isNotBlank(apiClient.getSystemName())) {
            wrapper.like(ApiClient::getSystemName, apiClient.getSystemName());
        }
        if (StringUtils.isNotBlank(apiClient.getDescription())) {
            wrapper.like(ApiClient::getDescription, apiClient.getDescription());
        }
        List<ApiClient> list = wrapper.list();
        return AjaxJson.success().put("data", list.size() > 0 ? list : Collections.emptyList());
    }

    @RequiresPermissions(value = {"sys:apiclient:add", "sys:apiclient:edit"}, logical = Logical.OR)
    @PostMapping("save")
    public AjaxJson save(ApiClient apiClient) {
        String errMsg = beanValidator(apiClient);
        if (StringUtils.isNotBlank(errMsg)){
            return AjaxJson.error(errMsg);
        }
        apiClientService.saveOrUpdate(apiClient);
        return AjaxJson.success("接口认证配置保存成功");
    }

    @RequiresPermissions("sys:apiclient:del")
    @PostMapping("delete")
    public AjaxJson delete(String ids) {
        if (StringUtils.isNotBlank(ids)) {
            apiClientService.removeByIds(Arrays.asList(ids.split(",")));
        }
        return AjaxJson.success("接口认证配置删除成功");
    }

    @RequiresPermissions("sys:apiclient:edit")
    @PostMapping("refresh")
    public AjaxJson refresh() {
        apiClientService.refreshCache();
        return AjaxJson.success();
    }

}
