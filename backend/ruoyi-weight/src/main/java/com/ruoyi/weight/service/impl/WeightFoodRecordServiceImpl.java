package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightFoodRecordMapper;
import com.ruoyi.weight.domain.WeightFoodRecord;
import com.ruoyi.weight.service.IWeightFoodRecordService;

/**
 * 饮食记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightFoodRecordServiceImpl implements IWeightFoodRecordService 
{
    @Autowired
    private WeightFoodRecordMapper weightFoodRecordMapper;

    /**
     * 查询饮食记录
     * 
     * @param id 饮食记录主键
     * @return 饮食记录
     */
    @Override
    public WeightFoodRecord selectWeightFoodRecordById(Long id)
    {
        return weightFoodRecordMapper.selectWeightFoodRecordById(id);
    }

    /**
     * 查询饮食记录列表
     * 
     * @param weightFoodRecord 饮食记录
     * @return 饮食记录
     */
    @Override
    public List<WeightFoodRecord> selectWeightFoodRecordList(WeightFoodRecord weightFoodRecord)
    {
        return weightFoodRecordMapper.selectWeightFoodRecordList(weightFoodRecord);
    }

    /**
     * 新增饮食记录
     * 
     * @param weightFoodRecord 饮食记录
     * @return 结果
     */
    @Override
    public int insertWeightFoodRecord(WeightFoodRecord weightFoodRecord)
    {
        weightFoodRecord.setCreateTime(DateUtils.getNowDate());
        return weightFoodRecordMapper.insertWeightFoodRecord(weightFoodRecord);
    }

    /**
     * 修改饮食记录
     * 
     * @param weightFoodRecord 饮食记录
     * @return 结果
     */
    @Override
    public int updateWeightFoodRecord(WeightFoodRecord weightFoodRecord)
    {
        weightFoodRecord.setUpdateTime(DateUtils.getNowDate());
        return weightFoodRecordMapper.updateWeightFoodRecord(weightFoodRecord);
    }

    /**
     * 批量删除饮食记录
     * 
     * @param ids 需要删除的饮食记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightFoodRecordByIds(Long[] ids)
    {
        return weightFoodRecordMapper.deleteWeightFoodRecordByIds(ids);
    }

    /**
     * 删除饮食记录信息
     * 
     * @param id 饮食记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightFoodRecordById(Long id)
    {
        return weightFoodRecordMapper.deleteWeightFoodRecordById(id);
    }
}
