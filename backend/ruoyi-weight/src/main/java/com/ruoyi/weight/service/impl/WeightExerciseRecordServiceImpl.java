package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightExerciseRecordMapper;
import com.ruoyi.weight.domain.WeightExerciseRecord;
import com.ruoyi.weight.service.IWeightExerciseRecordService;

/**
 * 运动记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightExerciseRecordServiceImpl implements IWeightExerciseRecordService 
{
    @Autowired
    private WeightExerciseRecordMapper weightExerciseRecordMapper;

    /**
     * 查询运动记录
     * 
     * @param id 运动记录主键
     * @return 运动记录
     */
    @Override
    public WeightExerciseRecord selectWeightExerciseRecordById(Long id)
    {
        return weightExerciseRecordMapper.selectWeightExerciseRecordById(id);
    }

    /**
     * 查询运动记录列表
     * 
     * @param weightExerciseRecord 运动记录
     * @return 运动记录
     */
    @Override
    public List<WeightExerciseRecord> selectWeightExerciseRecordList(WeightExerciseRecord weightExerciseRecord)
    {
        return weightExerciseRecordMapper.selectWeightExerciseRecordList(weightExerciseRecord);
    }

    /**
     * 新增运动记录
     * 
     * @param weightExerciseRecord 运动记录
     * @return 结果
     */
    @Override
    public int insertWeightExerciseRecord(WeightExerciseRecord weightExerciseRecord)
    {
        weightExerciseRecord.setCreateTime(DateUtils.getNowDate());
        return weightExerciseRecordMapper.insertWeightExerciseRecord(weightExerciseRecord);
    }

    /**
     * 修改运动记录
     * 
     * @param weightExerciseRecord 运动记录
     * @return 结果
     */
    @Override
    public int updateWeightExerciseRecord(WeightExerciseRecord weightExerciseRecord)
    {
        weightExerciseRecord.setUpdateTime(DateUtils.getNowDate());
        return weightExerciseRecordMapper.updateWeightExerciseRecord(weightExerciseRecord);
    }

    /**
     * 批量删除运动记录
     * 
     * @param ids 需要删除的运动记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightExerciseRecordByIds(Long[] ids)
    {
        return weightExerciseRecordMapper.deleteWeightExerciseRecordByIds(ids);
    }

    /**
     * 删除运动记录信息
     * 
     * @param id 运动记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightExerciseRecordById(Long id)
    {
        return weightExerciseRecordMapper.deleteWeightExerciseRecordById(id);
    }
}
