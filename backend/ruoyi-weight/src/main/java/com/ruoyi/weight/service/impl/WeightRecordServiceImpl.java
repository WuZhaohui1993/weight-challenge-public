package com.ruoyi.weight.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.mapper.WeightRecordMapper;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.service.IWeightRecordService;
import com.ruoyi.weight.service.IWeightUserService;

/**
 * 体重记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightRecordServiceImpl implements IWeightRecordService 
{
    private static final String BODY_METRIC_UPDATE_BY = "weight-record";

    @Autowired
    private WeightRecordMapper weightRecordMapper;

    @Autowired
    private IWeightUserService weightUserService;

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
    @Transactional(rollbackFor = Exception.class)
    public int insertWeightRecord(WeightRecord weightRecord)
    {
        populateBmiSnapshot(weightRecord, null);
        weightRecord.setCreateTime(DateUtils.getNowDate());
        int rows = weightRecordMapper.insertWeightRecord(weightRecord);
        if (rows > 0)
        {
            weightUserService.refreshCurrentWeightAndBmiByUserId(weightRecord.getUserId(), BODY_METRIC_UPDATE_BY);
        }
        return rows;
    }

    /**
     * 修改体重记录
     * 
     * @param weightRecord 体重记录
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWeightRecord(WeightRecord weightRecord)
    {
        WeightRecord existingRecord = weightRecord.getId() != null ? weightRecordMapper.selectWeightRecordById(weightRecord.getId()) : null;
        populateBmiSnapshot(weightRecord, existingRecord);
        weightRecord.setUpdateTime(DateUtils.getNowDate());
        int rows = weightRecordMapper.updateWeightRecord(weightRecord);
        if (rows > 0)
        {
            Set<Long> affectedUserIds = new LinkedHashSet<>();
            if (existingRecord != null && existingRecord.getUserId() != null)
            {
                affectedUserIds.add(existingRecord.getUserId());
            }
            if (weightRecord.getUserId() != null)
            {
                affectedUserIds.add(weightRecord.getUserId());
            }
            refreshAffectedUsers(affectedUserIds);
        }
        return rows;
    }

    /**
     * 批量删除体重记录
     * 
     * @param ids 需要删除的体重记录主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWeightRecordByIds(Long[] ids)
    {
        Set<Long> affectedUserIds = collectUserIds(ids);
        int rows = weightRecordMapper.deleteWeightRecordByIds(ids);
        if (rows > 0)
        {
            refreshAffectedUsers(affectedUserIds);
        }
        return rows;
    }

    /**
     * 删除体重记录信息
     * 
     * @param id 体重记录主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWeightRecordById(Long id)
    {
        WeightRecord existingRecord = weightRecordMapper.selectWeightRecordById(id);
        int rows = weightRecordMapper.deleteWeightRecordById(id);
        if (rows > 0 && existingRecord != null && existingRecord.getUserId() != null)
        {
            weightUserService.refreshCurrentWeightAndBmiByUserId(existingRecord.getUserId(), BODY_METRIC_UPDATE_BY);
        }
        return rows;
    }

    private Set<Long> collectUserIds(Long[] ids)
    {
        Set<Long> userIds = new LinkedHashSet<>();
        if (ids == null || ids.length == 0)
        {
            return userIds;
        }

        for (Long id : ids)
        {
            if (id == null)
            {
                continue;
            }
            WeightRecord record = weightRecordMapper.selectWeightRecordById(id);
            if (record != null && record.getUserId() != null)
            {
                userIds.add(record.getUserId());
            }
        }
        return userIds;
    }

    private void refreshAffectedUsers(Set<Long> userIds)
    {
        if (userIds == null || userIds.isEmpty())
        {
            return;
        }

        for (Long userId : userIds)
        {
            weightUserService.refreshCurrentWeightAndBmiByUserId(userId, BODY_METRIC_UPDATE_BY);
        }
    }

    private void populateBmiSnapshot(WeightRecord weightRecord, WeightRecord existingRecord)
    {
        if (weightRecord == null)
        {
            return;
        }

        Long userId = weightRecord.getUserId() != null
                ? weightRecord.getUserId()
                : existingRecord != null ? existingRecord.getUserId() : null;
        BigDecimal weight = weightRecord.getWeight() != null
                ? weightRecord.getWeight()
                : existingRecord != null ? existingRecord.getWeight() : null;

        if (userId == null)
        {
            return;
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        weightRecord.setBmi(calculateBmi(weight, weightUser != null ? weightUser.getHeight() : null));
    }

    private BigDecimal calculateBmi(BigDecimal currentWeight, BigDecimal height)
    {
        if (currentWeight == null || height == null || height.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }

        BigDecimal heightMeter = height.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal heightSquare = heightMeter.multiply(heightMeter);
        return currentWeight.divide(heightSquare, 1, java.math.RoundingMode.HALF_UP);
    }
}
