package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightDailyTask;

/**
 * 每日任务Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightDailyTaskService 
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
     * 批量删除每日任务
     * 
     * @param ids 需要删除的每日任务主键集合
     * @return 结果
     */
    public int deleteWeightDailyTaskByIds(Long[] ids);

    /**
     * 删除每日任务信息
     * 
     * @param id 每日任务主键
     * @return 结果
     */
    public int deleteWeightDailyTaskById(Long id);
}
