package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightUser;

/**
 * 用户扩展信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightUserMapper 
{
    /**
     * 查询用户扩展信息
     * 
     * @param userId 用户扩展信息主键
     * @return 用户扩展信息
     */
    public WeightUser selectWeightUserByUserId(Long userId);

    /**
     * 查询用户扩展信息列表
     * 
     * @param weightUser 用户扩展信息
     * @return 用户扩展信息集合
     */
    public List<WeightUser> selectWeightUserList(WeightUser weightUser);

    /**
     * 新增用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    public int insertWeightUser(WeightUser weightUser);

    /**
     * 修改用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    public int updateWeightUser(WeightUser weightUser);

    /**
     * 删除用户扩展信息
     * 
     * @param userId 用户扩展信息主键
     * @return 结果
     */
    public int deleteWeightUserByUserId(Long userId);

    /**
     * 批量删除用户扩展信息
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightUserByUserIds(Long[] userIds);
}
