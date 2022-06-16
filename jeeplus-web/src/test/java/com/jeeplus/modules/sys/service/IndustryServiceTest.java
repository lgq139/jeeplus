package com.jeeplus.modules.sys.service;

import cn.hutool.core.io.resource.ResourceUtil;
import com.jeeplus.common.utils.ObjectMapperHolder;
import com.jeeplus.modules.sys.entity.Industry;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
public class IndustryServiceTest {

    @Autowired
    private IndustryService industryService;

    @Test
    public void batchInstallDataTest() {
        try {
            Map map = ObjectMapperHolder.defaultObjectMapper.readValue(ResourceUtil.getStream("data.json"), Map.class);
            Map parentData = (Map) map.get("0");
            install("0", parentData);
            handle(map, parentData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void batchInstallNewDataTest() {
        List<Industry> industries = ObjectMapperHolder.fromArrayJson(
                ResourceUtil.readUtf8Str("industry.json"), Industry.class);
        if (CollectionUtils.isNotEmpty(industries)) {
            industryService.saveBatch(industries);
        }
    }


    public void handle(Map data, Map parentMap) {
        for (Object key : parentMap.keySet()) {
            if (!Objects.isNull(data.get(key))) {
                Map dataMap = (Map) data.get(key);
                install((String) key, dataMap);
                handle(data, dataMap);
            }
        }
    }

    public void install(String parentId, Map dataMap) {
        List<Industry> industryList = (List<Industry>) dataMap.keySet().stream()
                .map(it -> new Industry()
                        .setId((String) it)
                        .setParentId(parentId)
                        .setName((String) dataMap.get(it)))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(industryList)) {
            industryService.saveBatch(industryList);
        }
    }

}
