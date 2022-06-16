package com.jeeplus.core.oauth;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

@RetrofitClient(baseUrl = "${oauth2.base_url}")
public interface RemoteOauth2SSOService {

    /**
     * 令牌申请
     *
     * @param params
     *  code:
     *  client_id:
     *  client_secret:
     *  redirect_uri:
     *
     * @return
     */
    @GET("oauth2/token")
    OauthResponseResult<OauthToken> getToken(@QueryMap Map<String, Object> params);

    /**
     * 令牌续期
     *
     * @param params
     *  code:
     *  client_id:
     *  client_secret:
     *  redirect_uri:
     */
    @GET("oauth2/refreshToken")
    OauthResponseResult<OauthToken> refreshToken(@QueryMap Map<String, Object> params);

    /**
     * 获取用户信息
     *
     * @param params access_token
     * @return
     */
    @GET("oauth2/users/getInfo")
    OauthResponseResult<String> getUserInfo(@QueryMap Map<String, Object> params);

}
