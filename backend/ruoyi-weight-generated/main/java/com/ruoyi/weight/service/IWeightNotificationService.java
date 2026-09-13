package com.ruoyi.weight.service;

import java.util.List;
import com.ruoyi.weight.domain.WeightNotification;

/**
 * 消息通知Service接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public interface IWeightNotificationService 
{
    /**
     * 查询消息通知
     * 
     * @param id 消息通知主键
     * @return 消息通知
     */
    public WeightNotification selectWeightNotificationById(Long id);

    /**
     * 查询消息通知列表
     * 
     * @param weightNotification 消息通知
     * @return 消息通知集合
     */
    public List<WeightNotification> selectWeightNotificationList(WeightNotification weightNotification);

    /**
     * 新增消息通知
     * 
     * @param weightNotification 消息通知
     * @return 结果
     */
    public int insertWeightNotification(WeightNotification weightNotification);

    /**
     * 修改消息通知
     * 
     * @param weightNotification 消息通知
     * @return 结果
     */
    public int updateWeightNotification(WeightNotification weightNotification);

    /**
     * 批量删除消息通知
     * 
     * @param ids 需要删除的消息通知主键集合
     * @return 结果
     */
    public int deleteWeightNotificationByIds(Long[] ids);

    /**
     * 删除消息通知信息
     * 
     * @param id 消息通知主键
     * @return 结果
     */
    public int deleteWeightNotificationById(Long id);
}
