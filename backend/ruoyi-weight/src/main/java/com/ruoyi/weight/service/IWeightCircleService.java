package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircle;

/**
 * 圈子主Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightCircleService 
{
    /**
     * 查询圈子主
     * 
     * @param id 圈子主主键
     * @return 圈子主
     */
    public WeightCircle selectWeightCircleById(Long id);

    /**
     * 查询圈子主列表
     * 
     * @param weightCircle 圈子主
     * @return 圈子主集合
     */
    public List<WeightCircle> selectWeightCircleList(WeightCircle weightCircle);

    /**
     * 新增圈子主
     * 
     * @param weightCircle 圈子主
     * @return 结果
     */
    public int insertWeightCircle(WeightCircle weightCircle);

    /**
     * 修改圈子主
     * 
     * @param weightCircle 圈子主
     * @return 结果
     */
    public int updateWeightCircle(WeightCircle weightCircle);

    /**
     * 批量删除圈子主
     * 
     * @param ids 需要删除的圈子主主键集合
     * @return 结果
     */
    public int deleteWeightCircleByIds(Long[] ids);

    /**
     * 删除圈子主信息
     * 
     * @param id 圈子主主键
     * @return 结果
     */
    public int deleteWeightCircleById(Long id);
}
