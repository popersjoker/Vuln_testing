package com.ruoyi.afl.service;

import java.util.List;
import com.ruoyi.afl.domain.TableRecord;

/**
 * 测试记录Service接口
 *
 * @author zyz
 * @date 2023-11-05
 */
public interface ITableRecordService
{
    /**
     * 查询测试记录
     *
     * @param fid 测试记录主键
     * @return 测试记录
     */
    public TableRecord selectTableRecordByFid(Long fid);

    /**
     * 查询测试记录列表
     *
     * @param tableRecord 测试记录
     * @return 测试记录集合
     */
    public List<TableRecord> selectTableRecordList(TableRecord tableRecord);

    /**
     * 新增测试记录
     *
     * @param tableRecord 测试记录
     * @return 结果
     */
    public int insertTableRecord(TableRecord tableRecord);

    /**
     * 修改测试记录
     *
     * @param tableRecord 测试记录
     * @return 结果
     */
    public int updateTableRecord(TableRecord tableRecord);

    /**
     * 批量删除测试记录
     *
     * @param fids 需要删除的测试记录主键集合
     * @return 结果
     */
    public int deleteTableRecordByFids(Long[] fids);

    /**
     * 删除测试记录信息
     *
     * @param fid 测试记录主键
     * @return 结果
     */
    public int deleteTableRecordByFid(Long fid);

    public List<TableRecord> selectTableRecordByYid(Long yid);
}
