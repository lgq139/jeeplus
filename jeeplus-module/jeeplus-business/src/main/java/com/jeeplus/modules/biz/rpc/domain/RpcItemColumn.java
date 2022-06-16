package com.jeeplus.modules.biz.rpc.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class RpcItemColumn extends ArrayList<RpcItemColumn.RpcColumn> {

    public static class RpcColumn {

        private RpcItem columns;

        public RpcItem getColumns() {
            return columns;
        }
        public void setColumns(RpcItem columns) {
            this.columns = columns;
        }
    }

    public static class RpcItem {
        @JsonProperty("ID")
        private String itemId;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }

}
