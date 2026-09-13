package com.ruoyi.weight.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.weight.domain.WeightCircleFeed;

/**
 * 圈子动态Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightCircleFeedMapper 
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
    public List<WeightCircleFeed> selectCircleVisibleFeeds(@Param("circleId") Long circleId);

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
     * 删除圈子动态
     * 
     * @param id 圈子动态主键
     * @return 结果
     */
    public int deleteWeightCircleFeedById(Long id);

    /**
     * 批量删除圈子动态
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightCircleFeedByIds(Long[] ids);
}
