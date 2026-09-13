package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightHabit;

/**
 * 习惯定义Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightHabitMapper 
{
    /**
     * 查询习惯定义
     * 
     * @param id 习惯定义主键
     * @return 习惯定义
     */
    public WeightHabit selectWeightHabitById(Long id);

    /**
     * 查询习惯定义列表
     * 
     * @param weightHabit 习惯定义
     * @return 习惯定义集合
     */
    public List<WeightHabit> selectWeightHabitList(WeightHabit weightHabit);

    /**
     * 新增习惯定义
     * 
     * @param weightHabit 习惯定义
     * @return 结果
     */
    public int insertWeightHabit(WeightHabit weightHabit);

    /**
     * 修改习惯定义
     * 
     * @param weightHabit 习惯定义
     * @return 结果
     */
    public int updateWeightHabit(WeightHabit weightHabit);

    /**
     * 删除习惯定义
     * 
     * @param id 习惯定义主键
     * @return 结果
     */
    public int deleteWeightHabitById(Long id);

    /**
     * 批量删除习惯定义
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightHabitByIds(Long[] ids);
}
