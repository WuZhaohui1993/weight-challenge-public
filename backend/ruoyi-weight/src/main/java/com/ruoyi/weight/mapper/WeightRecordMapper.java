package com.ruoyi.weight.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.weight.domain.WeightRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 体重记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightRecordMapper 
{
    /**
     * 查询体重记录
     * 
     * @param id 体重记录主键
     * @return 体重记录
     */
    public WeightRecord selectWeightRecordById(Long id);

    /**
     * 查询体重记录列表
     * 
     * @param weightRecord 体重记录
     * @return 体重记录集合
     */
    public List<WeightRecord> selectWeightRecordList(WeightRecord weightRecord);

    /**
     * 新增体重记录
     * 
     * @param weightRecord 体重记录
     * @return 结果
     */
    public int insertWeightRecord(WeightRecord weightRecord);

    /**
     * 修改体重记录
     * 
     * @param weightRecord 体重记录
     * @return 结果
     */
    public int updateWeightRecord(WeightRecord weightRecord);

    /**
     * 按身高刷新用户历史 BMI 快照
     *
     * @param userId 用户ID
     * @param height 身高(cm)
     * @param updateBy 更新人
     * @return 结果
     */
    public int refreshBmiSnapshotsByUserId(@Param("userId") Long userId,
                                           @Param("height") BigDecimal height,
                                           @Param("updateBy") String updateBy);

    /**
     * 清空用户历史 BMI 快照
     *
     * @param userId 用户ID
     * @param updateBy 更新人
     * @return 结果
     */
    public int clearBmiSnapshotsByUserId(@Param("userId") Long userId, @Param("updateBy") String updateBy);

    /**
     * 删除体重记录
     * 
     * @param id 体重记录主键
     * @return 结果
     */
    public int deleteWeightRecordById(Long id);

    /**
     * 批量删除体重记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightRecordByIds(Long[] ids);
}
