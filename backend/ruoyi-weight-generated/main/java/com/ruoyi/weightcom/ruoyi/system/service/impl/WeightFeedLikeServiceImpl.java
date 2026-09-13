package com.ruoyi.weightcom.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weightcom.ruoyi.system.mapper.WeightFeedLikeMapper;
import com.ruoyi.weightcom.ruoyi.system.domain.WeightFeedLike;
import com.ruoyi.weightcom.ruoyi.system.service.IWeightFeedLikeService;

/**
 * 动态点赞Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightFeedLikeServiceImpl implements IWeightFeedLikeService 
{
    @Autowired
    private WeightFeedLikeMapper weightFeedLikeMapper;

    /**
     * 查询动态点赞
     * 
     * @param id 动态点赞主键
     * @return 动态点赞
     */
    @Override
    public WeightFeedLike selectWeightFeedLikeById(Long id)
    {
        return weightFeedLikeMapper.selectWeightFeedLikeById(id);
    }

    /**
     * 查询动态点赞列表
     * 
     * @param weightFeedLike 动态点赞
     * @return 动态点赞
     */
    @Override
    public List<WeightFeedLike> selectWeightFeedLikeList(WeightFeedLike weightFeedLike)
    {
        return weightFeedLikeMapper.selectWeightFeedLikeList(weightFeedLike);
    }

    /**
     * 新增动态点赞
     * 
     * @param weightFeedLike 动态点赞
     * @return 结果
     */
    @Override
    public int insertWeightFeedLike(WeightFeedLike weightFeedLike)
    {
        weightFeedLike.setCreateTime(DateUtils.getNowDate());
        return weightFeedLikeMapper.insertWeightFeedLike(weightFeedLike);
    }

    /**
     * 修改动态点赞
     * 
     * @param weightFeedLike 动态点赞
     * @return 结果
     */
    @Override
    public int updateWeightFeedLike(WeightFeedLike weightFeedLike)
    {
        return weightFeedLikeMapper.updateWeightFeedLike(weightFeedLike);
    }

    /**
     * 批量删除动态点赞
     * 
     * @param ids 需要删除的动态点赞主键
     * @return 结果
     */
    @Override
    public int deleteWeightFeedLikeByIds(Long[] ids)
    {
        return weightFeedLikeMapper.deleteWeightFeedLikeByIds(ids);
    }

    /**
     * 删除动态点赞信息
     * 
     * @param id 动态点赞主键
     * @return 结果
     */
    @Override
    public int deleteWeightFeedLikeById(Long id)
    {
        return weightFeedLikeMapper.deleteWeightFeedLikeById(id);
    }
}
