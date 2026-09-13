package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightAchievementMapper;
import com.ruoyi.weight.domain.WeightAchievement;
import com.ruoyi.weight.service.IWeightAchievementService;

/**
 * 成就徽章Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightAchievementServiceImpl implements IWeightAchievementService 
{
    @Autowired
    private WeightAchievementMapper weightAchievementMapper;

    /**
     * 查询成就徽章
     * 
     * @param id 成就徽章主键
     * @return 成就徽章
     */
    @Override
    public WeightAchievement selectWeightAchievementById(Long id)
    {
        return weightAchievementMapper.selectWeightAchievementById(id);
    }

    /**
     * 查询成就徽章列表
     * 
     * @param weightAchievement 成就徽章
     * @return 成就徽章
     */
    @Override
    public List<WeightAchievement> selectWeightAchievementList(WeightAchievement weightAchievement)
    {
        return weightAchievementMapper.selectWeightAchievementList(weightAchievement);
    }

    /**
     * 新增成就徽章
     * 
     * @param weightAchievement 成就徽章
     * @return 结果
     */
    @Override
    public int insertWeightAchievement(WeightAchievement weightAchievement)
    {
        weightAchievement.setCreateTime(DateUtils.getNowDate());
        return weightAchievementMapper.insertWeightAchievement(weightAchievement);
    }

    /**
     * 修改成就徽章
     * 
     * @param weightAchievement 成就徽章
     * @return 结果
     */
    @Override
    public int updateWeightAchievement(WeightAchievement weightAchievement)
    {
        weightAchievement.setUpdateTime(DateUtils.getNowDate());
        return weightAchievementMapper.updateWeightAchievement(weightAchievement);
    }

    /**
     * 批量删除成就徽章
     * 
     * @param ids 需要删除的成就徽章主键
     * @return 结果
     */
    @Override
    public int deleteWeightAchievementByIds(Long[] ids)
    {
        return weightAchievementMapper.deleteWeightAchievementByIds(ids);
    }

    /**
     * 删除成就徽章信息
     * 
     * @param id 成就徽章主键
     * @return 结果
     */
    @Override
    public int deleteWeightAchievementById(Long id)
    {
        return weightAchievementMapper.deleteWeightAchievementById(id);
    }
}
