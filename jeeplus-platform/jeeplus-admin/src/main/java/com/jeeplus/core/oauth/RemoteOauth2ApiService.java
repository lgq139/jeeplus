package com.jeeplus.core.oauth;

import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

/**
 * 统一身份认证系统开放接口访问服务
 *
 *
 */
@Intercept(handler = Oauth2AuthInterceptor.class)
@RetrofitClient(baseUrl = "${oauth2.api_url}")
public interface RemoteOauth2ApiService {

    /**
     * 推送角色信息
     *
     * @param roles
     * @return
     */
    @POST("api/pushRoles")
    OauthResponseResult<String> pushRoles(@Body List<OauthRole> roles);

    /**
     * 获取区划列表信息
     *
     * @return
     */
    @Deprecated
    @GET("api/area/list")
    OauthResponseResult<OauthRegion> getRegionList(@Query("level") Integer level);

    @GET("api/area/list")
    OauthResponseResult<OauthRegion> getRegionList(@Query("grade") String grade);

    /**
     * 获取组织机构列表信息
     *
     * @param regionCode 区划编码
     * @return
     */
    @GET("api/depart/list")
    OauthResponseResult<OauthDept> getDeptList(@Query("regionCode") String regionCode,
                                               @Query("pageNum") long pageNum,
                                               @Query("pageSize") long pageSize);
}
