package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightWaterRecordMapper;
import com.ruoyi.weight.domain.WeightWaterRecord;
import com.ruoyi.weight.service.IWeightWaterRecordService;

/**
 * 饮水记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightWaterRecordServiceImpl implements IWeightWaterRecordService 
{
    @Autowired
    private WeightWaterRecordMapper weightWaterRecordMapper;

    /**
     * 查询饮水记录
     * 
     * @param id 饮水记录主键
     * @return 饮水记录
     */
    @Override
    public WeightWaterRecord selectWeightWaterRecordById(Long id)
    {
        return weightWaterRecordMapper.selectWeightWaterRecordById(id);
    }

    /**
     * 查询饮水记录列表
     * 
     * @param weightWaterRecord 饮水记录
     * @return 饮水记录
     */
    @Override
    public List<WeightWaterRecord> selectWeightWaterRecordList(WeightWaterRecord weightWaterRecord)
    {
        return weightWaterRecordMapper.selectWeightWaterRecordList(weightWaterRecord);
    }

    /**
     * 新增饮水记录
     * 
     * @param weightWaterRecord 饮水记录
     * @return 结果
     */
    @Override
    public int insertWeightWaterRecord(WeightWaterRecord weightWaterRecord)
    {
        weightWaterRecord.setCreateTime(DateUtils.getNowDate());
        return weightWaterRecordMapper.insertWeightWaterRecord(weightWaterRecord);
    }

    /**
     * 修改饮水记录
     * 
     * @param weightWaterRecord 饮水记录
     * @return 结果
     */
    @Override
    public int updateWeightWaterRecord(WeightWaterRecord weightWaterRecord)
    {
        weightWaterRecord.setUpdateTime(DateUtils.getNowDate());
        return weightWaterRecordMapper.updateWeightWaterRecord(weightWaterRecord);
    }

    /**
     * 批量删除饮水记录
     * 
     * @param ids 需要删除的饮水记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightWaterRecordByIds(Long[] ids)
    {
        return weightWaterRecordMapper.deleteWeightWaterRecordByIds(ids);
    }

    /**
     * 删除饮水记录信息
     * 
     * @param id 饮水记录主键
     * @return 结果
     */
    @Override
    public int deleteWeightWaterRecordById(Long id)
    {
        return weightWaterRecordMapper.deleteWeightWaterRecordById(id);
    }
}
