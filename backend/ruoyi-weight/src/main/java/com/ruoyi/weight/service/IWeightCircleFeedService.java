package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircleFeed;

/**
 * 圈子动态Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightCircleFeedService 
{
    /**
     * 查询圈子动态
     * 
     * @param id 圈子动态主键
     * @return 圈子动态
     */
    public WeightCircleFeed selectWeightCircleFeedById(Long id);

    /**
     * 查询圈子动态列表
     * 
     * @param weightCircleFeed 圈子动态
     * @return 圈子动态集合
     */
    public List<WeightCircleFeed> selectWeightCircleFeedList(WeightCircleFeed weightCircleFeed);

    /**
     * 查询公开动态列表
     *
     * @return 圈子动态集合
     */
    public List<WeightCircleFeed> selectPublicFeedList();

    /**
     * 查询圈子内可见动态列表
     *
     * @param circleId 圈子ID
     * @return 圈子动态集合
     */
    public List<WeightCircleFeed> selectCircleVisibleFeeds(Long circleId);

    /**
     * 新增圈子动态
     * 
     * @param weightCircleFeed 圈子动态
     * @return 结果
     */
    public int insertWeightCircleFeed(WeightCircleFeed weightCircleFeed);

    /**
     * 修改圈子动态
     * 
     * @param weightCircleFeed 圈子动态
     * @return 结果
     */
    public int updateWeightCircleFeed(WeightCircleFeed weightCircleFeed);

    /**
     * 批量删除圈子动态
     * 
     * @param ids 需要删除的圈子动态主键集合
     * @return 结果
     */
    public int deleteWeightCircleFeedByIds(Long[] ids);

    /**
     * 删除圈子动态信息
     * 
     * @param id 圈子动态主键
     * @return 结果
     */
    public int deleteWeightCircleFeedById(Long id);
}
