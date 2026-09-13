package com.ruoyi.weightcom.ruoyi.system.service;

import java.util.List;
import com.ruoyi.weightcom.ruoyi.system.domain.WeightFeedLike;

/**
 * 动态点赞Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightFeedLikeService 
{
    /**
     * 查询动态点赞
     * 
     * @param id 动态点赞主键
     * @return 动态点赞
     */
    public WeightFeedLike selectWeightFeedLikeById(Long id);

    /**
     * 查询动态点赞列表
     * 
     * @param weightFeedLike 动态点赞
     * @return 动态点赞集合
     */
    public List<WeightFeedLike> selectWeightFeedLikeList(WeightFeedLike weightFeedLike);

    /**
     * 新增动态点赞
     * 
     * @param weightFeedLike 动态点赞
     * @return 结果
     */
    public int insertWeightFeedLike(WeightFeedLike weightFeedLike);

    /**
     * 修改动态点赞
     * 
     * @param weightFeedLike 动态点赞
     * @return 结果
     */
    public int updateWeightFeedLike(WeightFeedLike weightFeedLike);

    /**
     * 批量删除动态点赞
     * 
     * @param ids 需要删除的动态点赞主键集合
     * @return 结果
     */
    public int deleteWeightFeedLikeByIds(Long[] ids);

    /**
     * 删除动态点赞信息
     * 
     * @param id 动态点赞主键
     * @return 结果
     */
    public int deleteWeightFeedLikeById(Long id);
}
