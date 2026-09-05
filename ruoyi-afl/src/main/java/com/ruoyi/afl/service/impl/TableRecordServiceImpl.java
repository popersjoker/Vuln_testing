package com.ruoyi.afl.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.afl.mapper.TableRecordMapper;
import com.ruoyi.afl.domain.TableRecord;
import com.ruoyi.afl.service.ITableRecordService;

/**
 * 测试记录Service业务层处理
 *
 * @author zyz
 * @date 2023-11-05
 */
@Service
public class TableRecordServiceImpl implements ITableRecordService
{
    @Autowired
    private TableRecordMapper tableRecordMapper;

    /**
     * 查询测试记录
     *
     * @param fid 测试记录主键
     * @return 测试记录
     */
    @Override
    public TableRecord selectTableRecordByFid(Long fid)
    {
        return tableRecordMapper.selectTableRecordByFid(fid);
    }

    /**
     * 查询测试记录列表
     *
     * @param tableRecord 测试记录
     * @return 测试记录
     */
    @Override
    public List<TableRecord> selectTableRecordList(TableRecord tableRecord)
    {
        return tableRecordMapper.selectTableRecordList(tableRecord);
    }

    /**
     * 新增测试记录
     *
     * @param tableRecord 测试记录
     * @return 结果
     */
    @Override
    public int insertTableRecord(TableRecord tableRecord)
    {
        return tableRecordMapper.insertTableRecord(tableRecord);
    }

    /**
     * 修改测试记录
     *
     * @param tableRecord 测试记录
     * @return 结果
     */
    @Override
    public int updateTableRecord(TableRecord tableRecord)
    {
        return tableRecordMapper.updateTableRecord(tableRecord);
    }

    /**
     * 批量删除测试记录
     *
     * @param fids 需要删除的测试记录主键
     * @return 结果
     */
    @Override
    public int deleteTableRecordByFids(Long[] fids)
    {
        return tableRecordMapper.deleteTableRecordByFids(fids);
    }

    /**
     * 删除测试记录信息
     *
     * @param fid 测试记录主键
     * @return 结果
     */
    @Override
    public int deleteTableRecordByFid(Long fid)
    {
        return tableRecordMapper.deleteTableRecordByFid(fid);
    }

    @Override
    public List<TableRecord> selectTableRecordByYid(Long yid){
        return tableRecordMapper.selectTableRecordByYid(yid);
    }
}
