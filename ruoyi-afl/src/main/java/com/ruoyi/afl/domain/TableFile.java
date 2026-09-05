package com.ruoyi.afl.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 源码文件信息对象 table_file
 * 
 * @author zyz
 * @date 2023-10-23
 */
public class TableFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 源码文件id */
    private Long yid;

    /** 源码文件名 */
    @Excel(name = "源码文件名")
    private String fileName;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 源码文件上传位置 */
    @Excel(name = "源码文件上传位置")
    private String uploadUrl;

    public void setYid(Long yid) 
    {
        this.yid = yid;
    }

    public Long getYid() 
    {
        return yid;
    }
    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }
    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }



    public void setUploadUrl(String uploadUrl) 
    {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadUrl() 
    {
        return uploadUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("yid", getYid())
            .append("fileName", getFileName())
            .append("uploadTime", getUploadTime())
            .append("uploadUrl", getUploadUrl())
            .toString();
    }
}
