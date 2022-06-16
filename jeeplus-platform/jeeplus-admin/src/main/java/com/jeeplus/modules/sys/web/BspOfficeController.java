package com.jeeplus.modules.sys.web;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.json.AjaxPage;
import com.jeeplus.core.oauth.OauthDept;
import com.jeeplus.modules.sys.service.BspOfficeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/office")
public class BspOfficeController {

    @Autowired
    private BspOfficeService bspOfficeService;

    /**
     * 从bsp数据库获取office（组织机构）数据
     *
     * @param regionCode
     *
     * @param regionCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "bspData")
    public AjaxJson bspData(@RequestParam("regionCode") String regionCode,
                            @RequestParam(value = "orgName", required = false) String orgName,
                            @RequestParam(value = "pageNo", defaultValue = "1") String pageNo,
                            @RequestParam(value = "pageSize", defaultValue = "20") String pageSize) {
        AjaxPage<OauthDept.Dept> list = bspOfficeService.listOffice(regionCode, Long.parseLong(pageNo), Long.parseLong(pageSize));
        if (StringUtils.isNotBlank(orgName)) {
            list.setRecords(list.getRecords().stream().filter(it -> it.getName().contains(orgName)).collect(Collectors.toList()));
        }
        return AjaxJson.success().put("data", list);
    }

}
