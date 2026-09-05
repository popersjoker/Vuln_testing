package com.ruoyi.afl.mapper;

import java.util.List;
import com.ruoyi.afl.domain.Testafl;

/**
 * afl测试Mapper接口
 * 
 * @author lzh
 * @date 2023-09-25
 */
public interface TestaflMapper 
{
    /**
     * 查询afl测试
     * 
     * @param fid afl测试主键
     * @return afl测试
     */
    public Testafl selectTestaflByFid(Long fid);

    /**
     * 查询afl测试列表
     * 
     * @param testafl afl测试
     * @return afl测试集合
     */
    public List<Testafl> selectTestaflList(Testafl testafl);

    /**
     * 新增afl测试
     * 
     * @param testafl afl测试
     * @return 结果
     */
    public int insertTestafl(Testafl testafl);

    /**
     * 修改afl测试
     * 
     * @param testafl afl测试
     * @return 结果
     */
    public int updateTestafl(Testafl testafl);

    /**
     * 删除afl测试
     * 
     * @param fid afl测试主键
     * @return 结果
     */
    public int deleteTestaflByFid(Long fid);

    /**
     * 批量删除afl测试
     * 
     * @param fids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTestaflByFids(Long[] fids);
}
