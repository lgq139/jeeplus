package com.jeeplus.core.webdisk;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import okhttp3.MultipartBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.io.File;

@RetrofitClient(baseUrl = "${net_disk_url}/webdisk/manager/filetransfer/")
public interface WebDiskClientService {

    @Multipart
    @POST("upload")
    WebDiskResponseResult<String> uploadFile(@Header("uid") String uid, @Part MultipartBody.Part file);

}
