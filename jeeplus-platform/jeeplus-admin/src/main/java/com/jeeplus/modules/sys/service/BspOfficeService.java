package com.jeeplus.modules.sys.service;

import com.jeeplus.common.json.AjaxPage;
import com.jeeplus.common.utils.CacheUtils;
import com.jeeplus.core.oauth.OauthDept;
import com.jeeplus.core.oauth.OauthResponseResult;
import com.jeeplus.core.oauth.RemoteOauth2ApiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BspOfficeService {

    /** 区划下部门List缓存 */
    private static final String CACHE_OAUTH_DEPT_KEY = "oauth_dept";
    /** 区划下部门Map缓存 */
    private static final String CACHE_OAUTH_DEPT_MAP_KEY = "oauth_dept_map";

    @Autowired
    private RemoteOauth2ApiService remoteOauth2ApiService;

    /**
     * 工具方法：获取区划下部门的Map结构化数据
     *
     * @param regionCode 区划编码
     * @return
     */
    public Map<String, OauthDept.Dept> getDeptMap(String regionCode) {
        Map<String, OauthDept.Dept> deptMap = (Map<String, OauthDept.Dept>) CacheUtils.get(CACHE_OAUTH_DEPT_MAP_KEY + "_" + regionCode);
        if (MapUtils.isEmpty(deptMap)) {
            deptMap = listOffice(regionCode).stream()
                    .collect(Collectors.toMap(OauthDept.Dept::getCode, Function.identity()));
            CacheUtils.put(CACHE_OAUTH_DEPT_MAP_KEY + "_" + regionCode, deptMap);
        }
        return deptMap;
    }

    /**
     * 工具方法：获取区划下部门的列表数据
     *
     * @param regionCode 区划编码
     * @return
     */
    public List<OauthDept.Dept> listOffice(String regionCode) {
        OauthDept dept = (OauthDept) CacheUtils.get(CACHE_OAUTH_DEPT_KEY + "_" + regionCode);
        if (Objects.isNull(dept)) {
            OauthResponseResult<OauthDept> result = remoteOauth2ApiService.getDeptList(regionCode, 1, 200);
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getMsg());
            }
            dept = result.getData();
            CacheUtils.put(CACHE_OAUTH_DEPT_KEY + "_" + regionCode, dept);
        }
        return dept.getRows();
    }

    public AjaxPage<OauthDept.Dept> listOffice(String regionCode, long pageNum, long pageSize) {
        OauthResponseResult<OauthDept> result = remoteOauth2ApiService.getDeptList(regionCode, pageNum, pageSize);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMsg());
        }
        return new AjaxPage<>(pageNum, pageSize, result.getData().getTotal(),
                CollectionUtils.isNotEmpty(result.getData().getRows()) ?
                        result.getData().getRows() : Collections.emptyList());
    }

}
