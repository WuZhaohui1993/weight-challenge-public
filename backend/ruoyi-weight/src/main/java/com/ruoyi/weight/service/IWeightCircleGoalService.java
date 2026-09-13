package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightCircleGoal;

/**
 * 圈子目标Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightCircleGoalService 
{
    /**
     * 查询圈子目标
     * 
     * @param id 圈子目标主键
     * @return 圈子目标
     */
    public WeightCircleGoal selectWeightCircleGoalById(Long id);

    /**
     * 查询圈子目标列表
     * 
     * @param weightCircleGoal 圈子目标
     * @return 圈子目标集合
     */
    public List<WeightCircleGoal> selectWeightCircleGoalList(WeightCircleGoal weightCircleGoal);

    /**
     * 新增圈子目标
     * 
     * @param weightCircleGoal 圈子目标
     * @return 结果
     */
    public int insertWeightCircleGoal(WeightCircleGoal weightCircleGoal);

    /**
     * 修改圈子目标
     * 
     * @param weightCircleGoal 圈子目标
     * @return 结果
     */
    public int updateWeightCircleGoal(WeightCircleGoal weightCircleGoal);

    /**
     * 批量删除圈子目标
     * 
     * @param ids 需要删除的圈子目标主键集合
     * @return 结果
     */
    public int deleteWeightCircleGoalByIds(Long[] ids);

    /**
     * 删除圈子目标信息
     * 
     * @param id 圈子目标主键
     * @return 结果
     */
    public int deleteWeightCircleGoalById(Long id);
}
