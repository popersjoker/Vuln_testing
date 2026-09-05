package com.ruoyi.afl.mapper;

import java.util.List;

import com.ruoyi.afl.domain.TableCompile;

/**
 * 编译记录Mapper接口
 *
 * @author zyz
 * @date 2023-10-30
 */
public interface TableCompileMapper {
    /**
     * 查询编译记录
     *
     * @param cid 编译记录主键
     * @return 编译记录
     */
    public TableCompile selectTableCompileByCid(Long cid);

    /**
     * 查询编译记录列表
     *
     * @param tableCompile 编译记录
     * @return 编译记录集合
     */
    public List<TableCompile> selectTableCompileList(TableCompile tableCompile);

    /**
     * 新增编译记录
     *
     * @param tableCompile 编译记录
     * @return 结果
     */
    public int insertTableCompile(TableCompile tableCompile);

    /**
     * 修改编译记录
     *
     * @param tableCompile 编译记录
     * @return 结果
     */
    public int updateTableCompile(TableCompile tableCompile);

    /**
     * 删除编译记录
     *
     * @param cid 编译记录主键
     * @return 结果
     */
    public int deleteTableCompileByCid(Long cid);

    /**
     * 批量删除编译记录
     *
     * @param cids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTableCompileByCids(Long[] cids);

    public TableCompile selectLatestTableCompileByYid(Long l);

    public boolean existsByYid(Long yid);

    public int updateTableCompileByYid(TableCompile tableCompile);
}
