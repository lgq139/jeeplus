package com.jeeplus.modules.sys.service;

import com.jeeplus.core.ext.persistence.Node;
import com.jeeplus.core.oauth.OauthRegion;
import com.jeeplus.core.oauth.OauthResponseResult;
import com.jeeplus.core.oauth.RemoteOauth2ApiService;
import com.jeeplus.modules.sys.entity.AreaAuth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class AreaServiceTest {

    @Autowired
    private BspAreaService bspAreaService;
    @Autowired
    private AreaAuthService areaAuthService;
    @Autowired
    private RemoteOauth2ApiService remoteOauth2ApiService;

    @Test
    public void bspRegionListTest() {
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
        areaAuthService.saveBatch(areaAuths);
    }

}
