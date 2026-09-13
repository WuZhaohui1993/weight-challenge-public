package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightDailyTaskMapper;
import com.ruoyi.weight.domain.WeightDailyTask;
import com.ruoyi.weight.service.IWeightDailyTaskService;

/**
 * 每日任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightDailyTaskServiceImpl implements IWeightDailyTaskService 
{
    @Autowired
    private WeightDailyTaskMapper weightDailyTaskMapper;

    /**
     * 查询每日任务
     * 
     * @param id 每日任务主键
     * @return 每日任务
     */
    @Override
    public WeightDailyTask selectWeightDailyTaskById(Long id)
    {
        return weightDailyTaskMapper.selectWeightDailyTaskById(id);
    }

    /**
     * 查询每日任务列表
     * 
     * @param weightDailyTask 每日任务
     * @return 每日任务
     */
    @Override
    public List<WeightDailyTask> selectWeightDailyTaskList(WeightDailyTask weightDailyTask)
    {
        return weightDailyTaskMapper.selectWeightDailyTaskList(weightDailyTask);
    }

    /**
     * 新增每日任务
     * 
     * @param weightDailyTask 每日任务
     * @return 结果
     */
    @Override
    public int insertWeightDailyTask(WeightDailyTask weightDailyTask)
    {
        weightDailyTask.setCreateTime(DateUtils.getNowDate());
        return weightDailyTaskMapper.insertWeightDailyTask(weightDailyTask);
    }

    /**
     * 修改每日任务
     * 
     * @param weightDailyTask 每日任务
     * @return 结果
     */
    @Override
    public int updateWeightDailyTask(WeightDailyTask weightDailyTask)
    {
        weightDailyTask.setUpdateTime(DateUtils.getNowDate());
        return weightDailyTaskMapper.updateWeightDailyTask(weightDailyTask);
    }

    /**
     * 批量删除每日任务
     * 
     * @param ids 需要删除的每日任务主键
     * @return 结果
     */
    @Override
    public int deleteWeightDailyTaskByIds(Long[] ids)
    {
        return weightDailyTaskMapper.deleteWeightDailyTaskByIds(ids);
    }

    /**
     * 删除每日任务信息
     * 
     * @param id 每日任务主键
     * @return 结果
     */
    @Override
    public int deleteWeightDailyTaskById(Long id)
    {
        return weightDailyTaskMapper.deleteWeightDailyTaskById(id);
    }
}
