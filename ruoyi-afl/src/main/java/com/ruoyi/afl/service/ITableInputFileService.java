package com.ruoyi.afl.service;

import java.util.List;
import com.ruoyi.afl.domain.TableInputFile;
import javafx.scene.control.Tab;

/**
 * 输入文件上传记录Service接口
 * 
 * @author zyz
 * @date 2023-10-26
 */
public interface ITableInputFileService 
{
    /**
     * 查询输入文件上传记录
     * 
     * @param uid 输入文件上传记录主键
     * @return 输入文件上传记录
     */
    public TableInputFile selectTableInputFileByUid(Long uid);

    public TableInputFile selectByYid(Long yid);

    /**
     * 查询输入文件上传记录列表
     * 
     * @param tableInputFile 输入文件上传记录
     * @return 输入文件上传记录集合
     */
    public List<TableInputFile> selectTableInputFileList(TableInputFile tableInputFile);

    /**
     * 新增输入文件上传记录
     * 
     * @param tableInputFile 输入文件上传记录
     * @return 结果
     */
    public int insertTableInputFile(TableInputFile tableInputFile);

    /**
     * 修改输入文件上传记录
     * 
     * @param tableInputFile 输入文件上传记录
     * @return 结果
     */
    public int updateTableInputFile(TableInputFile tableInputFile);

    /**
     * 批量删除输入文件上传记录
     * 
     * @param uids 需要删除的输入文件上传记录主键集合
     * @return 结果
     */
    public int deleteTableInputFileByUids(Long[] uids);

    /**
     * 删除输入文件上传记录信息
     * 
     * @param uid 输入文件上传记录主键
     * @return 结果
     */
    public int deleteTableInputFileByUid(Long uid);



    public boolean selectTableInputFileByYid(Long yid);

    public int updateTableInputFileByYid(TableInputFile tableInputFile);
}
