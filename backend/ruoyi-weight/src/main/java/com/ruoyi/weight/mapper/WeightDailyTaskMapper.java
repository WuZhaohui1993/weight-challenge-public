package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightDailyTask;

/**
 * 每日任务Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightDailyTaskMapper 
{
    /**
     * 查询每日任务
     * 
     * @param id 每日任务主键
     * @return 每日任务
     */
    public WeightDailyTask selectWeightDailyTaskById(Long id);

    /**
     * 查询每日任务列表
     * 
     * @param weightDailyTask 每日任务
     * @return 每日任务集合
     */
    public List<WeightDailyTask> selectWeightDailyTaskList(WeightDailyTask weightDailyTask);

    /**
     * 新增每日任务
     * 
     * @param weightDailyTask 每日任务
     * @return 结果
     */
    public int insertWeightDailyTask(WeightDailyTask weightDailyTask);

    /**
     * 修改每日任务
     * 
     * @param weightDailyTask 每日任务
     * @return 结果
     */
    public int updateWeightDailyTask(WeightDailyTask weightDailyTask);

    /**
     * 删除每日任务
     * 
     * @param id 每日任务主键
     * @return 结果
     */
    public int deleteWeightDailyTaskById(Long id);

    /**
     * 批量删除每日任务
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightDailyTaskByIds(Long[] ids);
}
