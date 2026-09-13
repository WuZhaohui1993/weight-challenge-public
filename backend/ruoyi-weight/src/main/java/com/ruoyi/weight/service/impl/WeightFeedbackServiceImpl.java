package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.weight.domain.WeightFeedback;
import com.ruoyi.weight.mapper.WeightFeedbackMapper;
import com.ruoyi.weight.service.IWeightFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户反馈Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-26
 */
@Service
public class WeightFeedbackServiceImpl implements IWeightFeedbackService
{
    @Autowired
    private WeightFeedbackMapper weightFeedbackMapper;

    @Override
    public WeightFeedback selectWeightFeedbackById(Long id)
    {
        return weightFeedbackMapper.selectWeightFeedbackById(id);
    }

    @Override
    public List<WeightFeedback> selectWeightFeedbackList(WeightFeedback weightFeedback)
    {
        return weightFeedbackMapper.selectWeightFeedbackList(weightFeedback);
    }

    @Override
    public int insertWeightFeedback(WeightFeedback weightFeedback)
    {
        weightFeedback.setCreateTime(DateUtils.getNowDate());
        return weightFeedbackMapper.insertWeightFeedback(weightFeedback);
    }

    @Override
    public int updateWeightFeedback(WeightFeedback weightFeedback)
    {
        weightFeedback.setUpdateTime(DateUtils.getNowDate());
        return weightFeedbackMapper.updateWeightFeedback(weightFeedback);
    }

    @Override
    public int deleteWeightFeedbackByIds(Long[] ids)
    {
        return weightFeedbackMapper.deleteWeightFeedbackByIds(ids);
    }

    @Override
    public int deleteWeightFeedbackById(Long id)
    {
        return weightFeedbackMapper.deleteWeightFeedbackById(id);
    }
}
