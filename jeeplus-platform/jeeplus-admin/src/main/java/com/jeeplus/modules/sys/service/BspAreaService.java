package com.jeeplus.modules.sys.service;

import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.ext.persistence.Node;
import com.jeeplus.core.oauth.OauthRegion;
import com.jeeplus.core.oauth.OauthResponseResult;
import com.jeeplus.core.oauth.RemoteOauth2ApiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BspAreaService {

    /** 区划Map缓存 */
    private static final String CACHE_OAUTH_REGION_MAP_KEY = "oauth_region_map";
    /** 区划List缓存 */
    private static final String CACHE_OAUTH_REGION_KEY = "oauth_region";

    @Autowired
    private RemoteOauth2ApiService remoteOauth2ApiService;

    /**
     * 获取省、市两级的区划数据
     *
     * @return
     */
    public List<Node> listCities() {
        return listOauthRegion().stream()
                .filter(it -> "2".equals(it.getGrade()) || "3".equals(it.getGrade()))
                .collect(Collectors.toList());
    }

    /**
     * 获取某市下县级列表数据
     *
     * @param regionCode 区划编码
     * @return
     */
    public List<Node> listCounties(String regionCode) {
        return listOauthRegion().stream()
                .filter(it -> it.getParent().equals(regionCode))
                .collect(Collectors.toList());
    }

    /**
     * 获取区划编码对应的区划名称
     *
     * @param regionCode 区划编码
     * @return
     */
    public String findRegionName(String regionCode) {
        Map<String, Node> regionMap = getRegionMap();
        Node node = regionMap.get(regionCode);
        if (!Objects.isNull(node)) {
            return node.getText();
        }
        return null;
    }

    /**
     * 通过区划编码获取区划对应的简码
     *
     * @param regionCode 区划编码
     * @return
     */
    public String findRegionShortCode(String regionCode) {
        Map<String, Node> regionMap = getRegionMap();
        Node node = regionMap.get(regionCode);
        if (!Objects.isNull(node)) {
            return node.getSortCode();
        }
        return null;
    }

    /**
     * 获取区划Map
     *
     * @return
     */
    public Map<String, Node> getRegionMap() {
        Map<String, Node> regionMap = (Map<String, Node>) CacheUtils.get(CACHE_OAUTH_REGION_MAP_KEY);
        if (MapUtils.isEmpty(regionMap)) {
            regionMap = listOauthRegion().stream().collect(Collectors.toMap(Node::getId, Function.identity()));
            CacheUtils.put(CACHE_OAUTH_REGION_MAP_KEY, regionMap);
        }
        return regionMap;
    }

    /**
     * 获取区划列表
     *
     * @return
     */
    public List<Node> listOauthRegion() {
        OauthRegion region = (OauthRegion) CacheUtils.get(CACHE_OAUTH_REGION_KEY);
        if (CollectionUtils.isEmpty(region)) {
            // 只获取省、市、县（区）级别的区划
            OauthResponseResult<OauthRegion> result = remoteOauth2ApiService.getRegionList("4");
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getMsg());
            }
            region = result.getData().stream()
                    .sorted(Comparator.comparing(OauthRegion.Info::getCode))
                    .collect(Collectors.toCollection(OauthRegion::new));
            CacheUtils.put(CACHE_OAUTH_REGION_KEY, region);
        }
        return region.stream()
                .map(it -> {
                    Node node = new Node();
                    node.setId(it.getCode());
                    node.setSortCode(it.getShortCode());
                    node.setText(it.getName());
                    node.setParent(it.getParentCode());
                    node.setGrade(it.getGrade());
                    return node;
                })
                .collect(Collectors.toList());
    }

}
