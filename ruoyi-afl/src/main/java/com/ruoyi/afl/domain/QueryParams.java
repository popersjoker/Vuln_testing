package com.ruoyi.afl.domain;

/*
 * 功能：
 * 作者：zyz
 * 日期：2023/11/11 16:07
 */
public class QueryParams {
    private Integer pageNum;
    private Integer pageSize;
    private String testTime;
    private String fileName;
    private Long fid;
    private Long yid;

    // 其他可能的查询参数

    // 省略构造函数、getter 和 setter

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public Long getYid() {
        return yid;
    }

    public void setYid(Long yid) {
        this.yid = yid;
    }

    @Override
    public String toString() {
        return "QueryParams{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", testTime='" + testTime + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fid=" + fid +
                ", yid=" + yid +
                '}';
    }
}


