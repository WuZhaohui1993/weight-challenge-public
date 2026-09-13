package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCommentMapper;
import com.ruoyi.weight.domain.WeightComment;
import com.ruoyi.weight.service.IWeightCommentService;

/**
 * 评论Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCommentServiceImpl implements IWeightCommentService 
{
    @Autowired
    private WeightCommentMapper weightCommentMapper;

    /**
     * 查询评论
     * 
     * @param id 评论主键
     * @return 评论
     */
    @Override
    public WeightComment selectWeightCommentById(Long id)
    {
        return weightCommentMapper.selectWeightCommentById(id);
    }

    /**
     * 查询评论列表
     * 
     * @param weightComment 评论
     * @return 评论
     */
    @Override
    public List<WeightComment> selectWeightCommentList(WeightComment weightComment)
    {
        return weightCommentMapper.selectWeightCommentList(weightComment);
    }

    /**
     * 新增评论
     * 
     * @param weightComment 评论
     * @return 结果
     */
    @Override
    public int insertWeightComment(WeightComment weightComment)
    {
        weightComment.setCreateTime(DateUtils.getNowDate());
        return weightCommentMapper.insertWeightComment(weightComment);
    }

    /**
     * 修改评论
     * 
     * @param weightComment 评论
     * @return 结果
     */
    @Override
    public int updateWeightComment(WeightComment weightComment)
    {
        weightComment.setUpdateTime(DateUtils.getNowDate());
        return weightCommentMapper.updateWeightComment(weightComment);
    }

    /**
     * 批量删除评论
     * 
     * @param ids 需要删除的评论主键
     * @return 结果
     */
    @Override
    public int deleteWeightCommentByIds(Long[] ids)
    {
        return weightCommentMapper.deleteWeightCommentByIds(ids);
    }

    /**
     * 删除评论信息
     * 
     * @param id 评论主键
     * @return 结果
     */
    @Override
    public int deleteWeightCommentById(Long id)
    {
        return weightCommentMapper.deleteWeightCommentById(id);
    }
}
