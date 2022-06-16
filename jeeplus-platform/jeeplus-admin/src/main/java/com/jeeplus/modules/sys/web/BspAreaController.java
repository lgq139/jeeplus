package com.jeeplus.modules.sys.web;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.modules.sys.service.BspAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/area")
public class BspAreaController {

    @Autowired
    private BspAreaService bspAreaService;

    @GetMapping("/areaList")
    public AjaxJson getRegionList() {
        return AjaxJson.success().put("data", bspAreaService.listOauthRegion());
    }

    @GetMapping("/cities")
    public AjaxJson getRegionCityList() {
        return AjaxJson.success().put("data", bspAreaService.listCities());
    }

    @GetMapping("/counties")
    public AjaxJson getRegionCountyList(@RequestParam("regionCode") String regionCode) {
        return AjaxJson.success().put("data", bspAreaService.listCounties(regionCode));
    }

}
