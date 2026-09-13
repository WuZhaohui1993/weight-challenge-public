package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightAchievement;

/**
 * 成就徽章Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightAchievementService 
{
    /**
     * 查询成就徽章
     * 
     * @param id 成就徽章主键
     * @return 成就徽章
     */
    public WeightAchievement selectWeightAchievementById(Long id);

    /**
     * 查询成就徽章列表
     * 
     * @param weightAchievement 成就徽章
     * @return 成就徽章集合
     */
    public List<WeightAchievement> selectWeightAchievementList(WeightAchievement weightAchievement);

    /**
     * 新增成就徽章
     * 
     * @param weightAchievement 成就徽章
     * @return 结果
     */
    public int insertWeightAchievement(WeightAchievement weightAchievement);

    /**
     * 修改成就徽章
     * 
     * @param weightAchievement 成就徽章
     * @return 结果
     */
    public int updateWeightAchievement(WeightAchievement weightAchievement);

    /**
     * 批量删除成就徽章
     * 
     * @param ids 需要删除的成就徽章主键集合
     * @return 结果
     */
    public int deleteWeightAchievementByIds(Long[] ids);

    /**
     * 删除成就徽章信息
     * 
     * @param id 成就徽章主键
     * @return 结果
     */
    public int deleteWeightAchievementById(Long id);
}
