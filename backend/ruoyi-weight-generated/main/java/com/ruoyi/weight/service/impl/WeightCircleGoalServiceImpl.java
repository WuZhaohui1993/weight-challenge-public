package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightCircleGoalMapper;
import com.ruoyi.weight.domain.WeightCircleGoal;
import com.ruoyi.weight.service.IWeightCircleGoalService;

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
}
