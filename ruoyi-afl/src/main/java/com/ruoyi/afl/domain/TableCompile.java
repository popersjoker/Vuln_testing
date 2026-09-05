package com.ruoyi.afl.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 编译记录对象 table_compile
 *
 * @author zyz
 * @date 2023-10-30
 */
public class TableCompile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编译文件id */
    private Long cid;

    /** 编译文件名 */
    @Excel(name = "编译文件名")
    private String compiledName;

    /** 编译文件路径 */
    @Excel(name = "编译文件路径")
    private String compiledUrl;

    /** 源码文件id */
    @Excel(name = "源码文件id")
    private Long yid;

    public void setCid(Long cid)
    {
        this.cid = cid;
    }

    public Long getCid()
    {
        return cid;
    }
    public void setCompiledName(String compiledName)
    {
        this.compiledName = compiledName;
    }

    public String getCompiledName()
    {
        return compiledName;
    }
    public void setCompiledUrl(String compiledUrl)
    {
        this.compiledUrl = compiledUrl;
    }

    public String getCompiledUrl()
    {
        return compiledUrl;
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
                .append("cid", getCid())
                .append("compiledName", getCompiledName())
                .append("compiledUrl", getCompiledUrl())
                .append("yid", getYid())
                .toString();
    }
}
