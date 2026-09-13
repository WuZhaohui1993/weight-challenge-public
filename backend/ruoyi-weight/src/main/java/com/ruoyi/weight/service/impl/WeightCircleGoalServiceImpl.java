package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleGoalMapper;
import com.ruoyi.weight.domain.WeightCircleGoal;
import com.ruoyi.weight.service.IWeightCircleGoalService;
import org.apache.commons.lang3.StringUtils;

/**
 * 圈子目标Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightCircleGoalServiceImpl implements IWeightCircleGoalService 
{
    @Autowired
    private WeightCircleGoalMapper weightCircleGoalMapper;

    /**
     * 查询圈子目标
     * 
     * @param id 圈子目标主键
     * @return 圈子目标
     */
    @Override
    public WeightCircleGoal selectWeightCircleGoalById(Long id)
    {
        return weightCircleGoalMapper.selectWeightCircleGoalById(id);
    }

    /**
     * 查询圈子目标列表
     * 
     * @param weightCircleGoal 圈子目标
     * @return 圈子目标
     */
    @Override
    public List<WeightCircleGoal> selectWeightCircleGoalList(WeightCircleGoal weightCircleGoal)
    {
        return weightCircleGoalMapper.selectWeightCircleGoalList(weightCircleGoal);
    }

    /**
     * 新增圈子目标
     * 
     * @param weightCircleGoal 圈子目标
     * @return 结果
     */
    @Override
    public int insertWeightCircleGoal(WeightCircleGoal weightCircleGoal)
    {
        normalizeGoalConfig(weightCircleGoal);
        weightCircleGoal.setCreateTime(DateUtils.getNowDate());
        return weightCircleGoalMapper.insertWeightCircleGoal(weightCircleGoal);
    }

    /**
     * 修改圈子目标
     * 
     * @param weightCircleGoal 圈子目标
     * @return 结果
     */
    @Override
    public int updateWeightCircleGoal(WeightCircleGoal weightCircleGoal)
    {
        normalizeGoalConfig(weightCircleGoal);
        weightCircleGoal.setUpdateTime(DateUtils.getNowDate());
        return weightCircleGoalMapper.updateWeightCircleGoal(weightCircleGoal);
    }

    /**
     * 批量删除圈子目标
     * 
     * @param ids 需要删除的圈子目标主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleGoalByIds(Long[] ids)
    {
        return weightCircleGoalMapper.deleteWeightCircleGoalByIds(ids);
    }

    /**
     * 删除圈子目标信息
     * 
     * @param id 圈子目标主键
     * @return 结果
     */
    @Override
    public int deleteWeightCircleGoalById(Long id)
    {
        return weightCircleGoalMapper.deleteWeightCircleGoalById(id);
    }

    private void normalizeGoalConfig(WeightCircleGoal goal)
    {
        if (goal == null)
        {
            throw new ServiceException("圈子目标不能为空");
        }

        String verificationType = StringUtils.defaultIfBlank(StringUtils.trimToNull(goal.getVerificationType()), "0");
        if (!"0".equals(verificationType) && !"1".equals(verificationType))
        {
            throw new ServiceException("移动端圈子目标仅支持自动统计或手动完成");
        }
        goal.setVerificationType(verificationType);

        String metricCode = StringUtils.trimToNull(goal.getMetricCode());
        if (metricCode == null)
        {
            metricCode = resolveMetricCode(goal);
        }
        if (metricCode == null)
        {
            throw new ServiceException("当前目标类型和单位无法映射为移动端可执行目标，请调整目标类型、单位或验证方式");
        }
        goal.setMetricCode(metricCode);
    }

    private String resolveMetricCode(WeightCircleGoal goal)
    {
        String goalType = StringUtils.defaultString(goal.getGoalType()).trim();
        String targetUnit = StringUtils.defaultString(goal.getTargetUnit()).trim();
        String verificationType = StringUtils.defaultString(goal.getVerificationType()).trim();

        if ("weight_loss".equals(goalType))
        {
            return containsAny(targetUnit, "kg", "KG", "公斤") ? "weight_loss_kg" : "weight_record_count";
        }
        if ("exercise".equals(goalType))
        {
            return containsAny(targetUnit, "分钟", "min", "MIN") ? "exercise_minutes" : "exercise_count";
        }
        if ("diet".equals(goalType))
        {
            return "food_record_count";
        }
        if ("habit".equals(goalType))
        {
            return "habit_checkin_count";
        }
        if ("checkin".equals(goalType))
        {
            return "1".equals(verificationType) ? "manual_circle_checkin" : "circle_checkin_count";
        }
        return null;
    }

    private boolean containsAny(String value, String... candidates)
    {
        if (StringUtils.isBlank(value) || candidates == null)
        {
            return false;
        }
        for (String candidate : candidates)
        {
            if (StringUtils.containsIgnoreCase(value, candidate))
            {
                return true;
            }
        }
        return false;
    }
}
