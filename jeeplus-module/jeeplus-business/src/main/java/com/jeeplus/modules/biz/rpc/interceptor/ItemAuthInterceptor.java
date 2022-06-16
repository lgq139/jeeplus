package com.jeeplus.modules.biz.rpc.interceptor;

import cn.hutool.crypto.digest.DigestUtil;
import com.github.lianjiatech.retrofit.spring.boot.interceptor.BasePathMatchInterceptor;
import com.jeeplus.common.utils.DateUtils;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * 事项系统接口认证
 *
 * 通过拦截器给接口添加访问认证
 *
 */
@Component
public class ItemAuthInterceptor extends BasePathMatchInterceptor {
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    @Value("${item_client_id}")
    private String clientId;
    @Value("${item_secret}")
    private String secret;

    @Override
    protected Response doIntercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        HttpUrl.Builder urlBuilder = request.url().newBuilder();

        String time = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String a = clientId + secret + time;
        String sign = DigestUtil.md5Hex(a);

        if (request.method().equals(METHOD_POST)) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            // 把已有的post参数添加到新的构造器
            if (request.body() instanceof FormBody) {
                FormBody formBody = (FormBody) request.body();
                for (int i = 0; i < formBody.size(); i++) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }
            }
            // 这里可以添加一些公共post参数
            FormBody newBody = bodyBuilder.addEncoded("client_id", clientId)
                    .addEncoded("time", time)
                    .addEncoded("sign", sign)
                    .build();
            // 将最终的表单body填充到request中
            requestBuilder.post(newBody);
        } else if (request.method().equals(METHOD_GET)) {
            HttpUrl httpUrl = urlBuilder
                    .addQueryParameter("client_id", clientId)
                    .addQueryParameter("time", time)
                    .addQueryParameter("sign", sign)
                    .build();
            // 将最终的url填充到request中
            requestBuilder.url(httpUrl);
        }
        return chain.proceed(requestBuilder.build());
    }


}
