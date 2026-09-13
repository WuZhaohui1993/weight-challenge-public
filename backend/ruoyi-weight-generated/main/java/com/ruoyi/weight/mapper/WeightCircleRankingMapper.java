package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircleRanking;

/**
 * 排行榜缓存Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightCircleRankingMapper 
{
    /**
     * 查询排行榜缓存
     * 
     * @param id 排行榜缓存主键
     * @return 排行榜缓存
     */
    public WeightCircleRanking selectWeightCircleRankingById(Long id);

    /**
     * 查询排行榜缓存列表
     * 
     * @param weightCircleRanking 排行榜缓存
     * @return 排行榜缓存集合
     */
    public List<WeightCircleRanking> selectWeightCircleRankingList(WeightCircleRanking weightCircleRanking);

    /**
     * 新增排行榜缓存
     * 
     * @param weightCircleRanking 排行榜缓存
     * @return 结果
     */
    public int insertWeightCircleRanking(WeightCircleRanking weightCircleRanking);

    /**
     * 修改排行榜缓存
     * 
     * @param weightCircleRanking 排行榜缓存
     * @return 结果
     */
    public int updateWeightCircleRanking(WeightCircleRanking weightCircleRanking);

    /**
     * 删除排行榜缓存
     * 
     * @param id 排行榜缓存主键
     * @return 结果
     */
    public int deleteWeightCircleRankingById(Long id);

    /**
     * 批量删除排行榜缓存
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightCircleRankingByIds(Long[] ids);
}
