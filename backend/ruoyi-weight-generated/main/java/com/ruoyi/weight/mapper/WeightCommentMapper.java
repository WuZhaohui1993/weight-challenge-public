package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightComment;

/**
 * 评论Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightCommentMapper 
{
    /**
     * 查询评论
     * 
     * @param id 评论主键
     * @return 评论
     */
    public WeightComment selectWeightCommentById(Long id);

    /**
     * 查询评论列表
     * 
     * @param weightComment 评论
     * @return 评论集合
     */
    public List<WeightComment> selectWeightCommentList(WeightComment weightComment);

    /**
     * 新增评论
     * 
     * @param weightComment 评论
     * @return 结果
     */
    public int insertWeightComment(WeightComment weightComment);

    /**
     * 修改评论
     * 
     * @param weightComment 评论
     * @return 结果
     */
    public int updateWeightComment(WeightComment weightComment);

    /**
     * 删除评论
     * 
     * @param id 评论主键
     * @return 结果
     */
    public int deleteWeightCommentById(Long id);

    /**
     * 批量删除评论
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightCommentByIds(Long[] ids);
}
