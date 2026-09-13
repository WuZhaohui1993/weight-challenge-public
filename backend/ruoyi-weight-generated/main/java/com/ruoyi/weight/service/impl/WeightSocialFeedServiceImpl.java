package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightSocialFeedMapper;
import com.ruoyi.weight.domain.WeightSocialFeed;
import com.ruoyi.weight.service.IWeightSocialFeedService;

/**
 * 关注动态流Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightSocialFeedServiceImpl implements IWeightSocialFeedService 
{
    @Autowired
    private WeightSocialFeedMapper weightSocialFeedMapper;

    /**
     * 查询关注动态流
     * 
     * @param id 关注动态流主键
     * @return 关注动态流
     */
    @Override
    public WeightSocialFeed selectWeightSocialFeedById(Long id)
    {
        return weightSocialFeedMapper.selectWeightSocialFeedById(id);
    }

    /**
     * 查询关注动态流列表
     * 
     * @param weightSocialFeed 关注动态流
     * @return 关注动态流
     */
    @Override
    public List<WeightSocialFeed> selectWeightSocialFeedList(WeightSocialFeed weightSocialFeed)
    {
        return weightSocialFeedMapper.selectWeightSocialFeedList(weightSocialFeed);
    }

    /**
     * 新增关注动态流
     * 
     * @param weightSocialFeed 关注动态流
     * @return 结果
     */
    @Override
    public int insertWeightSocialFeed(WeightSocialFeed weightSocialFeed)
    {
        weightSocialFeed.setCreateTime(DateUtils.getNowDate());
        return weightSocialFeedMapper.insertWeightSocialFeed(weightSocialFeed);
    }

    /**
     * 修改关注动态流
     * 
     * @param weightSocialFeed 关注动态流
     * @return 结果
     */
    @Override
    public int updateWeightSocialFeed(WeightSocialFeed weightSocialFeed)
    {
        return weightSocialFeedMapper.updateWeightSocialFeed(weightSocialFeed);
    }

    /**
     * 批量删除关注动态流
     * 
     * @param ids 需要删除的关注动态流主键
     * @return 结果
     */
    @Override
    public int deleteWeightSocialFeedByIds(Long[] ids)
    {
        return weightSocialFeedMapper.deleteWeightSocialFeedByIds(ids);
    }

    /**
     * 删除关注动态流信息
     * 
     * @param id 关注动态流主键
     * @return 结果
     */
    @Override
    public int deleteWeightSocialFeedById(Long id)
    {
        return weightSocialFeedMapper.deleteWeightSocialFeedById(id);
    }
}
