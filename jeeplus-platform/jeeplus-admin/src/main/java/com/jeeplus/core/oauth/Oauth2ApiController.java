package com.jeeplus.core.oauth;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/openApi/role")
public class Oauth2ApiController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/pushRole")
    public AjaxJson pushRole(@RequestBody StandardRole standardRole) {
        if (!Objects.isNull(standardRole)) {
            if (standardRole.getSyncType().equals(StandardRole.ALL)) {
                List<Role> roleList = standardRole.getRoles().stream()
                        .map(it -> {
                            Role role = new Role();
                            role.setId(it.getId());
                            role.setName(it.getName());
                            role.setEnname(it.getCode());
                            role.setRoleType("9");
                            role.setRemarks(it.getRoleDesc());
                            role.setUseable(it.getUseable());
                            role.setSysData("1");
                            return role;
                        })
                        .collect(Collectors.toList());
                roleService.saveOrUpdateBatch(roleList);
            }
        }
        return AjaxJson.success();
    }

}
