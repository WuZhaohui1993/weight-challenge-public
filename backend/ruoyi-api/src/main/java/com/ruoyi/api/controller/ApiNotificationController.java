package com.ruoyi.api.controller;

import com.ruoyi.api.dto.ApiNotificationResponse;
import com.ruoyi.api.dto.ApiPageResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.weight.domain.WeightComment;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightCommentService;
import com.ruoyi.weight.service.IWeightNotificationService;
import com.ruoyi.weight.service.IWeightUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 移动端 - 通知相关 API
 */
@RestController
@RequestMapping("/api/notifications")
public class ApiNotificationController extends ApiBaseController {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

    @Autowired
    private IWeightNotificationService notificationService;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private IWeightCommentService commentService;

    @GetMapping
    public AjaxResult notifications(HttpServletRequest request,
                                    @RequestParam(value = "type", required = false) String type) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        String normalizedType = normalizeType(type);
        if (normalizedType == null) {
            return AjaxResult.error("type 仅支持 all / like / comment / system");
        }

        List<WeightNotification> notifications = loadNotifications(userId, normalizedType);
        return pageSuccessInMemory(buildNotificationItems(notifications));
    }

    @GetMapping("/unread-count")
    public AjaxResult unreadCount(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        return successWithData(countUnread(userId));
    }

    @PutMapping("/read-all")
    public AjaxResult markAllRead(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightNotification query = new WeightNotification();
        query.setUserId(userId);
        query.setIsRead(0);
        List<WeightNotification> unreadNotifications = notificationService.selectWeightNotificationList(query);

        int updated = 0;
        if (unreadNotifications != null) {
            for (WeightNotification notification : unreadNotifications) {
                if (notification == null || isRead(notification)) {
                    continue;
                }
                notification.setIsRead(1);
                updated += notificationService.updateWeightNotification(notification);
            }
        }

        return successWithData(updated > 0 ? "已全部标记为已读" : "当前没有未读通知", updated);
    }

    @PutMapping("/{notificationId}/read")
    public AjaxResult markRead(HttpServletRequest request, @PathVariable("notificationId") Long notificationId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightNotification notification = notificationService.selectWeightNotificationById(notificationId);
        if (notification == null) {
            return AjaxResult.error("通知不存在");
        }
        if (!userId.equals(notification.getUserId())) {
            return forbidden("无权操作该通知");
        }

        if (!isRead(notification)) {
            notification.setIsRead(1);
            notificationService.updateWeightNotification(notification);
        }

        return successWithData("已标记为已读", buildNotificationItems(Collections.singletonList(notification)).get(0));
    }

    private List<WeightNotification> loadNotifications(Long userId, String type) {
        WeightNotification query = new WeightNotification();
        query.setUserId(userId);
        if (!"all".equals(type)) {
            query.setType(type);
        }

        List<WeightNotification> notifications = notificationService.selectWeightNotificationList(query);
        List<WeightNotification> safeNotifications = notifications != null
                ? new ArrayList<>(notifications)
                : new ArrayList<WeightNotification>();
        safeNotifications.sort(Comparator
                .comparing(WeightNotification::getCreateTime, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightNotification::getId, Comparator.nullsLast(Long::compareTo))
                .reversed());
        return safeNotifications;
    }

    private String normalizeType(String value) {
        if (StringUtils.isBlank(value)) {
            return "all";
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if ("all".equals(normalized) || "like".equals(normalized) || "comment".equals(normalized) || "system".equals(normalized)) {
            return normalized;
        }
        return null;
    }

    private Long countUnread(Long userId) {
        WeightNotification query = new WeightNotification();
        query.setUserId(userId);
        query.setIsRead(0);
        List<WeightNotification> unreadNotifications = notificationService.selectWeightNotificationList(query);
        return unreadNotifications == null ? 0L : (long) unreadNotifications.size();
    }

    private List<ApiNotificationResponse.NotificationItem> buildNotificationItems(List<WeightNotification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, WeightUser> userMap = loadUserMap(extractFromUserIds(notifications));
        Map<Long, WeightComment> commentMap = loadCommentMap(extractCommentIds(notifications));
        List<ApiNotificationResponse.NotificationItem> items = new ArrayList<>();
        for (WeightNotification notification : notifications) {
            ApiNotificationResponse.NotificationItem item = new ApiNotificationResponse.NotificationItem();
            WeightUser fromUser = userMap.get(notification.getFromUserId());
            WeightComment targetComment = commentMap.get(notification.getTargetId());

            item.setId(notification.getId());
            item.setType(notification.getType());
            item.setFromUserId(notification.getFromUserId());
            item.setFromUserNickname(fromUser != null ? fromUser.getNickname() : null);
            item.setFromUserAvatar(fromUser != null ? fromUser.getAvatar() : null);
            item.setTargetType(notification.getTargetType());
            item.setTargetId(notification.getTargetId());
            item.setFeedId(resolveFeedId(notification, targetComment));
            item.setCommentId("comment".equals(notification.getTargetType()) ? notification.getTargetId() : null);
            item.setContent(notification.getContent());
            item.setPreview(notification.getPreview());
            item.setRead(isRead(notification));
            item.setCreatedAt(formatDateTime(notification.getCreateTime()));
            items.add(item);
        }
        return items;
    }

    private Map<Long, WeightUser> loadUserMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, WeightUser> userMap = new HashMap<>();
        for (Long userId : userIds) {
            if (userId == null || userMap.containsKey(userId)) {
                continue;
            }
            WeightUser user = weightUserService.selectWeightUserByUserId(userId);
            if (user != null) {
                userMap.put(userId, user);
            }
        }
        return userMap;
    }

    private Set<Long> extractFromUserIds(List<WeightNotification> notifications) {
        Set<Long> userIds = new LinkedHashSet<>();
        for (WeightNotification notification : notifications) {
            if (notification != null && notification.getFromUserId() != null) {
                userIds.add(notification.getFromUserId());
            }
        }
        return userIds;
    }

    private Set<Long> extractCommentIds(List<WeightNotification> notifications) {
        Set<Long> commentIds = new LinkedHashSet<>();
        for (WeightNotification notification : notifications) {
            if (notification != null
                    && "comment".equals(notification.getTargetType())
                    && notification.getTargetId() != null) {
                commentIds.add(notification.getTargetId());
            }
        }
        return commentIds;
    }

    private Map<Long, WeightComment> loadCommentMap(Collection<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, WeightComment> commentMap = new HashMap<>();
        for (Long commentId : commentIds) {
            if (commentId == null || commentMap.containsKey(commentId)) {
                continue;
            }
            WeightComment comment = commentService.selectWeightCommentById(commentId);
            if (comment != null) {
                commentMap.put(commentId, comment);
            }
        }
        return commentMap;
    }

    private Long resolveFeedId(WeightNotification notification, WeightComment targetComment) {
        if (notification == null) {
            return null;
        }
        if ("feed".equals(notification.getTargetType())) {
            return notification.getTargetId();
        }
        if ("comment".equals(notification.getTargetType())) {
            return targetComment != null ? targetComment.getFeedId() : null;
        }
        return null;
    }

    private boolean isRead(WeightNotification notification) {
        return notification != null && Integer.valueOf(1).equals(notification.getIsRead());
    }

    private String formatDateTime(Date date) {
        return date != null ? DateUtils.parseDateToStr(DATE_TIME_PATTERN, date) : null;
    }

    private <T> AjaxResult pageSuccessInMemory(List<T> fullList) {
        List<T> safeList = fullList != null ? fullList : Collections.<T>emptyList();
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() != null && pageDomain.getPageNum() > 0 ? pageDomain.getPageNum() : 1;
        int pageSize = pageDomain.getPageSize() != null && pageDomain.getPageSize() > 0 ? pageDomain.getPageSize() : 10;
        int fromIndex = Math.min((pageNum - 1) * pageSize, safeList.size());
        int toIndex = Math.min(fromIndex + pageSize, safeList.size());

        ApiPageResponse<T> response = new ApiPageResponse<>();
        response.setList(new ArrayList<>(safeList.subList(fromIndex, toIndex)));
        response.setTotal(safeList.size());
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        return successWithData("查询成功", response);
    }
}
