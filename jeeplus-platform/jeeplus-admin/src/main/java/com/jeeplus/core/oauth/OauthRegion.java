package com.jeeplus.core.oauth;

import java.io.Serializable;
import java.util.ArrayList;

public class OauthRegion extends ArrayList<OauthRegion.Info> implements Serializable {

    public static class Info implements Serializable {
        // 标准编码
        private String code;
        // 区划名称
        private String name;
        // 一体化平台区划编码（bsp,非标准6位）
        private String shortCode;
        // 行使层级, 2:省 3:市 4:县（区）
        private String grade;
        // 上级区划编码
        private String parentCode;
        // 排序
        private String sortOrder;
        private int subAreaCount;

        public Info() {
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortCode() {
            return shortCode;
        }

        public void setShortCode(String shortCode) {
            this.shortCode = shortCode;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getParentCode() {
            return parentCode;
        }

        public void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        public String getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        public int getSubAreaCount() {
            return subAreaCount;
        }

        public void setSubAreaCount(int subAreaCount) {
            this.subAreaCount = subAreaCount;
        }
    }

}
