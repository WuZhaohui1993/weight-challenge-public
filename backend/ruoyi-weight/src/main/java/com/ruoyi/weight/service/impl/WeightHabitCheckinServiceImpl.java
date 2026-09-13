package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightHabitCheckinMapper;
import com.ruoyi.weight.domain.WeightHabitCheckin;
import com.ruoyi.weight.service.IWeightHabitCheckinService;

/**
 * 习惯打卡记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightHabitCheckinServiceImpl implements IWeightHabitCheckinService 
{
    @Autowired
    private WeightHabitCheckinMapper weightHabitCheckinMapper;

    /**
     * 查询习惯打卡记录
     * 
     * @param id 习惯打卡记录主键
     * @return 习惯打卡记录
     */
    @Override
    public WeightHabitCheckin selectWeightHabitCheckinById(Long id)
    {
        return weightHabitCheckinMapper.selectWeightHabitCheckinById(id);
    }

    /**
     * 查询习惯打卡记录列表
     * 
     * @param weightHabitCheckin 习惯打卡记录
     * @return 习惯打卡记录
     */
    @Override
    public List<WeightHabitCheckin> selectWeightHabitCheckinList(WeightHabitCheckin weightHabitCheckin)
    {
        return weightHabitCheckinMapper.selectWeightHabitCheckinList(weightHabitCheckin);
    }

    /**
     * 新增习惯打卡记录
     * 
     * @param weightHabitCheckin 习惯打卡记录
     * @return 结果
     */
    @Override
    public int insertWeightHabitCheckin(WeightHabitCheckin weightHabitCheckin)
    {
        weightHabitCheckin.setCreateTime(DateUtils.getNowDate());
        return weightHabitCheckinMapper.insertWeightHabitCheckin(weightHabitCheckin);
    }

    /**
     * 修改习惯打卡记录
     * 
     * @param weightHabitCheckin 习惯打卡记录
     * @return 结果
     */
    @Override
    public int updateWeightHabitCheckin(WeightHabitCheckin weightHabitCheckin)
    {
        return weightHabitCheckinMapper.updateWeightHabitCheckin(weightHabitCheckin);
    }

    /**
     * 批量删除习惯打卡记录
     * 
     * @param ids 需要删除的习惯打卡记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightHabitCheckinByIds(Long[] ids)
    {
        return weightHabitCheckinMapper.deleteWeightHabitCheckinByIds(ids);
    }

    /**
     * 删除习惯打卡记录信息
     * 
     * @param id 习惯打卡记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightHabitCheckinById(Long id)
    {
        return weightHabitCheckinMapper.deleteWeightHabitCheckinById(id);
    }
}
