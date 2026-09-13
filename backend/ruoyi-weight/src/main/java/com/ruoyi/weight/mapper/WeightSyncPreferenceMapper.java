package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightSyncPreference;

/**
 * 同步偏好设置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface WeightSyncPreferenceMapper 
{
    /**
     * 查询同步偏好设置
     * 
     * @param id 同步偏好设置主键
     * @return 同步偏好设置
     */
    public WeightSyncPreference selectWeightSyncPreferenceById(Long id);

    /**
     * 查询同步偏好设置列表
     * 
     * @param weightSyncPreference 同步偏好设置
     * @return 同步偏好设置集合
     */
    public List<WeightSyncPreference> selectWeightSyncPreferenceList(WeightSyncPreference weightSyncPreference);

    /**
     * 新增同步偏好设置
     * 
     * @param weightSyncPreference 同步偏好设置
     * @return 结果
     */
    public int insertWeightSyncPreference(WeightSyncPreference weightSyncPreference);

    /**
     * 修改同步偏好设置
     * 
     * @param weightSyncPreference 同步偏好设置
     * @return 结果
     */
    public int updateWeightSyncPreference(WeightSyncPreference weightSyncPreference);

    /**
     * 删除同步偏好设置
     * 
     * @param id 同步偏好设置主键
     * @return 结果
     */
    public int deleteWeightSyncPreferenceById(Long id);

    /**
     * 批量删除同步偏好设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWeightSyncPreferenceByIds(Long[] ids);
}
