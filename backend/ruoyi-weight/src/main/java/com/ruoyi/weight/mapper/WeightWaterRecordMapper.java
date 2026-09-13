package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightWaterRecord;

/**
 * 饮水记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightWaterRecordMapper 
{
    /**
     * 查询饮水记录
     * 
     * @param id 饮水记录主键
     * @return 饮水记录
     */
    public WeightWaterRecord selectWeightWaterRecordById(Long id);

    /**
     * 查询饮水记录列表
     * 
     * @param weightWaterRecord 饮水记录
     * @return 饮水记录集合
     */
    public List<WeightWaterRecord> selectWeightWaterRecordList(WeightWaterRecord weightWaterRecord);

    /**
     * 新增饮水记录
     * 
     * @param weightWaterRecord 饮水记录
     * @return 结果
     */
    public int insertWeightWaterRecord(WeightWaterRecord weightWaterRecord);

    /**
     * 修改饮水记录
     * 
     * @param weightWaterRecord 饮水记录
     * @return 结果
     */
    public int updateWeightWaterRecord(WeightWaterRecord weightWaterRecord);

    /**
     * 删除饮水记录
     * 
     * @param id 饮水记录主键
     * @return 结果
     */
    public int deleteWeightWaterRecordById(Long id);

    /**
     * 批量删除饮水记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightWaterRecordByIds(Long[] ids);
}
