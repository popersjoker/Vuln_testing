package com.ruoyi.afl.service;

import java.util.List;
import com.ruoyi.afl.domain.TableFile;

/**
 * 源码文件信息Service接口
 * 
 * @author zyz
 * @date 2023-10-23
 */
public interface ITableFileService 
{
    /**
     * 查询源码文件信息
     * 
     * @param yid 源码文件信息主键
     * @return 源码文件信息
     */
    public TableFile selectTableFileByYid(Long yid);

    /**
     * 查询源码文件信息列表
     * 
     * @param tableFile 源码文件信息
     * @return 源码文件信息集合
     */
    public List<TableFile> selectTableFileList(TableFile tableFile);

    /**
     * 新增源码文件信息
     * 
     * @param tableFile 源码文件信息
     * @return 结果
     */
    public int insertTableFile(TableFile tableFile);

    /**
     * 修改源码文件信息
     * 
     * @param tableFile 源码文件信息
     * @return 结果
     */
    public int updateTableFile(TableFile tableFile);

    /**
     * 批量删除源码文件信息
     * 
     * @param yids 需要删除的源码文件信息主键集合
     * @return 结果
     */
    public int deleteTableFileByYids(Long[] yids);

    /**
     * 删除源码文件信息信息
     * 
     * @param yid 源码文件信息主键
     * @return 结果
     */
    public int deleteTableFileByYid(Long yid);


    public List<TableFile> select3TableFile(TableFile tableFile);
}
