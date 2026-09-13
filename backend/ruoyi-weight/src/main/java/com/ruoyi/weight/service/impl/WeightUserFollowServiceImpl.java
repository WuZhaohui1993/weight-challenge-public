package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightUserFollowMapper;
import com.ruoyi.weight.domain.WeightUserFollow;
import com.ruoyi.weight.service.IWeightUserFollowService;

/**
 * 用户关注关系Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightUserFollowServiceImpl implements IWeightUserFollowService 
{
    @Autowired
    private WeightUserFollowMapper weightUserFollowMapper;

    /**
     * 查询用户关注关系
     * 
     * @param id 用户关注关系主键
     * @return 用户关注关系
     */
    @Override
    public WeightUserFollow selectWeightUserFollowById(Long id)
    {
        return weightUserFollowMapper.selectWeightUserFollowById(id);
    }

    /**
     * 查询用户关注关系列表
     * 
     * @param weightUserFollow 用户关注关系
     * @return 用户关注关系
     */
    @Override
    public List<WeightUserFollow> selectWeightUserFollowList(WeightUserFollow weightUserFollow)
    {
        return weightUserFollowMapper.selectWeightUserFollowList(weightUserFollow);
    }

    /**
     * 新增用户关注关系
     * 
     * @param weightUserFollow 用户关注关系
     * @return 结果
     */
    @Override
    public int insertWeightUserFollow(WeightUserFollow weightUserFollow)
    {
        weightUserFollow.setCreateTime(DateUtils.getNowDate());
        return weightUserFollowMapper.insertWeightUserFollow(weightUserFollow);
    }

    /**
     * 修改用户关注关系
     * 
     * @param weightUserFollow 用户关注关系
     * @return 结果
     */
    @Override
    public int updateWeightUserFollow(WeightUserFollow weightUserFollow)
    {
        return weightUserFollowMapper.updateWeightUserFollow(weightUserFollow);
    }

    /**
     * 批量删除用户关注关系
     * 
     * @param ids 需要删除的用户关注关系主键
     * @return 结果
     */
    @Override
    public int deleteWeightUserFollowByIds(Long[] ids)
    {
        return weightUserFollowMapper.deleteWeightUserFollowByIds(ids);
    }

    /**
     * 删除用户关注关系信息
     * 
     * @param id 用户关注关系主键
     * @return 结果
     */
    @Override
    public int deleteWeightUserFollowById(Long id)
    {
        return weightUserFollowMapper.deleteWeightUserFollowById(id);
    }
}
