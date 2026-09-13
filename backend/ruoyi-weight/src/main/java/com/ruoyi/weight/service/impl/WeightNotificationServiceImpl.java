package com.ruoyi.weight.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.weight.mapper.WeightNotificationMapper;
import com.ruoyi.weight.mapper.WeightUserMapper;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.domain.WeightNotificationSendRequest;
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
    private static final String RECIPIENT_MODE_ALL = "all";

    private static final String RECIPIENT_MODE_SELECTED = "selected";

    private static final String SYSTEM_NOTIFICATION_TYPE = "system";

    private static final int BATCH_INSERT_SIZE = 500;

    @Autowired
    private WeightNotificationMapper weightNotificationMapper;

    @Autowired
    private WeightUserMapper weightUserMapper;

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
     * 管理端发送系统通知
     *
     * @param request 发送请求
     * @return 已发送通知数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sendSystemNotification(WeightNotificationSendRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("发送内容不能为空");
        }

        String content = StringUtils.trimToNull(request.getContent());
        if (content == null)
        {
            throw new ServiceException("通知内容不能为空");
        }

        List<Long> recipientUserIds = resolveRecipientUserIds(request);
        if (recipientUserIds.isEmpty())
        {
            throw new ServiceException("没有可发送的用户");
        }

        Date now = DateUtils.getNowDate();
        String normalizedContent = StringUtils.abbreviate(content, 500);
        String preview = StringUtils.defaultIfBlank(
            StringUtils.trimToNull(request.getPreview()),
            StringUtils.abbreviate(normalizedContent, 80)
        );
        String targetType = StringUtils.abbreviate(StringUtils.trimToNull(request.getTargetType()), 32);

        List<WeightNotification> notifications = new ArrayList<>(recipientUserIds.size());
        for (Long userId : recipientUserIds)
        {
            WeightNotification notification = new WeightNotification();
            notification.setUserId(userId);
            notification.setType(SYSTEM_NOTIFICATION_TYPE);
            notification.setFromUserId(null);
            notification.setTargetType(targetType);
            notification.setTargetId(request.getTargetId());
            notification.setContent(normalizedContent);
            notification.setPreview(StringUtils.abbreviate(preview, 256));
            notification.setIsRead(0);
            notification.setCreateTime(now);
            notifications.add(notification);
        }

        int rows = 0;
        for (int start = 0; start < notifications.size(); start += BATCH_INSERT_SIZE)
        {
            int end = Math.min(start + BATCH_INSERT_SIZE, notifications.size());
            rows += weightNotificationMapper.batchInsertWeightNotifications(notifications.subList(start, end));
        }
        return rows;
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

    private List<Long> resolveRecipientUserIds(WeightNotificationSendRequest request)
    {
        String recipientMode = StringUtils.defaultIfBlank(
            StringUtils.trimToNull(request.getRecipientMode()),
            RECIPIENT_MODE_SELECTED
        );
        if (RECIPIENT_MODE_ALL.equals(recipientMode))
        {
            List<Long> allUserIds = weightUserMapper.selectAllWeightUserIds();
            return distinctPositiveIds(allUserIds);
        }
        if (!RECIPIENT_MODE_SELECTED.equals(recipientMode))
        {
            throw new ServiceException("接收范围不正确");
        }

        List<Long> requestedUserIds = distinctPositiveIds(request.getUserIds());
        if (requestedUserIds.isEmpty())
        {
            throw new ServiceException("请至少选择一个接收用户");
        }

        List<Long> existingUserIds = weightUserMapper.selectExistingWeightUserIds(requestedUserIds);
        Set<Long> existingUserIdSet = new LinkedHashSet<>(existingUserIds);
        List<Long> missingUserIds = requestedUserIds.stream()
            .filter(userId -> !existingUserIdSet.contains(userId))
            .collect(Collectors.toList());
        if (!missingUserIds.isEmpty())
        {
            throw new ServiceException("存在无效用户ID：" + missingUserIds);
        }
        return requestedUserIds;
    }

    private List<Long> distinctPositiveIds(List<Long> userIds)
    {
        if (userIds == null || userIds.isEmpty())
        {
            return Collections.emptyList();
        }

        return userIds.stream()
            .filter(userId -> userId != null && userId > 0)
            .collect(Collectors.toCollection(LinkedHashSet::new))
            .stream()
            .collect(Collectors.toList());
    }
}
