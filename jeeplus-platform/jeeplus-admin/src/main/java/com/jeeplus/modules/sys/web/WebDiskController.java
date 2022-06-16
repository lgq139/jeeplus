package com.jeeplus.modules.sys.web;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.webdisk.WebDiskClientService;
import com.jeeplus.core.webdisk.WebDiskResponseResult;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

@RestController
@RequestMapping("/sys/webdisk")
public class WebDiskController {

    @Autowired
    private WebDiskClientService webDiskClientService;
    @Value("${net_disk_uid}")
    private String webdiskUid;

    @PostMapping("upload")
    public AjaxJson upload(MultipartFile file) throws IOException {
        String fileName = URLEncoder.encode(Objects.requireNonNull(file.getOriginalFilename()), "utf-8");
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file.getBytes());
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", fileName, requestBody);
        WebDiskResponseResult<String> result = webDiskClientService.uploadFile(webdiskUid, part);
        return AjaxJson.success().put("data", result);
    }

}
