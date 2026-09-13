package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightRecordMapper;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.service.IWeightRecordService;

/**
 * 体重记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightRecordServiceImpl implements IWeightRecordService 
{
    @Autowired
    private WeightRecordMapper weightRecordMapper;

    /**
     * 查询体重记录
     * 
     * @param id 体重记录主键
     * @return 体重记录
     */
    @Override
    public WeightRecord selectWeightRecordById(Long id)
    {
        return weightRecordMapper.selectWeightRecordById(id);
    }

    /**
     * 查询体重记录列表
     * 
     * @param weightRecord 体重记录
     * @return 体重记录
     */
    @Override
    public List<WeightRecord> selectWeightRecordList(WeightRecord weightRecord)
    {
        return weightRecordMapper.selectWeightRecordList(weightRecord);
    }

    /**
     * 新增体重记录
     * 
     * @param weightRecord 体重记录
     * @return 结果
     */
    @Override
    public int insertWeightRecord(WeightRecord weightRecord)
    {
        weightRecord.setCreateTime(DateUtils.getNowDate());
        return weightRecordMapper.insertWeightRecord(weightRecord);
    }

    /**
     * 修改体重记录
     * 
     * @param weightRecord 体重记录
     * @return 结果
     */
    @Override
    public int updateWeightRecord(WeightRecord weightRecord)
    {
        weightRecord.setUpdateTime(DateUtils.getNowDate());
        return weightRecordMapper.updateWeightRecord(weightRecord);
    }

    /**
     * 批量删除体重记录
     * 
     * @param ids 需要删除的体重记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightRecordByIds(Long[] ids)
    {
        return weightRecordMapper.deleteWeightRecordByIds(ids);
    }

    /**
     * 删除体重记录信息
     * 
     * @param id 体重记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightRecordById(Long id)
    {
        return weightRecordMapper.deleteWeightRecordById(id);
    }
}
