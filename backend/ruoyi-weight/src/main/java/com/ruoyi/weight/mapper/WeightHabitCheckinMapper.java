package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightHabitCheckin;

/**
 * 习惯打卡记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightHabitCheckinMapper 
{
    /**
     * 查询习惯打卡记录
     * 
     * @param id 习惯打卡记录主键
     * @return 习惯打卡记录
     */
    public WeightHabitCheckin selectWeightHabitCheckinById(Long id);

    /**
     * 查询习惯打卡记录列表
     * 
     * @param weightHabitCheckin 习惯打卡记录
     * @return 习惯打卡记录集合
     */
    public List<WeightHabitCheckin> selectWeightHabitCheckinList(WeightHabitCheckin weightHabitCheckin);

    /**
     * 新增习惯打卡记录
     * 
     * @param weightHabitCheckin 习惯打卡记录
     * @return 结果
     */
    public int insertWeightHabitCheckin(WeightHabitCheckin weightHabitCheckin);

    /**
     * 修改习惯打卡记录
     * 
     * @param weightHabitCheckin 习惯打卡记录
     * @return 结果
     */
    public int updateWeightHabitCheckin(WeightHabitCheckin weightHabitCheckin);

    /**
     * 删除习惯打卡记录
     * 
     * @param id 习惯打卡记录主键
     * @return 结果
     */
    public int deleteWeightHabitCheckinById(Long id);

    /**
     * 批量删除习惯打卡记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightHabitCheckinByIds(Long[] ids);
}
