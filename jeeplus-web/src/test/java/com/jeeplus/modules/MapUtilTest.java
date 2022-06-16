package com.jeeplus.modules;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.jeeplus.common.utils.ObjectMapperHolder;
import com.jeeplus.common.utils.StringUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MapUtilTest {

    public static void main(String[] args) {
        Map<String, Object> resultMap = new HashMap<>();
        String inputStream = ResourceUtil.readStr("structure.json", StandardCharsets.UTF_8);
        List<Map> items = ObjectMapperHolder.fromArrayJson(inputStream, Map.class);
        if (CollectionUtils.isNotEmpty(items)) {
            for (Map<String, String> obj : items) {
                Item item = new Item(obj);
                if (StringUtils.isBlank(item.getPId())) {
                    if (!item.getId().startsWith("X")) {
                        // 第一类 鼓励类
                        item.setType("1");
                    } else {
                        // 第二类 限制类
                        item.setType("2");
                    }
                    resultMap.put(item.getId(), item);
                }
            }
            for (Map<String, String> obj : items) {
                Item item = new Item(obj);
                if (StringUtils.isNotBlank(item.getPId())) {
                    Item parentItem = (Item) resultMap.get(item.getPId());
                    if (!Objects.isNull(parentItem)) {
                        parentItem.getChildren().add(item);
                    }
                }
            }
        }
        System.out.println(JSONUtil.toJsonStr(resultMap));
    }

    @Data
    public static class Item implements Serializable {
        private String id;
        private String pId;
        private String name;
        private String type;
        private List<Item> children = new ArrayList<>();

        public Item() {
        }

        public Item(Map<String, String> obj) {
            this.id = obj.get("id");
            this.pId = obj.get("pId");
            this.name = obj.get("name");
        }
    }

}
