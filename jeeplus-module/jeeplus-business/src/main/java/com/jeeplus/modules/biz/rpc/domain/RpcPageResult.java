package com.jeeplus.modules.biz.rpc.domain;

public class RpcPageResult {

    private RpcItemColumn pageList;

    private int totalPage;

    private int totlaRow;

    public RpcItemColumn getPageList() {
        return pageList;
    }

    public void setPageList(RpcItemColumn pageList) {
        this.pageList = pageList;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotlaRow() {
        return totlaRow;
    }

    public void setTotlaRow(int totlaRow) {
        this.totlaRow = totlaRow;
    }
}
