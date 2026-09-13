package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightExerciseRecord;

/**
 * 运动记录Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightExerciseRecordService 
{
    /**
     * 查询运动记录
     * 
     * @param id 运动记录主键
     * @return 运动记录
     */
    public WeightExerciseRecord selectWeightExerciseRecordById(Long id);

    /**
     * 查询运动记录列表
     * 
     * @param weightExerciseRecord 运动记录
     * @return 运动记录集合
     */
    public List<WeightExerciseRecord> selectWeightExerciseRecordList(WeightExerciseRecord weightExerciseRecord);

    /**
     * 新增运动记录
     * 
     * @param weightExerciseRecord 运动记录
     * @return 结果
     */
    public int insertWeightExerciseRecord(WeightExerciseRecord weightExerciseRecord);

    /**
     * 修改运动记录
     * 
     * @param weightExerciseRecord 运动记录
     * @return 结果
     */
    public int updateWeightExerciseRecord(WeightExerciseRecord weightExerciseRecord);

    /**
     * 批量删除运动记录
     * 
     * @param ids 需要删除的运动记录主键集合
     * @return 结果
     */
    public int deleteWeightExerciseRecordByIds(Long[] ids);

    /**
     * 删除运动记录信息
     * 
     * @param id 运动记录主键
     * @return 结果
     */
    public int deleteWeightExerciseRecordById(Long id);
}
