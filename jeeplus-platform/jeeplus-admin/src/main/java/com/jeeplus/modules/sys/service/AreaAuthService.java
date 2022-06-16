package com.jeeplus.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeeplus.core.oauth.OauthRegion;
import com.jeeplus.core.oauth.OauthResponseResult;
import com.jeeplus.core.oauth.RemoteOauth2ApiService;
import com.jeeplus.modules.sys.entity.AreaAuth;
import com.jeeplus.modules.sys.mapper.AreaAuthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AreaAuthService extends ServiceImpl<AreaAuthMapper, AreaAuth> {
    @Autowired
    private RemoteOauth2ApiService remoteOauth2ApiService;

    /**
     * 刷新数据
     */
    public void refresh() {
        // 只获取省、市、县（区）级别的区划
        OauthResponseResult<OauthRegion> result = remoteOauth2ApiService.getRegionList("4");
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMsg());
        }
        OauthRegion region = result.getData().stream()
                .sorted(Comparator.comparing(OauthRegion.Info::getCode))
                .collect(Collectors.toCollection(OauthRegion::new));
        List<AreaAuth> areaAuths = region.stream()
                .map(it -> {
                    AreaAuth auth = new AreaAuth();
                    auth.setId(it.getCode());
                    auth.setParentId(it.getParentCode());
                    auth.setName(it.getName());
                    auth.setCode(it.getCode());
                    auth.setSortCode(it.getShortCode());
                    auth.setLevel(it.getGrade());
                    auth.setSort(Integer.parseInt(it.getSortOrder()));
                    return auth;
                })
                .collect(Collectors.toList());
        saveOrUpdateBatch(areaAuths);
    }

}
