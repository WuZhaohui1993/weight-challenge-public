package com.ruoyi.weight.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightNotificationMapper;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.service.IWeightNotificationService;

/**
 * 消息通知Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Service
public class WeightNotificationServiceImpl implements IWeightNotificationService 
{
    @Autowired
    private WeightNotificationMapper weightNotificationMapper;

    /**
     * 查询消息通知
     * 
     * @param id 消息通知主键
     * @return 消息通知
     */
    @Override
    public WeightNotification selectWeightNotificationById(Long id)
    {
        return weightNotificationMapper.selectWeightNotificationById(id);
    }

    /**
     * 查询消息通知列表
     * 
     * @param weightNotification 消息通知
     * @return 消息通知
     */
    @Override
    public List<WeightNotification> selectWeightNotificationList(WeightNotification weightNotification)
    {
        return weightNotificationMapper.selectWeightNotificationList(weightNotification);
    }

    /**
     * 新增消息通知
     * 
     * @param weightNotification 消息通知
     * @return 结果
     */
    @Override
    public int insertWeightNotification(WeightNotification weightNotification)
    {
        weightNotification.setCreateTime(DateUtils.getNowDate());
        return weightNotificationMapper.insertWeightNotification(weightNotification);
    }

    /**
     * 修改消息通知
     * 
     * @param weightNotification 消息通知
     * @return 结果
     */
    @Override
    public int updateWeightNotification(WeightNotification weightNotification)
    {
        return weightNotificationMapper.updateWeightNotification(weightNotification);
    }

    /**
     * 批量删除消息通知
     * 
     * @param ids 需要删除的消息通知主键
     * @return 结果
     */
    @Override
    public int deleteWeightNotificationByIds(Long[] ids)
    {
        return weightNotificationMapper.deleteWeightNotificationByIds(ids);
    }

    /**
     * 删除消息通知信息
     * 
     * @param id 消息通知主键
     * @return 结果
     */
    @Override
    public int deleteWeightNotificationById(Long id)
    {
        return weightNotificationMapper.deleteWeightNotificationById(id);
    }
}
