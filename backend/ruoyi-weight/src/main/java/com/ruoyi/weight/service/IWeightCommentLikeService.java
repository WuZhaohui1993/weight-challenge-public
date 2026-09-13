package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightCommentLike;

/**
 * 评论点赞Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightCommentLikeService 
{
    /**
     * 查询评论点赞
     * 
     * @param id 评论点赞主键
     * @return 评论点赞
     */
    public WeightCommentLike selectWeightCommentLikeById(Long id);

    /**
     * 查询评论点赞列表
     * 
     * @param weightCommentLike 评论点赞
     * @return 评论点赞集合
     */
    public List<WeightCommentLike> selectWeightCommentLikeList(WeightCommentLike weightCommentLike);

    /**
     * 新增评论点赞
     * 
     * @param weightCommentLike 评论点赞
     * @return 结果
     */
    public int insertWeightCommentLike(WeightCommentLike weightCommentLike);

    /**
     * 修改评论点赞
     * 
     * @param weightCommentLike 评论点赞
     * @return 结果
     */
    public int updateWeightCommentLike(WeightCommentLike weightCommentLike);

    /**
     * 批量删除评论点赞
     * 
     * @param ids 需要删除的评论点赞主键集合
     * @return 结果
     */
    public int deleteWeightCommentLikeByIds(Long[] ids);

    /**
     * 删除评论点赞信息
     * 
     * @param id 评论点赞主键
     * @return 结果
     */
    public int deleteWeightCommentLikeById(Long id);
}
