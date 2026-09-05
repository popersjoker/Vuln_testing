package com.ruoyi.afl.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.afl.mapper.TableCompileMapper;
import com.ruoyi.afl.domain.TableCompile;
import com.ruoyi.afl.service.ITableCompileService;

/**
 * 编译记录Service业务层处理
 *
 * @author zyz
 * @date 2023-10-30
 */
@Service
public class TableCompileServiceImpl implements ITableCompileService
{
    @Autowired
    private TableCompileMapper tableCompileMapper;

    /**
     * 查询编译记录
     *
     * @param cid 编译记录主键
     * @return 编译记录
     */
    @Override
    public TableCompile selectTableCompileByCid(Long cid)
    {
        return tableCompileMapper.selectTableCompileByCid(cid);
    }

    /**
     * 查询编译记录列表
     *
     * @param tableCompile 编译记录
     * @return 编译记录
     */
    @Override
    public List<TableCompile> selectTableCompileList(TableCompile tableCompile)
    {
        return tableCompileMapper.selectTableCompileList(tableCompile);
    }

    /**
     * 新增编译记录
     *
     * @param tableCompile 编译记录
     * @return 结果
     */
    @Override
    public int insertTableCompile(TableCompile tableCompile)
    {
        return tableCompileMapper.insertTableCompile(tableCompile);
    }

    /**
     * 修改编译记录
     *
     * @param tableCompile 编译记录
     * @return 结果
     */
    @Override
    public int updateTableCompile(TableCompile tableCompile)
    {
        return tableCompileMapper.updateTableCompile(tableCompile);
    }

    /**
     * 批量删除编译记录
     *
     * @param cids 需要删除的编译记录主键
     * @return 结果
     */
    @Override
    public int deleteTableCompileByCids(Long[] cids)
    {
        return tableCompileMapper.deleteTableCompileByCids(cids);
    }

    /**
     * 删除编译记录信息
     *
     * @param cid 编译记录主键
     * @return 结果
     */
    @Override
    public int deleteTableCompileByCid(Long cid)
    {
        return tableCompileMapper.deleteTableCompileByCid(cid);
    }

    @Override
    public TableCompile selectLatestTableCompileByYid(long l) {
        return tableCompileMapper.selectLatestTableCompileByYid(l);
    }

    @Override
    public boolean existsByYid(Long yid) {
        return tableCompileMapper.existsByYid(yid);
    }

    public int updateTableCompileByYid(TableCompile tableCompile){
        return tableCompileMapper.updateTableCompileByYid(tableCompile);
    }
}
