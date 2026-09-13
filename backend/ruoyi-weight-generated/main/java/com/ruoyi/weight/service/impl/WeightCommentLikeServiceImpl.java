package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCommentLikeMapper;
import com.ruoyi.weight.domain.WeightCommentLike;
import com.ruoyi.weight.service.IWeightCommentLikeService;

/**
 * 评论点赞Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCommentLikeServiceImpl implements IWeightCommentLikeService 
{
    @Autowired
    private WeightCommentLikeMapper weightCommentLikeMapper;

    /**
     * 查询评论点赞
     * 
     * @param id 评论点赞主键
     * @return 评论点赞
     */
    @Override
    public WeightCommentLike selectWeightCommentLikeById(Long id)
    {
        return weightCommentLikeMapper.selectWeightCommentLikeById(id);
    }

    /**
     * 查询评论点赞列表
     * 
     * @param weightCommentLike 评论点赞
     * @return 评论点赞
     */
    @Override
    public List<WeightCommentLike> selectWeightCommentLikeList(WeightCommentLike weightCommentLike)
    {
        return weightCommentLikeMapper.selectWeightCommentLikeList(weightCommentLike);
    }

    /**
     * 新增评论点赞
     * 
     * @param weightCommentLike 评论点赞
     * @return 结果
     */
    @Override
    public int insertWeightCommentLike(WeightCommentLike weightCommentLike)
    {
        weightCommentLike.setCreateTime(DateUtils.getNowDate());
        return weightCommentLikeMapper.insertWeightCommentLike(weightCommentLike);
    }

    /**
     * 修改评论点赞
     * 
     * @param weightCommentLike 评论点赞
     * @return 结果
     */
    @Override
    public int updateWeightCommentLike(WeightCommentLike weightCommentLike)
    {
        return weightCommentLikeMapper.updateWeightCommentLike(weightCommentLike);
    }

    /**
     * 批量删除评论点赞
     * 
     * @param ids 需要删除的评论点赞主键
     * @return 结果
     */
    @Override
    public int deleteWeightCommentLikeByIds(Long[] ids)
    {
        return weightCommentLikeMapper.deleteWeightCommentLikeByIds(ids);
    }

    /**
     * 删除评论点赞信息
     * 
     * @param id 评论点赞主键
     * @return 结果
     */
    @Override
    public int deleteWeightCommentLikeById(Long id)
    {
        return weightCommentLikeMapper.deleteWeightCommentLikeById(id);
    }
}
