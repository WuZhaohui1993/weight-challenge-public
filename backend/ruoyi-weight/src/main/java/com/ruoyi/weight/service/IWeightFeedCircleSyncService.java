package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightFeedCircleSync;

/**
 * 动态同步圈子关系Service接口
 *
 * @author ruoyi
 * @date 2026-03-24
 */
public interface IWeightFeedCircleSyncService
{
    /**
     * 查询动态同步圈子关系列表
     *
     * @param weightFeedCircleSync 动态同步圈子关系
     * @return 动态同步圈子关系集合
     */
    public List<WeightFeedCircleSync> selectWeightFeedCircleSyncList(WeightFeedCircleSync weightFeedCircleSync);

    /**
     * 按动态ID批量查询同步圈子关系
     *
     * @param feedIds 动态ID集合
     * @return 动态同步圈子关系集合
     */
    public List<WeightFeedCircleSync> selectWeightFeedCircleSyncByFeedIds(List<Long> feedIds);

    /**
     * 新增动态同步圈子关系
     *
     * @param weightFeedCircleSync 动态同步圈子关系
     * @return 结果
     */
    public int insertWeightFeedCircleSync(WeightFeedCircleSync weightFeedCircleSync);

    /**
     * 根据动态ID删除同步圈子关系
     *
     * @param feedId 动态ID
     * @return 结果
     */
    public int deleteWeightFeedCircleSyncByFeedId(Long feedId);
}
