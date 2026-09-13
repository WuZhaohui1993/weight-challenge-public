package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightUserMapper;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightUserService;

/**
 * 用户扩展信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightUserServiceImpl implements IWeightUserService 
{
    @Autowired
    private WeightUserMapper weightUserMapper;

    /**
     * 查询用户扩展信息
     * 
     * @param userId 用户扩展信息主键
     * @return 用户扩展信息
     */
    @Override
    public WeightUser selectWeightUserByUserId(Long userId)
    {
        return weightUserMapper.selectWeightUserByUserId(userId);
    }

    /**
     * 查询用户扩展信息列表
     * 
     * @param weightUser 用户扩展信息
     * @return 用户扩展信息
     */
    @Override
    public List<WeightUser> selectWeightUserList(WeightUser weightUser)
    {
        return weightUserMapper.selectWeightUserList(weightUser);
    }

    /**
     * 新增用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    @Override
    public int insertWeightUser(WeightUser weightUser)
    {
        weightUser.setCreateTime(DateUtils.getNowDate());
        return weightUserMapper.insertWeightUser(weightUser);
    }

    /**
     * 修改用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    @Override
    public int updateWeightUser(WeightUser weightUser)
    {
        weightUser.setUpdateTime(DateUtils.getNowDate());
        return weightUserMapper.updateWeightUser(weightUser);
    }

    /**
     * 批量删除用户扩展信息
     * 
     * @param userIds 需要删除的用户扩展信息主键
     * @return 结果
     */
    @Override
    public int deleteWeightUserByUserIds(Long[] userIds)
    {
        return weightUserMapper.deleteWeightUserByUserIds(userIds);
    }

    /**
     * 删除用户扩展信息信息
     * 
     * @param userId 用户扩展信息主键
     * @return 结果
     */
    @Override
    public int deleteWeightUserByUserId(Long userId)
    {
        return weightUserMapper.deleteWeightUserByUserId(userId);
    }
}
