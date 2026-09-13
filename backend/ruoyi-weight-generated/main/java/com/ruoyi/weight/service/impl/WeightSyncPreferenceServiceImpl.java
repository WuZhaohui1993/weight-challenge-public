package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightSyncPreferenceMapper;
import com.ruoyi.weight.domain.WeightSyncPreference;
import com.ruoyi.weight.service.IWeightSyncPreferenceService;

/**
 * 同步偏好设置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightSyncPreferenceServiceImpl implements IWeightSyncPreferenceService 
{
    @Autowired
    private WeightSyncPreferenceMapper weightSyncPreferenceMapper;

    /**
     * 查询同步偏好设置
     * 
     * @param id 同步偏好设置主键
     * @return 同步偏好设置
     */
    @Override
    public WeightSyncPreference selectWeightSyncPreferenceById(Long id)
    {
        return weightSyncPreferenceMapper.selectWeightSyncPreferenceById(id);
    }

    /**
     * 查询同步偏好设置列表
     * 
     * @param weightSyncPreference 同步偏好设置
     * @return 同步偏好设置
     */
    @Override
    public List<WeightSyncPreference> selectWeightSyncPreferenceList(WeightSyncPreference weightSyncPreference)
    {
        return weightSyncPreferenceMapper.selectWeightSyncPreferenceList(weightSyncPreference);
    }

    /**
     * 新增同步偏好设置
     * 
     * @param weightSyncPreference 同步偏好设置
     * @return 结果
     */
    @Override
    public int insertWeightSyncPreference(WeightSyncPreference weightSyncPreference)
    {
        weightSyncPreference.setCreateTime(DateUtils.getNowDate());
        return weightSyncPreferenceMapper.insertWeightSyncPreference(weightSyncPreference);
    }

    /**
     * 修改同步偏好设置
     * 
     * @param weightSyncPreference 同步偏好设置
     * @return 结果
     */
    @Override
    public int updateWeightSyncPreference(WeightSyncPreference weightSyncPreference)
    {
        weightSyncPreference.setUpdateTime(DateUtils.getNowDate());
        return weightSyncPreferenceMapper.updateWeightSyncPreference(weightSyncPreference);
    }

    /**
     * 批量删除同步偏好设置
     * 
     * @param ids 需要删除的同步偏好设置主键
     * @return 结果
     */
    @Override
    public int deleteWeightSyncPreferenceByIds(Long[] ids)
    {
        return weightSyncPreferenceMapper.deleteWeightSyncPreferenceByIds(ids);
    }

    /**
     * 删除同步偏好设置信息
     * 
     * @param id 同步偏好设置主键
     * @return 结果
     */
    @Override
    public int deleteWeightSyncPreferenceById(Long id)
    {
        return weightSyncPreferenceMapper.deleteWeightSyncPreferenceById(id);
    }
}
