package com.jeeplus.modules.sys.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.jeeplus.modules.sys.service.BspAreaService;

/**
 *
 *
 * @author FEAR
 */
public class BspAreaUtils {

    public static String getRegionShortCode(String regionCode) {
        return SpringUtil.getBean(BspAreaService.class).findRegionShortCode(regionCode);
    }

}
