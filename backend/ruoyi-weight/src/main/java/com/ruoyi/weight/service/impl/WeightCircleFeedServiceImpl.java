package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleFeedMapper;
import com.ruoyi.weight.domain.WeightCircleFeed;
import com.ruoyi.weight.service.IWeightCircleFeedService;

/**
 * 圈子动态Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleFeedServiceImpl implements IWeightCircleFeedService 
{
    @Autowired
    private WeightCircleFeedMapper weightCircleFeedMapper;

    /**
     * 查询圈子动态
     * 
     * @param id 圈子动态主键
     * @return 圈子动态
     */
    @Override
    public WeightCircleFeed selectWeightCircleFeedById(Long id)
    {
        return weightCircleFeedMapper.selectWeightCircleFeedById(id);
    }

    /**
     * 查询圈子动态列表
     * 
     * @param weightCircleFeed 圈子动态
     * @return 圈子动态
     */
    @Override
    public List<WeightCircleFeed> selectWeightCircleFeedList(WeightCircleFeed weightCircleFeed)
    {
        return weightCircleFeedMapper.selectWeightCircleFeedList(weightCircleFeed);
    }

    /**
     * 查询公开动态列表
     *
     * @return 圈子动态
     */
    @Override
    public List<WeightCircleFeed> selectPublicFeedList()
    {
        return weightCircleFeedMapper.selectPublicFeedList();
    }

    /**
     * 查询圈子内可见动态列表
     *
     * @param circleId 圈子ID
     * @return 圈子动态
     */
    @Override
    public List<WeightCircleFeed> selectCircleVisibleFeeds(Long circleId)
    {
        return weightCircleFeedMapper.selectCircleVisibleFeeds(circleId);
    }

    /**
     * 新增圈子动态
     * 
     * @param weightCircleFeed 圈子动态
     * @return 结果
     */
    @Override
    public int insertWeightCircleFeed(WeightCircleFeed weightCircleFeed)
    {
        weightCircleFeed.setCreateTime(DateUtils.getNowDate());
        return weightCircleFeedMapper.insertWeightCircleFeed(weightCircleFeed);
    }

    /**
     * 修改圈子动态
     * 
     * @param weightCircleFeed 圈子动态
     * @return 结果
     */
    @Override
    public int updateWeightCircleFeed(WeightCircleFeed weightCircleFeed)
    {
        weightCircleFeed.setUpdateTime(DateUtils.getNowDate());
        return weightCircleFeedMapper.updateWeightCircleFeed(weightCircleFeed);
    }

    /**
     * 批量删除圈子动态
     * 
     * @param ids 需要删除的圈子动态主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleFeedByIds(Long[] ids)
    {
        return weightCircleFeedMapper.deleteWeightCircleFeedByIds(ids);
    }

    /**
     * 删除圈子动态信息
     * 
     * @param id 圈子动态主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleFeedById(Long id)
    {
        return weightCircleFeedMapper.deleteWeightCircleFeedById(id);
    }
}
