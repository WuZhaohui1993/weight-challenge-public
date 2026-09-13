package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightFoodRecord;

/**
 * 饮食记录Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightFoodRecordService 
{
    /**
     * 查询饮食记录
     * 
     * @param id 饮食记录主键
     * @return 饮食记录
     */
    public WeightFoodRecord selectWeightFoodRecordById(Long id);

    /**
     * 查询饮食记录列表
     * 
     * @param weightFoodRecord 饮食记录
     * @return 饮食记录集合
     */
    public List<WeightFoodRecord> selectWeightFoodRecordList(WeightFoodRecord weightFoodRecord);

    /**
     * 新增饮食记录
     * 
     * @param weightFoodRecord 饮食记录
     * @return 结果
     */
    public int insertWeightFoodRecord(WeightFoodRecord weightFoodRecord);

    /**
     * 修改饮食记录
     * 
     * @param weightFoodRecord 饮食记录
     * @return 结果
     */
    public int updateWeightFoodRecord(WeightFoodRecord weightFoodRecord);

    /**
     * 批量删除饮食记录
     * 
     * @param ids 需要删除的饮食记录主键集合
     * @return 结果
     */
    public int deleteWeightFoodRecordByIds(Long[] ids);

    /**
     * 删除饮食记录信息
     * 
     * @param id 饮食记录主键
     * @return 结果
     */
    public int deleteWeightFoodRecordById(Long id);
}
