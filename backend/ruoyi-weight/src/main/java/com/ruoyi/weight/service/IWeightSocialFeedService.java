package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightSocialFeed;

/**
 * 关注动态流Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightSocialFeedService 
{
    /**
     * 查询关注动态流
     * 
     * @param id 关注动态流主键
     * @return 关注动态流
     */
    public WeightSocialFeed selectWeightSocialFeedById(Long id);

    /**
     * 查询关注动态流列表
     * 
     * @param weightSocialFeed 关注动态流
     * @return 关注动态流集合
     */
    public List<WeightSocialFeed> selectWeightSocialFeedList(WeightSocialFeed weightSocialFeed);

    /**
     * 新增关注动态流
     * 
     * @param weightSocialFeed 关注动态流
     * @return 结果
     */
    public int insertWeightSocialFeed(WeightSocialFeed weightSocialFeed);

    /**
     * 修改关注动态流
     * 
     * @param weightSocialFeed 关注动态流
     * @return 结果
     */
    public int updateWeightSocialFeed(WeightSocialFeed weightSocialFeed);

    /**
     * 批量删除关注动态流
     * 
     * @param ids 需要删除的关注动态流主键集合
     * @return 结果
     */
    public int deleteWeightSocialFeedByIds(Long[] ids);

    /**
     * 删除关注动态流信息
     * 
     * @param id 关注动态流主键
     * @return 结果
     */
    public int deleteWeightSocialFeedById(Long id);
}
