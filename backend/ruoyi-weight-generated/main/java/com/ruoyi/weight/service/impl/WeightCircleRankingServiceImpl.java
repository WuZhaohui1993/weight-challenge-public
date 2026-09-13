package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleRankingMapper;
import com.ruoyi.weight.domain.WeightCircleRanking;
import com.ruoyi.weight.service.IWeightCircleRankingService;

/**
 * 排行榜缓存Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleRankingServiceImpl implements IWeightCircleRankingService 
{
    @Autowired
    private WeightCircleRankingMapper weightCircleRankingMapper;

    /**
     * 查询排行榜缓存
     * 
     * @param id 排行榜缓存主键
     * @return 排行榜缓存
     */
    @Override
    public WeightCircleRanking selectWeightCircleRankingById(Long id)
    {
        return weightCircleRankingMapper.selectWeightCircleRankingById(id);
    }

    /**
     * 查询排行榜缓存列表
     * 
     * @param weightCircleRanking 排行榜缓存
     * @return 排行榜缓存
     */
    @Override
    public List<WeightCircleRanking> selectWeightCircleRankingList(WeightCircleRanking weightCircleRanking)
    {
        return weightCircleRankingMapper.selectWeightCircleRankingList(weightCircleRanking);
    }

    /**
     * 新增排行榜缓存
     * 
     * @param weightCircleRanking 排行榜缓存
     * @return 结果
     */
    @Override
    public int insertWeightCircleRanking(WeightCircleRanking weightCircleRanking)
    {
        return weightCircleRankingMapper.insertWeightCircleRanking(weightCircleRanking);
    }

    /**
     * 修改排行榜缓存
     * 
     * @param weightCircleRanking 排行榜缓存
     * @return 结果
     */
    @Override
    public int updateWeightCircleRanking(WeightCircleRanking weightCircleRanking)
    {
        weightCircleRanking.setUpdateTime(DateUtils.getNowDate());
        return weightCircleRankingMapper.updateWeightCircleRanking(weightCircleRanking);
    }

    /**
     * 批量删除排行榜缓存
     * 
     * @param ids 需要删除的排行榜缓存主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleRankingByIds(Long[] ids)
    {
        return weightCircleRankingMapper.deleteWeightCircleRankingByIds(ids);
    }

    /**
     * 删除排行榜缓存信息
     * 
     * @param id 排行榜缓存主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleRankingById(Long id)
    {
        return weightCircleRankingMapper.deleteWeightCircleRankingById(id);
    }
}
