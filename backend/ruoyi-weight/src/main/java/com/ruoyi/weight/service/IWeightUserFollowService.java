package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightUserFollow;

/**
 * 用户关注关系Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightUserFollowService 
{
    /**
     * 查询用户关注关系
     * 
     * @param id 用户关注关系主键
     * @return 用户关注关系
     */
    public WeightUserFollow selectWeightUserFollowById(Long id);

    /**
     * 查询用户关注关系列表
     * 
     * @param weightUserFollow 用户关注关系
     * @return 用户关注关系集合
     */
    public List<WeightUserFollow> selectWeightUserFollowList(WeightUserFollow weightUserFollow);

    /**
     * 新增用户关注关系
     * 
     * @param weightUserFollow 用户关注关系
     * @return 结果
     */
    public int insertWeightUserFollow(WeightUserFollow weightUserFollow);

    /**
     * 修改用户关注关系
     * 
     * @param weightUserFollow 用户关注关系
     * @return 结果
     */
    public int updateWeightUserFollow(WeightUserFollow weightUserFollow);

    /**
     * 批量删除用户关注关系
     * 
     * @param ids 需要删除的用户关注关系主键集合
     * @return 结果
     */
    public int deleteWeightUserFollowByIds(Long[] ids);

    /**
     * 删除用户关注关系信息
     * 
     * @param id 用户关注关系主键
     * @return 结果
     */
    public int deleteWeightUserFollowById(Long id);
}
