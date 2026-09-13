package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.domain.WeightFeedCircleSync;
import com.ruoyi.weight.mapper.WeightFeedCircleSyncMapper;
import com.ruoyi.weight.service.IWeightFeedCircleSyncService;

/**
 * 动态同步圈子关系Service业务层处理
 *
 * @author ruoyi
 * @date 2026-03-24
 */
@Service
public class WeightFeedCircleSyncServiceImpl implements IWeightFeedCircleSyncService
{
    @Autowired
    private WeightFeedCircleSyncMapper weightFeedCircleSyncMapper;

    @Override
    public List<WeightFeedCircleSync> selectWeightFeedCircleSyncList(WeightFeedCircleSync weightFeedCircleSync)
    {
        return weightFeedCircleSyncMapper.selectWeightFeedCircleSyncList(weightFeedCircleSync);
    }

    @Override
    public List<WeightFeedCircleSync> selectWeightFeedCircleSyncByFeedIds(List<Long> feedIds)
    {
        return weightFeedCircleSyncMapper.selectWeightFeedCircleSyncByFeedIds(feedIds);
    }

    @Override
    public int insertWeightFeedCircleSync(WeightFeedCircleSync weightFeedCircleSync)
    {
        weightFeedCircleSync.setCreateTime(DateUtils.getNowDate());
        return weightFeedCircleSyncMapper.insertWeightFeedCircleSync(weightFeedCircleSync);
    }

    @Override
    public int deleteWeightFeedCircleSyncByFeedId(Long feedId)
    {
        return weightFeedCircleSyncMapper.deleteWeightFeedCircleSyncByFeedId(feedId);
    }
}
