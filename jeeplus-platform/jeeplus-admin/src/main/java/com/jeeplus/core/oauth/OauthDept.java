package com.jeeplus.core.oauth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OauthDept implements Serializable {

    private long total;

    private List<Dept> rows;

    @Data
    public static class Dept implements Serializable {

        private String code;

        private String shortCode;

        private String name;

        private String shortName;

        private String level;

        private String regionCode;

        private String regionName;

        private String position;

        private String creditCode;

    }

}
