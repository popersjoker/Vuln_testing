package com.ruoyi.afl.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 输入文件上传记录对象 table_input_file
 * 
 * @author zyz
 * @date 2023-10-26
 */
public class TableInputFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 输入文件id */
    private Long uid;

    /** 输入文件名 */
    @Excel(name = "输入文件名")
    private String inputFileName;

    /** 上传输入文件路径 */
    @Excel(name = "上传输入文件路径")
    private String uploadInputUrl;

    /** 源码文件id */
    @Excel(name = "源码文件id")
    private Long yid;

    public void setUid(Long uid) 
    {
        this.uid = uid;
    }

    public Long getUid() 
    {
        return uid;
    }
    public void setInputFileName(String inputFileName) 
    {
        this.inputFileName = inputFileName;
    }

    public String getInputFileName() 
    {
        return inputFileName;
    }
    public void setUploadInputUrl(String uploadInputUrl) 
    {
        this.uploadInputUrl = uploadInputUrl;
    }

    public String getUploadInputUrl() 
    {
        return uploadInputUrl;
    }
    public void setYid(Long yid) 
    {
        this.yid = yid;
    }

    public Long getYid() 
    {
        return yid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("uid", getUid())
            .append("inputFileName", getInputFileName())
            .append("uploadInputUrl", getUploadInputUrl())
            .append("yid", getYid())
            .toString();
    }
}
