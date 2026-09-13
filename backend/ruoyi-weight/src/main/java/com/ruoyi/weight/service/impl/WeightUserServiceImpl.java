package com.ruoyi.weight.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightRecordMapper;
import com.ruoyi.weight.mapper.WeightUserMapper;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightUserService;
import com.ruoyi.weight.support.WeightUserGoalCalculator;

/**
 * 用户扩展信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightUserServiceImpl implements IWeightUserService 
{
    @Autowired
    private WeightUserMapper weightUserMapper;

    @Autowired
    private WeightRecordMapper weightRecordMapper;

    /**
     * 查询用户扩展信息
     * 
     * @param userId 用户扩展信息主键
     * @return 用户扩展信息
     */
    @Override
    public WeightUser selectWeightUserByUserId(Long userId)
    {
        return weightUserMapper.selectWeightUserByUserId(userId);
    }

    /**
     * 查询用户扩展信息列表
     * 
     * @param weightUser 用户扩展信息
     * @return 用户扩展信息
     */
    @Override
    public List<WeightUser> selectWeightUserList(WeightUser weightUser)
    {
        return weightUserMapper.selectWeightUserList(weightUser);
    }

    /**
     * 新增用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    @Override
    public int insertWeightUser(WeightUser weightUser)
    {
        weightUser.setCreateTime(DateUtils.getNowDate());
        weightUser.setBmi(calculateBmi(weightUser.getCurrentWeight(), weightUser.getHeight()));
        weightUser.setDailyCalorieDeficitTarget(WeightUserGoalCalculator.calculateDailyCalorieDeficit(weightUser));
        weightUser.setDailyCalorieTarget(WeightUserGoalCalculator.calculateDailyCalorieTarget(weightUser));
        return weightUserMapper.insertWeightUser(weightUser);
    }

    /**
     * 修改用户扩展信息
     * 
     * @param weightUser 用户扩展信息
     * @return 结果
     */
    @Override
    public int updateWeightUser(WeightUser weightUser)
    {
        BigDecimal mergedHeight;
        BigDecimal mergedCurrentWeight;
        BigDecimal mergedTargetWeight;
        Date mergedTargetCompletionDate;
        Date mergedBirthday;
        String mergedGender;
        WeightUser existingUser = weightUser.getUserId() != null
                ? weightUserMapper.selectWeightUserByUserId(weightUser.getUserId())
                : null;
        if (existingUser != null)
        {
            mergedHeight = weightUser.getHeight() != null ? weightUser.getHeight() : existingUser.getHeight();
            mergedCurrentWeight = weightUser.getCurrentWeight() != null ? weightUser.getCurrentWeight() : existingUser.getCurrentWeight();
            mergedTargetWeight = weightUser.getTargetWeight() != null ? weightUser.getTargetWeight() : existingUser.getTargetWeight();
            mergedTargetCompletionDate = weightUser.getTargetCompletionDate() != null
                    ? weightUser.getTargetCompletionDate()
                    : existingUser.getTargetCompletionDate();
            mergedBirthday = weightUser.getBirthday() != null ? weightUser.getBirthday() : existingUser.getBirthday();
            mergedGender = weightUser.getGender() != null ? weightUser.getGender() : existingUser.getGender();
            weightUser.setBmi(calculateBmi(mergedCurrentWeight, mergedHeight));
        }
        else
        {
            mergedHeight = weightUser.getHeight();
            mergedCurrentWeight = weightUser.getCurrentWeight();
            mergedTargetWeight = weightUser.getTargetWeight();
            mergedTargetCompletionDate = weightUser.getTargetCompletionDate();
            mergedBirthday = weightUser.getBirthday();
            mergedGender = weightUser.getGender();
            weightUser.setBmi(calculateBmi(weightUser.getCurrentWeight(), weightUser.getHeight()));
        }

        weightUser.setDailyCalorieDeficitTarget(WeightUserGoalCalculator.calculateDailyCalorieDeficit(
                mergedCurrentWeight,
                mergedTargetWeight,
                mergedTargetCompletionDate
        ));
        weightUser.setDailyCalorieTarget(WeightUserGoalCalculator.calculateDailyCalorieTarget(
                mergedCurrentWeight,
                mergedHeight,
                mergedBirthday,
                mergedGender,
                mergedTargetWeight,
                mergedTargetCompletionDate
        ));
        boolean heightChanged = existingUser != null && hasBigDecimalChanged(existingUser.getHeight(), mergedHeight);
        weightUser.setUpdateTime(DateUtils.getNowDate());
        int rows = weightUserMapper.updateWeightUser(weightUser);
        if (rows > 0 && weightUser.getUserId() != null && heightChanged)
        {
            syncHistoricalBmiSnapshots(weightUser.getUserId(), mergedHeight, weightUser.getUpdateBy());
        }
        return rows;
    }

    /**
     * 清空用户主圈子
     *
     * @param userId 用户ID
     * @param updateBy 更新人
     * @return 结果
     */
    @Override
    public int clearMainCircleIdByUserId(Long userId, String updateBy)
    {
        return weightUserMapper.clearMainCircleIdByUserId(userId, updateBy);
    }

    /**
     * 设置用户主圈子
     *
     * @param userId 用户ID
     * @param mainCircleId 主圈子ID
     * @param updateBy 更新人
     * @return 结果
     */
    @Override
    public int updateMainCircleIdByUserId(Long userId, Long mainCircleId, String updateBy)
    {
        return weightUserMapper.updateMainCircleIdByUserId(userId, mainCircleId, updateBy);
    }

    /**
     * 按最新体重记录刷新用户当前体重与 BMI
     *
     * @param userId 用户ID
     * @param updateBy 更新人
     * @return 结果
     */
    @Override
    public int refreshCurrentWeightAndBmiByUserId(Long userId, String updateBy)
    {
        if (userId == null)
        {
            return 0;
        }

        WeightUser existingUser = weightUserMapper.selectWeightUserByUserId(userId);
        if (existingUser == null)
        {
            return 0;
        }

        WeightRecord latestRecord = findLatestWeightRecord(userId);
        WeightUser weightUser = new WeightUser();
        weightUser.setUserId(userId);
        weightUser.setUpdateBy(updateBy);
        if (latestRecord != null)
        {
            weightUser.setCurrentWeight(latestRecord.getWeight());
            weightUser.setBmi(calculateBmi(latestRecord.getWeight(), existingUser.getHeight()));
            return updateWeightUser(weightUser);
        }

        return weightUserMapper.clearCurrentWeightAndBmiByUserId(userId, updateBy);
    }

    /**
     * 批量删除用户扩展信息
     * 
     * @param userIds 需要删除的用户扩展信息主键
     * @return 结果
     */
    @Override
    public int deleteWeightUserByUserIds(Long[] userIds)
    {
        return weightUserMapper.deleteWeightUserByUserIds(userIds);
    }

    /**
     * 删除用户扩展信息信息
     * 
     * @param userId 用户扩展信息主键
     * @return 结果
     */
    @Override
    public int deleteWeightUserByUserId(Long userId)
    {
        return weightUserMapper.deleteWeightUserByUserId(userId);
    }

    private WeightRecord findLatestWeightRecord(Long userId)
    {
        WeightRecord query = new WeightRecord();
        query.setUserId(userId);
        List<WeightRecord> records = weightRecordMapper.selectWeightRecordList(query);
        if (records == null || records.isEmpty())
        {
            return null;
        }

        records.sort(Comparator.comparing(WeightRecord::getRecordedAt, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightRecord::getId, Comparator.nullsLast(Long::compareTo))
                .reversed());
        return records.get(0);
    }

    private BigDecimal calculateBmi(BigDecimal currentWeight, BigDecimal height)
    {
        if (currentWeight == null || height == null || height.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }

        BigDecimal heightMeter = height.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal heightSquare = heightMeter.multiply(heightMeter);
        return currentWeight.divide(heightSquare, 1, RoundingMode.HALF_UP);
    }

    private void syncHistoricalBmiSnapshots(Long userId, BigDecimal height, String updateBy)
    {
        String operator = updateBy != null && !updateBy.trim().isEmpty() ? updateBy : "system";
        if (height == null || height.compareTo(BigDecimal.ZERO) <= 0)
        {
            weightRecordMapper.clearBmiSnapshotsByUserId(userId, operator);
            return;
        }

        weightRecordMapper.refreshBmiSnapshotsByUserId(userId, height, operator);
    }

    private boolean hasBigDecimalChanged(BigDecimal previous, BigDecimal next)
    {
        if (previous == null && next == null)
        {
            return false;
        }
        if (previous == null || next == null)
        {
            return true;
        }
        return previous.compareTo(next) != 0;
    }
}
