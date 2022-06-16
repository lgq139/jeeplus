package com.jeeplus.modules.biz.rpc.service;

import com.github.lianjiatech.retrofit.spring.boot.annotation.Intercept;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.retry.Retry;
import com.jeeplus.modules.biz.rpc.domain.RpcPageResult;
import com.jeeplus.modules.biz.rpc.interceptor.ItemAuthInterceptor;
import retrofit2.http.*;

import java.util.Map;

@Retry
@RetrofitClient(baseUrl = "${item_url}/api/")
@Intercept(handler = ItemAuthInterceptor.class)
public interface RemoteItemService {

    /**
     * 根据目录编码获取事项列表
     *
     * params:
     *      whereValue
     *      paramValue
     *      page
     *      rows
     *
     * @param params 参数Map
     * @return
     */
    @FormUrlEncoded
    @POST("getItemListByPage")
    RpcPageResult getItemListByPage(@FieldMap Map<String, String> params);

    /**
     * 根据事项ID获取事项的详细信息
     * @param itemId 事项ID
     * @return
     */
    @GET("getAllItemInfoByItemID")
    String getAllItemInfoByItemID(@Query("itemId") String itemId);

}
