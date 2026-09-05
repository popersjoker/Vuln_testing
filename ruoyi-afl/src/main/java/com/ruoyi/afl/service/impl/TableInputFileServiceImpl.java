package com.ruoyi.afl.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.afl.mapper.TableInputFileMapper;
import com.ruoyi.afl.domain.TableInputFile;
import com.ruoyi.afl.service.ITableInputFileService;

/**
 * 输入文件上传记录Service业务层处理
 * 
 * @author zyz
 * @date 2023-10-26
 */
@Service
public class TableInputFileServiceImpl implements ITableInputFileService 
{
    @Autowired
    private TableInputFileMapper tableInputFileMapper;

    /**
     * 查询输入文件上传记录
     * 
     * @param uid 输入文件上传记录主键
     * @return 输入文件上传记录
     */
    @Override
    public TableInputFile selectTableInputFileByUid(Long uid)
    {
        return tableInputFileMapper.selectTableInputFileByUid(uid);
    }
    @Override
    public TableInputFile selectByYid(Long yid)
    {
        return tableInputFileMapper.selectByYid(yid);
    }

    /**
     * 查询输入文件上传记录列表
     * 
     * @param tableInputFile 输入文件上传记录
     * @return 输入文件上传记录
     */
    @Override
    public List<TableInputFile> selectTableInputFileList(TableInputFile tableInputFile)
    {
        return tableInputFileMapper.selectTableInputFileList(tableInputFile);
    }

    /**
     * 新增输入文件上传记录
     * 
     * @param tableInputFile 输入文件上传记录
     * @return 结果
     */
    @Override
    public int insertTableInputFile(TableInputFile tableInputFile)
    {
        return tableInputFileMapper.insertTableInputFile(tableInputFile);
    }

    /**
     * 修改输入文件上传记录
     * 
     * @param tableInputFile 输入文件上传记录
     * @return 结果
     */
    @Override
    public int updateTableInputFile(TableInputFile tableInputFile)
    {
        return tableInputFileMapper.updateTableInputFile(tableInputFile);
    }

    /**
     * 批量删除输入文件上传记录
     * 
     * @param uids 需要删除的输入文件上传记录主键
     * @return 结果
     */
    @Override
    public int deleteTableInputFileByUids(Long[] uids)
    {
        return tableInputFileMapper.deleteTableInputFileByUids(uids);
    }

    /**
     * 删除输入文件上传记录信息
     * 
     * @param uid 输入文件上传记录主键
     * @return 结果
     */
    @Override
    public int deleteTableInputFileByUid(Long uid)
    {
        return tableInputFileMapper.deleteTableInputFileByUid(uid);
    }

    @Override
    public boolean selectTableInputFileByYid(Long yid){
        return tableInputFileMapper.selectTableInputFileByYid(yid);
    }

    public int updateTableInputFileByYid(TableInputFile tableInputFile)
    {
        return tableInputFileMapper.updateTableInputFileByYid(tableInputFile);
    }


}
