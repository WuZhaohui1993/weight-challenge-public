package com.ruoyi.api.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.api.dto.ApiFeedCommentCreateRequest;
import com.ruoyi.api.dto.ApiFeedPublishRequest;
import com.ruoyi.api.dto.ApiFeedResponse;
import com.ruoyi.api.service.CircleReadModelService;
import com.ruoyi.api.dto.ApiPageResponse;
import com.ruoyi.api.service.WechatContentSecurityService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.domain.WeightCircleFeed;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightComment;
import com.ruoyi.weight.domain.WeightExerciseRecord;
import com.ruoyi.weight.domain.WeightFeedCircleSync;
import com.ruoyi.weight.domain.WeightFeedLike;
import com.ruoyi.weight.domain.WeightFoodRecord;
import com.ruoyi.weight.domain.WeightHabitCheckin;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.domain.WeightWaterRecord;
import com.ruoyi.weight.service.IWeightCircleFeedService;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightCircleService;
import com.ruoyi.weight.service.IWeightCommentService;
import com.ruoyi.weight.service.IWeightExerciseRecordService;
import com.ruoyi.weight.service.IWeightFeedCircleSyncService;
import com.ruoyi.weight.service.IWeightFeedLikeService;
import com.ruoyi.weight.service.IWeightFoodRecordService;
import com.ruoyi.weight.service.IWeightHabitCheckinService;
import com.ruoyi.weight.service.IWeightNotificationService;
import com.ruoyi.weight.service.IWeightRecordService;
import com.ruoyi.weight.service.IWeightUserService;
import com.ruoyi.weight.service.IWeightWaterRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 移动端 - 动态相关 API
 */
@RestController
@RequestMapping("/api/feed")
public class ApiFeedController extends ApiBaseController {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String VISIBILITY_PUBLIC = "public";
    private static final String VISIBILITY_CIRCLE = "circle";
    private static final String COMMENT_DELETED_PLACEHOLDER = "该评论已删除";

    @Autowired
    private IWeightCircleService circleService;

    @Autowired
    private IWeightCircleMemberService memberService;

    @Autowired
    private IWeightCircleFeedService feedService;

    @Autowired
    private IWeightCommentService commentService;

    @Autowired
    private IWeightFeedCircleSyncService feedCircleSyncService;

    @Autowired
    private IWeightFeedLikeService feedLikeService;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private IWeightNotificationService notificationService;

    @Autowired
    private IWeightRecordService weightRecordService;

    @Autowired
    private IWeightFoodRecordService foodRecordService;

    @Autowired
    private IWeightExerciseRecordService exerciseRecordService;

    @Autowired
    private IWeightWaterRecordService waterRecordService;

    @Autowired
    private IWeightHabitCheckinService habitCheckinService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private CircleReadModelService circleReadModelService;

    @Autowired
    private WechatContentSecurityService contentSecurityService;

    /**
     * 发布动态
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createFeed(HttpServletRequest request, @RequestBody ApiFeedPublishRequest body) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (body == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        String visibilityScope = normalizeVisibilityScope(body.getVisibilityScope());
        if (visibilityScope == null) {
            return AjaxResult.error("visibilityScope 仅支持 public 或 circle");
        }

        String content = StringUtils.trimToNull(body.getContent());
        List<String> normalizedImages = normalizeImages(body.getImages());
        if (content == null && normalizedImages.isEmpty()) {
            return AjaxResult.error("动态内容和图片不能同时为空");
        }
        if (normalizedImages.size() > 9) {
            return AjaxResult.error("最多上传 9 张图片");
        }
        String securityMessage = contentSecurityService.validateText(
                userId, content, WechatContentSecurityService.SCENE_SOCIAL_LOG, "feed.publish");
        if (securityMessage != null) {
            return AjaxResult.error(securityMessage);
        }
        String feedType = StringUtils.trimToEmpty(body.getFeedType());
        if (StringUtils.isBlank(feedType)) {
            return AjaxResult.error("feedType 不能为空");
        }
        String sourceType = StringUtils.abbreviate(StringUtils.trimToNull(body.getSourceType()), 32);
        AjaxResult sourceValidationResult = validateRecordBackedFeed(feedType, sourceType, body.getSourceId(), userId);
        if (sourceValidationResult != null) {
            return sourceValidationResult;
        }

        Long originCircleId = body.getOriginCircleId();
        if (VISIBILITY_CIRCLE.equals(visibilityScope) && originCircleId == null) {
            return AjaxResult.error("仅圈子可见动态必须指定 originCircleId");
        }

        if (originCircleId != null) {
            WeightCircle originCircle = circleService.selectWeightCircleById(originCircleId);
            if (!isActiveCircle(originCircle)) {
                return AjaxResult.error("来源圈子不存在");
            }
            if (circleReadModelService.isCircleReadOnly(originCircle)) {
                return AjaxResult.error(circleReadModelService.resolveReadOnlyReason(originCircle));
            }
            if (findActiveMember(originCircleId, userId) == null) {
                return forbidden("仅已加入圈子的成员可在该圈子上下文发动态");
            }
        }

        List<Long> syncCircleIds = normalizeSyncCircleIds(body.getSyncCircleIds(), originCircleId);
        for (Long syncCircleId : syncCircleIds) {
            WeightCircle syncCircle = circleService.selectWeightCircleById(syncCircleId);
            if (!isActiveCircle(syncCircle)) {
                return AjaxResult.error("存在无效的同步圈子");
            }
            if (circleReadModelService.isCircleReadOnly(syncCircle)) {
                return AjaxResult.error(circleReadModelService.resolveReadOnlyReason(syncCircle));
            }
            if (findActiveMember(syncCircleId, userId) == null) {
                return forbidden("仅可同步到自己已加入的圈子");
            }
        }

        WeightCircleFeed feed = new WeightCircleFeed();
        feed.setCircleId(originCircleId);
        feed.setOriginCircleId(originCircleId);
        feed.setUserId(userId);
        feed.setFeedType(feedType);
        feed.setVisibilityScope(visibilityScope);
        feed.setContent(content);
        feed.setImages(normalizedImages.isEmpty() ? null : JSON.toJSONString(normalizedImages));
        feed.setSourceRecordType(sourceType);
        feed.setSourceRecordId(body.getSourceId());
        feed.setLikesCount(0L);
        feed.setCommentsCount(0L);
        feed.setIsFeatured(0);
        feed.setStatus("0");
        feed.setDelFlag("0");
        feedService.insertWeightCircleFeed(feed);

        saveFeedSyncCircles(feed.getId(), syncCircleIds);

        WeightCircleFeed savedFeed = feedService.selectWeightCircleFeedById(feed.getId());
        return successWithData("发布成功", buildFeedCard(savedFeed, userId));
    }

    /**
     * 上传动态图片
     */
    @PostMapping("/upload-image")
    public AjaxResult uploadFeedImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请选择图片文件");
        }

        try {
            String fileName = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            String securityMessage = contentSecurityService.validateImage(
                    resolveUploadedProfileFile(fileName), "feed.image");
            if (securityMessage != null) {
                deleteUploadedProfileFile(fileName);
                return AjaxResult.error(securityMessage);
            }
            String url = serverConfig.getUrl() + fileName;

            AjaxResult ajax = AjaxResult.success("上传成功");
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(resolveUploadErrorMessage(e, "图片"));
        }
    }

    /**
     * 公开动态列表
     */
    @GetMapping("/public/list")
    public AjaxResult publicFeeds(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        List<WeightCircleFeed> feeds = feedService.selectPublicFeedList();
        return pageSuccessInMemory(buildFeedCards(feeds, userId));
    }

    /**
     * 动态详情
     */
    @GetMapping("/{feedId}")
    public AjaxResult feedDetail(HttpServletRequest request, @PathVariable("feedId") Long feedId) {
        Long userId = getCurrentUserId(request);

        WeightCircleFeed feed = feedService.selectWeightCircleFeedById(feedId);
        if (!isVisibleFeed(feed)) {
            return AjaxResult.error("动态不存在");
        }
        if (!canAccessFeed(feed, userId)) {
            return forbidden("无权查看该动态");
        }
        return successWithData(buildFeedCard(feed, userId));
    }

    /**
     * 按来源记录查询当前用户发布的关联动态
     */
    @GetMapping("/source")
    public AjaxResult feedBySource(HttpServletRequest request,
                                   @RequestParam("sourceType") String sourceType,
                                   @RequestParam("sourceId") Long sourceId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (StringUtils.isBlank(sourceType) || sourceId == null) {
            return AjaxResult.error("来源记录参数不能为空");
        }

        WeightCircleFeed query = new WeightCircleFeed();
        query.setUserId(userId);
        query.setSourceRecordType(StringUtils.abbreviate(sourceType.trim(), 32));
        query.setSourceRecordId(sourceId);
        query.setStatus("0");
        List<WeightCircleFeed> feeds = feedService.selectWeightCircleFeedList(query);
        for (WeightCircleFeed feed : feeds) {
            if (isVisibleFeed(feed) && canAccessFeed(feed, userId)) {
                return successWithData(buildFeedCard(feed, userId));
            }
        }
        return successWithData(null);
    }

    /**
     * 删除动态
     */
    @DeleteMapping("/{feedId}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteFeed(HttpServletRequest request, @PathVariable("feedId") Long feedId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircleFeed feed = feedService.selectWeightCircleFeedById(feedId);
        if (!isVisibleFeed(feed)) {
            return AjaxResult.error("动态不存在");
        }
        if (feed.getUserId() == null || !feed.getUserId().equals(userId)) {
            return forbidden("仅作者可删除动态");
        }

        feed.setDelFlag("2");
        feed.setUpdateBy("api-feed");
        feedService.updateWeightCircleFeed(feed);

        return successWithData("动态已删除", buildDeleteResult(feedId));
    }

    /**
     * 点赞 / 取消点赞
     */
    @PostMapping("/{feedId}/like")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult toggleFeedLike(HttpServletRequest request, @PathVariable("feedId") Long feedId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircleFeed feed = feedService.selectWeightCircleFeedById(feedId);
        if (!isVisibleFeed(feed)) {
            return AjaxResult.error("动态不存在");
        }
        if (!canAccessFeed(feed, userId)) {
            return forbidden("无权操作该动态");
        }

        WeightFeedLike query = new WeightFeedLike();
        query.setFeedId(feedId);
        query.setUserId(userId);
        List<WeightFeedLike> likes = feedLikeService.selectWeightFeedLikeList(query);
        boolean liked;
        if (likes == null || likes.isEmpty()) {
            WeightFeedLike like = new WeightFeedLike();
            like.setFeedId(feedId);
            like.setUserId(userId);
            like.setCreateTime(new Date());
            feedLikeService.insertWeightFeedLike(like);
            createLikeNotification(feed, userId);
            liked = true;
        } else {
            feedLikeService.deleteWeightFeedLikeByFeedIdAndUserId(feedId, userId);
            liked = false;
        }

        refreshLikesCount(feed);
        WeightCircleFeed refreshedFeed = feedService.selectWeightCircleFeedById(feedId);
        return successWithData(liked ? "点赞成功" : "已取消点赞", buildFeedCard(refreshedFeed, userId));
    }

    /**
     * 动态评论列表
     */
    @GetMapping("/{feedId}/comments")
    public AjaxResult feedComments(HttpServletRequest request, @PathVariable("feedId") Long feedId) {
        Long userId = getCurrentUserId(request);

        WeightCircleFeed feed = feedService.selectWeightCircleFeedById(feedId);
        if (!isVisibleFeed(feed)) {
            return AjaxResult.error("动态不存在");
        }
        if (!canAccessFeed(feed, userId)) {
            return forbidden("无权查看该动态");
        }
        List<WeightComment> comments = commentService.selectVisibleCommentsByFeedId(feedId);
        return pageSuccessInMemory(buildCommentItems(comments, userId));
    }

    /**
     * 发表评论
     */
    @PostMapping("/{feedId}/comments")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createComment(HttpServletRequest request,
                                    @PathVariable("feedId") Long feedId,
                                    @RequestBody ApiFeedCommentCreateRequest body) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircleFeed feed = feedService.selectWeightCircleFeedById(feedId);
        if (!isVisibleFeed(feed)) {
            return AjaxResult.error("动态不存在");
        }
        if (!canAccessFeed(feed, userId)) {
            return forbidden("无权查看该动态");
        }
        if (!canCommentFeed(feed, userId)) {
            return forbidden("加入圈子后才能评论这条动态");
        }
        if (body == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        String content = StringUtils.trimToNull(body.getContent());
        List<String> normalizedImages = normalizeImages(body.getImages());
        if (content == null && normalizedImages.isEmpty()) {
            return AjaxResult.error("评论内容和图片不能同时为空");
        }
        if (normalizedImages.size() > 9) {
            return AjaxResult.error("最多上传 9 张图片");
        }
        String securityMessage = contentSecurityService.validateText(
                userId, content, WechatContentSecurityService.SCENE_COMMENT, "feed.comment");
        if (securityMessage != null) {
            return AjaxResult.error(securityMessage);
        }

        WeightComment replyToComment = null;
        if (body.getReplyToCommentId() != null) {
            replyToComment = commentService.selectWeightCommentById(body.getReplyToCommentId());
            if (replyToComment == null || !feedId.equals(replyToComment.getFeedId())) {
                return AjaxResult.error("回复的评论不存在");
            }
        }

        WeightComment comment = new WeightComment();
        comment.setFeedId(feedId);
        comment.setUserId(userId);
        comment.setContent(StringUtils.defaultString(content));
        comment.setImages(normalizedImages.isEmpty() ? null : JSON.toJSONString(normalizedImages));
        comment.setReplyToCommentId(body.getReplyToCommentId());
        comment.setReplyToUserId(replyToComment != null ? replyToComment.getUserId() : body.getReplyToUserId());
        comment.setLikesCount(0L);
        comment.setStatus("0");
        comment.setDelFlag("0");
        commentService.insertWeightComment(comment);

        refreshCommentsCount(feed);
        WeightComment savedComment = commentService.selectWeightCommentById(comment.getId());
        createCommentNotifications(feed, savedComment, replyToComment, userId);
        Set<Long> userIds = new LinkedHashSet<>();
        userIds.add(savedComment.getUserId());
        if (savedComment.getReplyToUserId() != null) {
            userIds.add(savedComment.getReplyToUserId());
        }
        return successWithData("评论成功", buildCommentItem(savedComment, loadUserMap(userIds), userId));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteComment(HttpServletRequest request, @PathVariable("commentId") Long commentId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightComment comment = commentService.selectWeightCommentById(commentId);
        if (!isVisibleComment(comment)) {
            return AjaxResult.error("评论不存在");
        }
        if (comment.getUserId() == null || !comment.getUserId().equals(userId)) {
            return forbidden("仅作者可删除评论");
        }

        WeightCircleFeed feed = feedService.selectWeightCircleFeedById(comment.getFeedId());
        if (!isVisibleFeed(feed)) {
            return AjaxResult.error("动态不存在");
        }
        if (!canAccessFeed(feed, userId)) {
            return forbidden("无权操作该评论");
        }

        List<WeightComment> visibleComments = commentService.selectVisibleCommentsByFeedId(comment.getFeedId());
        boolean hasReplies = hasVisibleReplies(commentId, visibleComments);
        if (hasReplies) {
            comment.setContent(COMMENT_DELETED_PLACEHOLDER);
            comment.setImages("[]");
            comment.setLikesCount(0L);
            comment.setUpdateBy("api-feed");
            commentService.updateWeightComment(comment);
        } else {
            comment.setDelFlag("2");
            comment.setUpdateBy("api-feed");
            commentService.updateWeightComment(comment);
        }

        refreshCommentsCount(feed);
        Map<String, Object> result = buildDeleteResult(commentId);
        result.put("feedId", comment.getFeedId());
        result.put("placeholder", hasReplies);
        return successWithData("评论已删除", result);
    }

    private void saveFeedSyncCircles(Long feedId, List<Long> syncCircleIds) {
        if (feedId == null || syncCircleIds == null || syncCircleIds.isEmpty()) {
            return;
        }
        for (Long circleId : syncCircleIds) {
            WeightFeedCircleSync sync = new WeightFeedCircleSync();
            sync.setFeedId(feedId);
            sync.setCircleId(circleId);
            feedCircleSyncService.insertWeightFeedCircleSync(sync);
        }
    }

    private void refreshCommentsCount(WeightCircleFeed feed) {
        List<WeightComment> comments = commentService.selectVisibleCommentsByFeedId(feed.getId());
        feed.setCommentsCount((long) comments.size());
        feedService.updateWeightCircleFeed(feed);
    }

    private Map<String, Object> buildDeleteResult(Long targetId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("targetId", targetId);
        result.put("deleted", Boolean.TRUE);
        return result;
    }

    private boolean hasVisibleReplies(Long commentId, List<WeightComment> comments) {
        if (commentId == null || comments == null || comments.isEmpty()) {
            return false;
        }
        for (WeightComment comment : comments) {
            if (comment != null
                    && commentId.equals(comment.getReplyToCommentId())
                    && isVisibleComment(comment)) {
                return true;
            }
        }
        return false;
    }

    private void refreshLikesCount(WeightCircleFeed feed) {
        WeightFeedLike query = new WeightFeedLike();
        query.setFeedId(feed.getId());
        List<WeightFeedLike> likes = feedLikeService.selectWeightFeedLikeList(query);
        feed.setLikesCount((long) (likes == null ? 0 : likes.size()));
        feedService.updateWeightCircleFeed(feed);
    }

    private void createLikeNotification(WeightCircleFeed feed, Long fromUserId) {
        if (feed == null || feed.getId() == null || fromUserId == null) {
            return;
        }
        if (feed.getUserId() == null) {
            return;
        }

        WeightUser fromUser = weightUserService.selectWeightUserByUserId(fromUserId);
        String actorName = StringUtils.defaultIfBlank(fromUser != null ? fromUser.getNickname() : null, "有人");
        boolean selfLike = feed.getUserId().equals(fromUserId);

        WeightNotification notification = new WeightNotification();
        notification.setUserId(feed.getUserId());
        notification.setType("like");
        notification.setFromUserId(fromUserId);
        notification.setTargetType("feed");
        notification.setTargetId(feed.getId());
        notification.setContent(selfLike ? "你赞了自己的动态" : actorName + " 赞了你的动态");
        notification.setPreview(buildFeedPreview(feed));
        notification.setIsRead(0);
        notificationService.insertWeightNotification(notification);
    }

    private void createCommentNotifications(WeightCircleFeed feed,
                                            WeightComment comment,
                                            WeightComment replyToComment,
                                            Long fromUserId) {
        if (feed == null || feed.getId() == null || comment == null || fromUserId == null) {
            return;
        }

        WeightUser fromUser = weightUserService.selectWeightUserByUserId(fromUserId);
        String actorName = StringUtils.defaultIfBlank(fromUser != null ? fromUser.getNickname() : null, "有人");
        Set<Long> notifiedUserIds = new LinkedHashSet<>();

        if (feed.getUserId() != null) {
            WeightNotification feedOwnerNotification = new WeightNotification();
            feedOwnerNotification.setUserId(feed.getUserId());
            feedOwnerNotification.setType("comment");
            feedOwnerNotification.setFromUserId(fromUserId);
            feedOwnerNotification.setTargetType("comment");
            feedOwnerNotification.setTargetId(comment.getId());
            feedOwnerNotification.setContent(buildFeedOwnerCommentContent(feed, comment, actorName, fromUserId));
            feedOwnerNotification.setPreview(buildCommentPreview(comment));
            feedOwnerNotification.setIsRead(0);
            notificationService.insertWeightNotification(feedOwnerNotification);
            notifiedUserIds.add(feed.getUserId());
        }

        Long replyTargetUserId = comment.getReplyToUserId();
        if (replyTargetUserId != null && !notifiedUserIds.contains(replyTargetUserId)) {
            WeightNotification replyNotification = new WeightNotification();
            replyNotification.setUserId(replyTargetUserId);
            replyNotification.setType("comment");
            replyNotification.setFromUserId(fromUserId);
            replyNotification.setTargetType("comment");
            replyNotification.setTargetId(comment.getId());
            replyNotification.setContent(buildReplyCommentContent(replyTargetUserId, actorName, fromUserId));
            replyNotification.setPreview(buildCommentPreview(comment));
            replyNotification.setIsRead(0);
            notificationService.insertWeightNotification(replyNotification);
        }
    }

    private String buildFeedOwnerCommentContent(WeightCircleFeed feed,
                                                WeightComment comment,
                                                String actorName,
                                                Long fromUserId) {
        if (feed.getUserId() == null) {
            return actorName + " 评论了动态";
        }
        boolean selfComment = feed.getUserId().equals(fromUserId);
        if (comment.getReplyToCommentId() != null) {
            return selfComment ? "你回复了自己动态下的评论" : actorName + " 回复了你动态下的评论";
        }
        return selfComment ? "你评论了自己的动态" : actorName + " 评论了你的动态";
    }

    private String buildReplyCommentContent(Long replyTargetUserId, String actorName, Long fromUserId) {
        if (replyTargetUserId != null && replyTargetUserId.equals(fromUserId)) {
            return "你回复了自己的评论";
        }
        return actorName + " 回复了你的评论";
    }

    private String buildCommentPreview(WeightComment comment) {
        if (comment == null) {
            return "评论内容待补充";
        }
        if (StringUtils.isNotBlank(comment.getContent())) {
            return StringUtils.abbreviate(comment.getContent().trim(), 80);
        }
        if (!parseImages(comment.getImages()).isEmpty()) {
            return "[图片评论]";
        }
        if (StringUtils.isBlank(comment.getContent())) {
            return "评论内容待补充";
        }
        return StringUtils.abbreviate(comment.getContent().trim(), 80);
    }

    private ApiFeedResponse.FeedCard buildFeedCard(WeightCircleFeed feed, Long userId) {
        if (feed == null) {
            return null;
        }
        List<ApiFeedResponse.FeedCard> cards = buildFeedCards(Collections.singletonList(feed), userId);
        return cards.isEmpty() ? null : cards.get(0);
    }

    private List<ApiFeedResponse.FeedCard> buildFeedCards(List<WeightCircleFeed> feeds, Long currentUserId) {
        if (feeds == null || feeds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, WeightUser> userMap = loadUserMap(extractUserIdsFromFeeds(feeds));
        Map<Long, WeightCircle> circleMap = loadCircleMap(extractOriginCircleIds(feeds));
        Map<Long, List<Long>> syncCircleIdsMap = loadSyncCircleIdsMap(extractFeedIds(feeds));
        Set<Long> likedFeedIds = loadLikedFeedIds(currentUserId, extractFeedIds(feeds));

        List<ApiFeedResponse.FeedCard> cards = new ArrayList<>();
        for (WeightCircleFeed feed : feeds) {
            ApiFeedResponse.FeedCard card = new ApiFeedResponse.FeedCard();
            WeightUser user = userMap.get(feed.getUserId());
            WeightCircle originCircle = circleMap.get(feed.getOriginCircleId());

            card.setId(feed.getId());
            card.setCircleId(feed.getOriginCircleId());
            card.setCircleName(originCircle != null ? originCircle.getName() : null);
            card.setOriginCircleId(feed.getOriginCircleId());
            card.setOriginCircleName(originCircle != null ? originCircle.getName() : null);
            card.setVisibilityScope(defaultVisibilityScope(feed));
            card.setCommentEnabled(Boolean.valueOf(canCommentFeed(feed, currentUserId)));
            card.setUserId(feed.getUserId());
            card.setUserNickname(user != null ? user.getNickname() : null);
            card.setUserAvatar(user != null ? user.getAvatar() : null);
            card.setFeedType(feed.getFeedType());
            card.setContent(feed.getContent());
            card.setImages(parseImages(feed.getImages()));
            card.setLikesCount(feed.getLikesCount());
            card.setCommentsCount(feed.getCommentsCount());
            card.setIsFeatured(Boolean.valueOf(Integer.valueOf(1).equals(feed.getIsFeatured())));
            card.setLikedByMe(likedFeedIds.contains(feed.getId()));
            card.setCreatedAt(formatDateTime(feed.getCreateTime()));
            card.setSyncCircleIds(syncCircleIdsMap.getOrDefault(feed.getId(), Collections.<Long>emptyList()));
            card.setOwnedByMe(Boolean.valueOf(currentUserId != null && currentUserId.equals(feed.getUserId())));
            card.setSourceType(feed.getSourceRecordType());
            card.setSourceId(feed.getSourceRecordId());
            cards.add(card);
        }
        return cards;
    }

    private List<ApiFeedResponse.FeedCommentItem> buildCommentItems(List<WeightComment> comments, Long currentUserId) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> userIds = new LinkedHashSet<>();
        for (WeightComment comment : comments) {
            if (comment.getUserId() != null) {
                userIds.add(comment.getUserId());
            }
            if (comment.getReplyToUserId() != null) {
                userIds.add(comment.getReplyToUserId());
            }
        }
        Map<Long, WeightUser> userMap = loadUserMap(userIds);

        List<ApiFeedResponse.FeedCommentItem> items = new ArrayList<>();
        for (WeightComment comment : comments) {
            items.add(buildCommentItem(comment, userMap, currentUserId));
        }
        return items;
    }

    private Set<Long> loadLikedFeedIds(Long userId, Collection<Long> feedIds) {
        if (userId == null || feedIds == null || feedIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<WeightFeedLike> likes = feedLikeService.selectWeightFeedLikeListByUserAndFeedIds(userId, new ArrayList<>(feedIds));
        if (likes == null || likes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> likedFeedIds = new LinkedHashSet<>();
        for (WeightFeedLike like : likes) {
            if (like.getFeedId() != null) {
                likedFeedIds.add(like.getFeedId());
            }
        }
        return likedFeedIds;
    }

    private ApiFeedResponse.FeedCommentItem buildCommentItem(WeightComment comment, Map<Long, WeightUser> userMap, Long currentUserId) {
        ApiFeedResponse.FeedCommentItem item = new ApiFeedResponse.FeedCommentItem();
        WeightUser user = userMap.get(comment.getUserId());
        WeightUser replyUser = userMap.get(comment.getReplyToUserId());
        item.setId(comment.getId());
        item.setFeedId(comment.getFeedId());
        item.setUserId(comment.getUserId());
        item.setUserNickname(user != null ? user.getNickname() : null);
        item.setUserAvatar(user != null ? user.getAvatar() : null);
        item.setContent(StringUtils.defaultString(comment.getContent()));
        item.setImages(parseImages(comment.getImages()));
        item.setReplyToCommentId(comment.getReplyToCommentId());
        item.setReplyToUserId(comment.getReplyToUserId());
        item.setReplyToUserNickname(replyUser != null ? replyUser.getNickname() : null);
        item.setLikesCount(comment.getLikesCount());
        item.setCreatedAt(formatDateTime(comment.getCreateTime()));
        item.setDeleted(Boolean.valueOf(isDeletedPlaceholder(comment)));
        item.setOwnedByMe(Boolean.valueOf(currentUserId != null && currentUserId.equals(comment.getUserId())));
        return item;
    }

    private boolean canAccessFeed(WeightCircleFeed feed, Long userId) {
        if (!isVisibleFeed(feed)) {
            return false;
        }
        if (isPublicFeed(feed)) {
            return true;
        }

        Set<Long> accessibleCircleIds = new LinkedHashSet<>();
        if (feed.getOriginCircleId() != null) {
            accessibleCircleIds.add(feed.getOriginCircleId());
        }
        for (Long circleId : loadSyncCircleIds(feed.getId())) {
            accessibleCircleIds.add(circleId);
        }
        if (accessibleCircleIds.isEmpty()) {
            return false;
        }
        for (Long circleId : accessibleCircleIds) {
            if (canAccessCircle(circleId, userId)) {
                return true;
            }
        }
        return false;
    }

    private boolean canCommentFeed(WeightCircleFeed feed, Long userId) {
        if (!isVisibleFeed(feed) || userId == null) {
            return false;
        }

        Set<Long> relatedCircleIds = new LinkedHashSet<>();
        if (feed.getOriginCircleId() != null) {
            relatedCircleIds.add(feed.getOriginCircleId());
        }
        for (Long circleId : loadSyncCircleIds(feed.getId())) {
            if (circleId != null) {
                relatedCircleIds.add(circleId);
            }
        }

        if (relatedCircleIds.isEmpty()) {
            return true;
        }

        for (Long circleId : relatedCircleIds) {
            if (canAccessCircle(circleId, userId)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAccessCircle(Long circleId, Long userId) {
        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return false;
        }
        return findActiveMember(circleId, userId) != null;
    }

    private List<Long> normalizeSyncCircleIds(List<Long> rawIds, Long originCircleId) {
        if (rawIds == null || rawIds.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> uniqueIds = new LinkedHashSet<>();
        for (Long circleId : rawIds) {
            if (circleId == null) {
                continue;
            }
            if (originCircleId != null && originCircleId.equals(circleId)) {
                continue;
            }
            uniqueIds.add(circleId);
        }
        return new ArrayList<>(uniqueIds);
    }

    private List<String> normalizeImages(List<String> rawImages) {
        if (rawImages == null || rawImages.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> images = new ArrayList<>();
        for (String image : rawImages) {
            String normalized = StringUtils.trimToNull(image);
            if (normalized != null) {
                images.add(normalized);
            }
        }
        return images;
    }

    private AjaxResult validateRecordBackedFeed(String feedType, String sourceType, Long sourceId, Long userId) {
        String expectedSourceType = expectedSourceTypeForFeedType(feedType);
        if (expectedSourceType == null) {
            return null;
        }
        if (StringUtils.isBlank(sourceType) || sourceId == null) {
            return AjaxResult.error("打卡类动态需要先完成对应记录");
        }
        if (!expectedSourceType.equals(sourceType)) {
            return AjaxResult.error("动态类型和来源记录不一致");
        }
        if (!sourceRecordBelongsToUser(expectedSourceType, sourceId, userId)) {
            return forbidden("来源记录不存在或不属于当前用户");
        }
        return null;
    }

    private String expectedSourceTypeForFeedType(String feedType) {
        String normalizedFeedType = StringUtils.trimToEmpty(feedType).toLowerCase();
        if ("weight".equals(normalizedFeedType)) {
            return "weight_record";
        }
        if ("food".equals(normalizedFeedType) || "diet".equals(normalizedFeedType)) {
            return "food_record";
        }
        if ("exercise".equals(normalizedFeedType)) {
            return "exercise_record";
        }
        if ("water".equals(normalizedFeedType)) {
            return "water_record";
        }
        if ("habit".equals(normalizedFeedType)) {
            return "habit_checkin";
        }
        return null;
    }

    private boolean sourceRecordBelongsToUser(String sourceType, Long sourceId, Long userId) {
        if (sourceId == null || userId == null) {
            return false;
        }
        if ("weight_record".equals(sourceType)) {
            WeightRecord record = weightRecordService.selectWeightRecordById(sourceId);
            return record != null && userId.equals(record.getUserId());
        }
        if ("food_record".equals(sourceType)) {
            WeightFoodRecord record = foodRecordService.selectWeightFoodRecordById(sourceId);
            return record != null && userId.equals(record.getUserId());
        }
        if ("exercise_record".equals(sourceType)) {
            WeightExerciseRecord record = exerciseRecordService.selectWeightExerciseRecordById(sourceId);
            return record != null && userId.equals(record.getUserId());
        }
        if ("water_record".equals(sourceType)) {
            WeightWaterRecord record = waterRecordService.selectWeightWaterRecordById(sourceId);
            return record != null && userId.equals(record.getUserId());
        }
        if ("habit_checkin".equals(sourceType)) {
            WeightHabitCheckin record = habitCheckinService.selectWeightHabitCheckinById(sourceId);
            return record != null && userId.equals(record.getUserId());
        }
        return false;
    }

    private String normalizeVisibilityScope(String visibilityScope) {
        if (StringUtils.isBlank(visibilityScope)) {
            return null;
        }
        String normalized = visibilityScope.trim().toLowerCase();
        if (VISIBILITY_PUBLIC.equals(normalized) || VISIBILITY_CIRCLE.equals(normalized)) {
            return normalized;
        }
        return null;
    }

    private boolean isVisibleFeed(WeightCircleFeed feed) {
        return feed != null
                && "0".equals(feed.getStatus())
                && !"2".equals(StringUtils.defaultString(feed.getDelFlag(), "0"));
    }

    private boolean isVisibleComment(WeightComment comment) {
        return comment != null
                && "0".equals(comment.getStatus())
                && !"2".equals(StringUtils.defaultString(comment.getDelFlag(), "0"));
    }

    private boolean isDeletedPlaceholder(WeightComment comment) {
        return comment != null && StringUtils.equals(COMMENT_DELETED_PLACEHOLDER, StringUtils.defaultString(comment.getContent()));
    }

    private boolean isPublicFeed(WeightCircleFeed feed) {
        return VISIBILITY_PUBLIC.equals(defaultVisibilityScope(feed));
    }

    private String defaultVisibilityScope(WeightCircleFeed feed) {
        return StringUtils.defaultIfBlank(feed.getVisibilityScope(), VISIBILITY_CIRCLE);
    }

    private Map<Long, WeightUser> loadUserMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, WeightUser> map = new HashMap<>();
        for (Long userId : userIds) {
            if (userId == null || map.containsKey(userId)) {
                continue;
            }
            WeightUser user = weightUserService.selectWeightUserByUserId(userId);
            if (user != null) {
                map.put(userId, user);
            }
        }
        return map;
    }

    private Map<Long, WeightCircle> loadCircleMap(Collection<Long> circleIds) {
        if (circleIds == null || circleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, WeightCircle> map = new LinkedHashMap<>();
        for (Long circleId : circleIds) {
            if (circleId == null || map.containsKey(circleId)) {
                continue;
            }
            WeightCircle circle = circleService.selectWeightCircleById(circleId);
            if (circle != null) {
                map.put(circleId, circle);
            }
        }
        return map;
    }

    private Map<Long, List<Long>> loadSyncCircleIdsMap(Collection<Long> feedIds) {
        if (feedIds == null || feedIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<WeightFeedCircleSync> syncList = feedCircleSyncService.selectWeightFeedCircleSyncByFeedIds(new ArrayList<>(feedIds));
        Map<Long, List<Long>> syncMap = new HashMap<>();
        for (WeightFeedCircleSync sync : syncList) {
            if (sync.getFeedId() == null || sync.getCircleId() == null) {
                continue;
            }
            if (!syncMap.containsKey(sync.getFeedId())) {
                syncMap.put(sync.getFeedId(), new ArrayList<Long>());
            }
            syncMap.get(sync.getFeedId()).add(sync.getCircleId());
        }
        return syncMap;
    }

    private List<Long> loadSyncCircleIds(Long feedId) {
        WeightFeedCircleSync query = new WeightFeedCircleSync();
        query.setFeedId(feedId);
        List<WeightFeedCircleSync> syncList = feedCircleSyncService.selectWeightFeedCircleSyncList(query);
        List<Long> circleIds = new ArrayList<>();
        for (WeightFeedCircleSync sync : syncList) {
            if (sync.getCircleId() != null) {
                circleIds.add(sync.getCircleId());
            }
        }
        return circleIds;
    }

    private WeightCircleMember findActiveMember(Long circleId, Long userId) {
        if (circleId == null || userId == null) {
            return null;
        }
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setUserId(userId);
        query.setStatus("0");
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return null;
        }
        return memberships.get(0);
    }

    private boolean isActiveCircle(WeightCircle circle) {
        return circle != null && "0".equals(circle.getStatus());
    }

    private boolean isPrivateCircle(WeightCircle circle) {
        return circle != null && "1".equals(circle.getType());
    }

    private Collection<Long> extractUserIdsFromFeeds(Collection<WeightCircleFeed> feeds) {
        List<Long> ids = new ArrayList<>();
        if (feeds == null) {
            return ids;
        }
        for (WeightCircleFeed feed : feeds) {
            if (feed != null && feed.getUserId() != null) {
                ids.add(feed.getUserId());
            }
        }
        return ids;
    }

    private Collection<Long> extractOriginCircleIds(Collection<WeightCircleFeed> feeds) {
        List<Long> ids = new ArrayList<>();
        if (feeds == null) {
            return ids;
        }
        for (WeightCircleFeed feed : feeds) {
            if (feed != null && feed.getOriginCircleId() != null) {
                ids.add(feed.getOriginCircleId());
            }
        }
        return ids;
    }

    private Collection<Long> extractFeedIds(Collection<WeightCircleFeed> feeds) {
        List<Long> ids = new ArrayList<>();
        if (feeds == null) {
            return ids;
        }
        for (WeightCircleFeed feed : feeds) {
            if (feed != null && feed.getId() != null) {
                ids.add(feed.getId());
            }
        }
        return ids;
    }

    private List<String> parseImages(String images) {
        if (StringUtils.isBlank(images)) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(images, String.class);
        } catch (Exception ignored) {
            return Collections.singletonList(images);
        }
    }

    private String formatDateTime(Date date) {
        return date != null ? DateUtils.parseDateToStr(DATE_TIME_PATTERN, date) : null;
    }

    private String buildFeedPreview(WeightCircleFeed feed) {
        if (feed == null) {
            return null;
        }

        String content = StringUtils.trimToNull(feed.getContent());
        if (content != null) {
            return StringUtils.abbreviate(content, 48);
        }
        if (StringUtils.isNotBlank(feed.getImages())) {
            return "[图片动态]";
        }
        return "[动态互动]";
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
