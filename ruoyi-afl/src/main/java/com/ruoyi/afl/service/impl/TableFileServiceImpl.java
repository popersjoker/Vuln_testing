package com.ruoyi.afl.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.afl.mapper.TableFileMapper;
import com.ruoyi.afl.domain.TableFile;
import com.ruoyi.afl.service.ITableFileService;

/**
 * 源码文件信息Service业务层处理
 * 
 * @author zyz
 * @date 2023-10-23
 */
@Service
public class TableFileServiceImpl implements ITableFileService 
{
    @Autowired
    private TableFileMapper tableFileMapper;

    /**
     * 查询源码文件信息
     * 
     * @param yid 源码文件信息主键
     * @return 源码文件信息
     */
    @Override
    public TableFile selectTableFileByYid(Long yid)
    {
        return tableFileMapper.selectTableFileByYid(yid);
    }

    /**
     * 查询源码文件信息列表
     * 
     * @param tableFile 源码文件信息
     * @return 源码文件信息
     */
    @Override
    public List<TableFile> selectTableFileList(TableFile tableFile)
    {
        return tableFileMapper.selectTableFileList(tableFile);
    }

    /**
     * 新增源码文件信息
     * 
     * @param tableFile 源码文件信息
     * @return 结果
     */
    @Override
    public int insertTableFile(TableFile tableFile)
    {
        return tableFileMapper.insertTableFile(tableFile);
    }

    /**
     * 修改源码文件信息
     * 
     * @param tableFile 源码文件信息
     * @return 结果
     */
    @Override
    public int updateTableFile(TableFile tableFile)
    {
        return tableFileMapper.updateTableFile(tableFile);
    }

    /**
     * 批量删除源码文件信息
     * 
     * @param yids 需要删除的源码文件信息主键
     * @return 结果
     * int: 是影响的行数 只要删除成功必然至少有一行被删掉
     */
    @Override
    public int deleteTableFileByYids(Long[] yids)
    {
        return tableFileMapper.deleteTableFileByYids(yids);
    }

    /**
     * 删除源码文件信息信息
     * 
     * @param yid 源码文件信息主键
     * @return 结果
     */
    @Override
    public int deleteTableFileByYid(Long yid)
    {
        return tableFileMapper.deleteTableFileByYid(yid);
    }

    @Override
    public List<TableFile> select3TableFile(TableFile tableFile){
        return tableFileMapper.select3TableFile(tableFile);
    }

}
