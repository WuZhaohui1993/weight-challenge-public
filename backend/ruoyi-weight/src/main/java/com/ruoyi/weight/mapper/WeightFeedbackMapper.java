package com.ruoyi.weight.mapper;

import java.util.List;
import com.ruoyi.weight.domain.WeightFeedback;

/**
 * 用户反馈Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public interface WeightFeedbackMapper
{
    public WeightFeedback selectWeightFeedbackById(Long id);

    public List<WeightFeedback> selectWeightFeedbackList(WeightFeedback weightFeedback);

    public int insertWeightFeedback(WeightFeedback weightFeedback);

    public int updateWeightFeedback(WeightFeedback weightFeedback);

    public int deleteWeightFeedbackById(Long id);

    public int deleteWeightFeedbackByIds(Long[] ids);
}
