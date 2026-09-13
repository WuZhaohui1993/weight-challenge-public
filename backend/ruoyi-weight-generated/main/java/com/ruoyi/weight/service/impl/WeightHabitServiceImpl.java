package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightHabitMapper;
import com.ruoyi.weight.domain.WeightHabit;
import com.ruoyi.weight.service.IWeightHabitService;

/**
 * 习惯定义Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightHabitServiceImpl implements IWeightHabitService 
{
    @Autowired
    private WeightHabitMapper weightHabitMapper;

    /**
     * 查询习惯定义
     * 
     * @param id 习惯定义主键
     * @return 习惯定义
     */
    @Override
    public WeightHabit selectWeightHabitById(Long id)
    {
        return weightHabitMapper.selectWeightHabitById(id);
    }

    /**
     * 查询习惯定义列表
     * 
     * @param weightHabit 习惯定义
     * @return 习惯定义
     */
    @Override
    public List<WeightHabit> selectWeightHabitList(WeightHabit weightHabit)
    {
        return weightHabitMapper.selectWeightHabitList(weightHabit);
    }

    /**
     * 新增习惯定义
     * 
     * @param weightHabit 习惯定义
     * @return 结果
     */
    @Override
    public int insertWeightHabit(WeightHabit weightHabit)
    {
        weightHabit.setCreateTime(DateUtils.getNowDate());
        return weightHabitMapper.insertWeightHabit(weightHabit);
    }

    /**
     * 修改习惯定义
     * 
     * @param weightHabit 习惯定义
     * @return 结果
     */
    @Override
    public int updateWeightHabit(WeightHabit weightHabit)
    {
        weightHabit.setUpdateTime(DateUtils.getNowDate());
        return weightHabitMapper.updateWeightHabit(weightHabit);
    }

    /**
     * 批量删除习惯定义
     * 
     * @param ids 需要删除的习惯定义主键
     * @return 结果
     */
    @Override
    public int deleteWeightHabitByIds(Long[] ids)
    {
        return weightHabitMapper.deleteWeightHabitByIds(ids);
    }

    /**
     * 删除习惯定义信息
     * 
     * @param id 习惯定义主键
     * @return 结果
     */
    @Override
    public int deleteWeightHabitById(Long id)
    {
        return weightHabitMapper.deleteWeightHabitById(id);
    }
}
