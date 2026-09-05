package com.ruoyi.afl.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 测试记录对象 table_record
 *
 * @author zyz
 * @date 2023-11-05
 */
public class TableRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 模糊测试记录id
     */
    private Long fid;

    /**
     * 测试时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "测试时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date testTime;

    /**
     * 输出文件url
     */
    @Excel(name = "输出文件url")
    private String fuzzOutUrl;

    /**
     * 源码文件id
     */
    @Excel(name = "源码文件id")
    private Long yid;

    /**
     * 编译文件id
     */
    @Excel(name = "编译文件id")
    private Long cid;

    /**
     * 输入文件id
     */
    @Excel(name = "输入文件id")
    private Long uid;

    /**
     * 唯一识别标识
     */
    @Excel(name = "唯一识别标识")
    private String uuid;

    private TableFile tableFile;

    public TableFile getTableFile() {
        return tableFile;
    }

    public void setTableFile(TableFile tableFile) {
        this.tableFile = tableFile;
    }


    public void setFid(Long fid) {
        this.fid = fid;
    }

    public Long getFid() {
        return fid;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setFuzzOutUrl(String fuzzOutUrl) {
        this.fuzzOutUrl = fuzzOutUrl;
    }

    public String getFuzzOutUrl() {
        return fuzzOutUrl;
    }

    public void setYid(Long yid) {
        this.yid = yid;
    }

    public Long getYid() {
        return yid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getCid() {
        return cid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("fid", getFid())
                .append("testTime", getTestTime())
                .append("fuzzOutUrl", getFuzzOutUrl())
                .append("yid", getYid())
                .append("cid", getCid())
                .append("uid", getUid())
                .append("uuid", getUuid())
                .toString();
    }
}
