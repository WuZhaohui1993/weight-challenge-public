package com.ruoyi.api.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.api.dto.ApiCircleCreateRequest;
import com.ruoyi.api.dto.ApiCircleGoalConfigResponse;
import com.ruoyi.api.dto.ApiCircleResponse;
import com.ruoyi.api.dto.ApiPageResponse;
import com.ruoyi.api.security.CircleInviteTokenService;
import com.ruoyi.api.service.CircleReadModelService;
import com.ruoyi.api.service.WechatContentSecurityService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.domain.WeightCircleCategory;
import com.ruoyi.weight.domain.WeightCircleDeposit;
import com.ruoyi.weight.domain.WeightCircleFeed;
import com.ruoyi.weight.domain.WeightCircleGoal;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightCircleRanking;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.domain.WeightFeedCircleSync;
import com.ruoyi.weight.service.IWeightCircleCategoryService;
import com.ruoyi.weight.service.IWeightCircleDepositService;
import com.ruoyi.weight.service.IWeightCircleFeedService;
import com.ruoyi.weight.service.IWeightCircleGoalService;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightCircleRankingService;
import com.ruoyi.weight.service.IWeightCircleService;
import com.ruoyi.weight.service.IWeightFeedCircleSyncService;
import com.ruoyi.weight.service.IWeightNotificationService;
import com.ruoyi.weight.service.IWeightUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 移动端 - 圈子相关 API
 */
@RestController
@RequestMapping("/api/circle")
public class ApiCircleController extends ApiBaseController {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final int DETAIL_PREVIEW_MEMBER_LIMIT = 5;
    private static final int DETAIL_RANKING_PREVIEW_LIMIT = 3;
    private static final String MEMBER_STATUS_ACTIVE = "0";
    private static final String MEMBER_STATUS_LEFT = "1";
    private static final String MEMBER_STATUS_PENDING = "2";
    private static final Long GOAL_TEMPLATE_CIRCLE_ID = 0L;

    @Autowired
    private IWeightCircleService circleService;

    @Autowired
    private IWeightCircleMemberService memberService;

    @Autowired
    private IWeightCircleGoalService circleGoalService;

    @Autowired
    private IWeightCircleRankingService circleRankingService;

    @Autowired
    private IWeightCircleDepositService circleDepositService;

    @Autowired
    private IWeightCircleFeedService feedService;

    @Autowired
    private IWeightCircleCategoryService categoryService;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private IWeightFeedCircleSyncService feedCircleSyncService;

    @Autowired
    private IWeightNotificationService notificationService;

    @Autowired
    private CircleInviteTokenService circleInviteTokenService;

    @Autowired
    private CircleReadModelService circleReadModelService;

    @Autowired
    private WechatContentSecurityService contentSecurityService;

    /**
     * 获取圈子列表（公开圈子）
     */
    @GetMapping("/list")
    public AjaxResult list(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        WeightCircle query = new WeightCircle();
        query.setStatus("0");
        List<WeightCircle> circles = circleService.selectWeightCircleList(query);
        circles = filterCircles(circles,
                request.getParameter("keyword"),
                request.getParameter("categoryId"),
                request.getParameter("type"));
        if (userId == null) {
            circles = filterPublicCircles(circles);
        }
        circles.sort(Comparator
                .comparing(WeightCircle::getMemberCount, Comparator.nullsLast(Long::compareTo)).reversed()
                .thenComparing(WeightCircle::getId, Comparator.nullsLast(Long::compareTo)));

        Map<Long, WeightCircleCategory> categoryMap = loadCategoryMap(extractCategoryIds(circles));
        Map<Long, WeightCircleMember> membershipMap = loadActiveMembershipMap(userId, extractCircleIds(circles));

        List<ApiCircleResponse.CircleCard> cards = new ArrayList<>();
        for (WeightCircle circle : circles) {
            cards.add(toCircleCard(circle, categoryMap.get(circle.getCategoryId()), membershipMap.containsKey(circle.getId())));
        }
        return pageSuccessInMemory(cards);
    }

    /**
     * 获取圈子分类
     */
    @GetMapping("/categories")
    public AjaxResult categories(HttpServletRequest request) {
        WeightCircleCategory query = new WeightCircleCategory();
        query.setStatus("0");
        List<WeightCircleCategory> categories = categoryService.selectWeightCircleCategoryList(query);
        categories.sort(Comparator
                .comparing(WeightCircleCategory::getSortOrder, Comparator.nullsLast(Long::compareTo))
                .thenComparing(WeightCircleCategory::getId, Comparator.nullsLast(Long::compareTo)));

        List<ApiCircleResponse.CircleCategoryOption> items = new ArrayList<>();
        for (WeightCircleCategory category : categories) {
            ApiCircleResponse.CircleCategoryOption item = new ApiCircleResponse.CircleCategoryOption();
            item.setId(category.getId());
            item.setCode(category.getCode());
            item.setName(category.getName());
            item.setIcon(category.getIcon());
            items.add(item);
        }
        return successWithData(items);
    }

    /**
     * 获取圈子目标模板与配置项
     */
    @GetMapping("/goal-templates")
    public AjaxResult goalTemplates(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        return successWithData(buildGoalConfigCatalog());
    }

    /**
     * 获取我加入的圈子
     */
    @GetMapping("/my")
    public AjaxResult myCircles(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircleMember query = new WeightCircleMember();
        query.setUserId(userId);
        query.setStatus("0");
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        memberships.sort(Comparator
                .comparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(Date::compareTo)).reversed()
                .thenComparing(WeightCircleMember::getId, Comparator.nullsLast(Long::compareTo).reversed()));

        Map<Long, WeightCircle> circleMap = loadCircleMap(extractCircleIdsFromMembers(memberships));
        Map<Long, WeightCircleCategory> categoryMap = loadCategoryMap(extractCategoryIds(circleMap.values()));

        List<ApiCircleResponse.MyCircleCard> cards = new ArrayList<>();
        for (WeightCircleMember membership : memberships) {
            WeightCircle circle = circleMap.get(membership.getCircleId());
            if (!isActiveCircle(circle)) {
                continue;
            }
            cards.add(toMyCircleCard(circle, categoryMap.get(circle.getCategoryId()), membership));
        }
        return pageSuccessInMemory(cards);
    }

    /**
     * 获取推荐动态
     */
    @GetMapping("/feed/recommended")
    public AjaxResult recommendedFeeds(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        List<WeightCircleFeed> feeds = feedService.selectPublicFeedList();
        Map<Long, WeightCircle> circleMap = loadCircleMap(extractOriginCircleIds(feeds));
        Map<Long, WeightUser> userMap = loadUserMap(extractUserIdsFromFeeds(feeds));
        List<ApiCircleResponse.CircleFeedCard> cards = buildFeedCards(feeds, circleMap, userMap, userId);
        return pageSuccessInMemory(cards);
    }

    /**
     * 获取圈子详情
     */
    @GetMapping("/{circleId}")
    public AjaxResult getInfo(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }

        WeightCircleMember currentMember = userId != null ? findActiveMember(circleId, userId) : null;
        if (isPrivateCircle(circle) && currentMember == null) {
            return forbidden("仅圈子成员可查看私密圈子");
        }
        if (circleReadModelService.isCircleExpired(circle) && currentMember == null) {
            return AjaxResult.error("圈子已结束");
        }

        return successWithData(buildCircleDetail(circle, currentMember, userId));
    }

    /**
     * 创建圈子
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createCircle(HttpServletRequest request, @RequestBody ApiCircleCreateRequest payload) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (payload == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        String name = StringUtils.trimToEmpty(payload.getName());
        if (StringUtils.length(name) < 2 || StringUtils.length(name) > 32) {
            return AjaxResult.error("圈子名称需为 2-32 个字符");
        }
        String description = StringUtils.abbreviate(StringUtils.trimToEmpty(payload.getDescription()), 500);
        String securityMessage = contentSecurityService.validateTexts(
                userId, WechatContentSecurityService.SCENE_FORUM, "circle.create", name, description);
        if (securityMessage != null) {
            return AjaxResult.error(securityMessage);
        }

        WeightCircleCategory category = categoryService.selectWeightCircleCategoryById(payload.getCategoryId());
        if (category == null || !"0".equals(category.getStatus())) {
            return AjaxResult.error("圈子分类不存在");
        }

        BigDecimal depositRequired = payload.getDepositRequired() != null
                ? payload.getDepositRequired().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (depositRequired.compareTo(BigDecimal.ZERO) < 0) {
            return AjaxResult.error("押金金额不能小于 0");
        }

        Long durationDays = payload.getDurationDays() != null ? payload.getDurationDays() : 0L;
        if (durationDays < 0) {
            return AjaxResult.error("持续天数不能小于 0");
        }
        if (durationDays > 365L) {
            return AjaxResult.error("持续天数不能超过 365 天");
        }
        String goalValidationMessage = validateGoalConfiguration(payload);
        if (goalValidationMessage != null) {
            return AjaxResult.error(goalValidationMessage);
        }

        Date now = DateUtils.getNowDate();
        WeightCircle circle = new WeightCircle();
        circle.setName(name);
        circle.setType(normalizeCircleType(payload.getType()));
        circle.setCategoryId(category.getId());
        circle.setIcon(resolveCircleIcon(category, payload.getType()));
        circle.setCoverUrl(normalizeCoverUrl(payload.getCoverUrl()));
        circle.setDescription(description);
        circle.setCreatorId(userId);
        circle.setMemberCount(1L);
        circle.setDepositRequired(depositRequired);
        circle.setPenaltyRule("0");
        circle.setDurationDays(durationDays);
        circle.setStartDate(DateUtils.toDate(LocalDate.now()));
        circle.setEndDate(durationDays > 0 ? DateUtils.toDate(LocalDate.now().plusDays(durationDays - 1)) : null);
        circle.setStatus("0");
        circle.setDelFlag("0");
        circle.setCreateBy("api-circle");
        circle.setCreateTime(now);
        circleService.insertWeightCircle(circle);

        syncCircleGoals(circle.getId(), payload, userId);

        WeightCircleMember member = new WeightCircleMember();
        member.setCircleId(circle.getId());
        member.setUserId(userId);
        member.setRole("0");
        member.setJoinedAt(now);
        member.setIsPinned(1);
        member.setStreakDays(0L);
        member.setDepositStatus("0");
        member.setTotalCheckins(0L);
        member.setWeightChange(BigDecimal.ZERO);
        member.setStatus("0");
        memberService.insertWeightCircleMember(member);

        ensureUserMainCircleAfterJoin(userId, circle.getId());
        createSystemNotification(userId, "circle", circle.getId(), "圈子已创建", circle.getName() + " 已创建成功，可开始发布动态或邀请成员");

        WeightCircle createdCircle = circleService.selectWeightCircleById(circle.getId());
        WeightCircleMember createdMember = memberService.selectWeightCircleMemberById(member.getId());
        return successWithData("创建成功", buildCircleDetail(createdCircle, createdMember, userId));
    }

    /**
     * 编辑圈子
     */
    @PutMapping("/{circleId}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateCircle(HttpServletRequest request,
                                   @PathVariable("circleId") Long circleId,
                                   @RequestBody ApiCircleCreateRequest payload) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (payload == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (!canManageCircle(circle, currentMember, userId)) {
            return forbidden("仅圈子管理员可编辑");
        }

        String name = StringUtils.trimToEmpty(payload.getName());
        if (StringUtils.length(name) < 2 || StringUtils.length(name) > 32) {
            return AjaxResult.error("圈子名称需为 2-32 个字符");
        }
        String description = StringUtils.abbreviate(StringUtils.trimToEmpty(payload.getDescription()), 500);
        String securityMessage = contentSecurityService.validateTexts(
                userId, WechatContentSecurityService.SCENE_FORUM, "circle.update", name, description);
        if (securityMessage != null) {
            return AjaxResult.error(securityMessage);
        }

        WeightCircleCategory category = categoryService.selectWeightCircleCategoryById(payload.getCategoryId());
        if (category == null || !"0".equals(category.getStatus())) {
            return AjaxResult.error("圈子分类不存在");
        }

        BigDecimal depositRequired = payload.getDepositRequired() != null
                ? payload.getDepositRequired().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (depositRequired.compareTo(BigDecimal.ZERO) < 0) {
            return AjaxResult.error("押金金额不能小于 0");
        }

        Long durationDays = payload.getDurationDays() != null ? payload.getDurationDays() : 0L;
        if (durationDays < 0) {
            return AjaxResult.error("持续天数不能小于 0");
        }
        if (durationDays > 365L) {
            return AjaxResult.error("持续天数不能超过 365 天");
        }
        String goalValidationMessage = validateGoalConfiguration(payload);
        if (goalValidationMessage != null) {
            return AjaxResult.error(goalValidationMessage);
        }

        Date startDate = circle.getStartDate() != null ? circle.getStartDate() : DateUtils.toDate(LocalDate.now());
        LocalDate startLocalDate = startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        circle.setName(name);
        circle.setType(normalizeCircleType(payload.getType()));
        circle.setCategoryId(category.getId());
        circle.setIcon(resolveCircleIcon(category, payload.getType()));
        circle.setCoverUrl(normalizeCoverUrl(payload.getCoverUrl()));
        circle.setDescription(description);
        circle.setDepositRequired(depositRequired);
        circle.setDurationDays(durationDays);
        circle.setStartDate(startDate);
        circle.setEndDate(durationDays > 0 ? DateUtils.toDate(startLocalDate.plusDays(durationDays - 1)) : null);
        circle.setUpdateBy("api-circle");
        circleService.updateWeightCircle(circle);

        syncCircleGoals(circleId, payload, userId);

        createSystemNotification(userId, "circle", circleId, "圈子已更新", buildCirclePreview(circle));
        WeightCircle updatedCircle = circleService.selectWeightCircleById(circleId);
        WeightCircleMember refreshedMember = findActiveMember(circleId, userId);
        return successWithData("更新成功", buildCircleDetail(updatedCircle, refreshedMember, userId));
    }

    /**
     * 生成圈子邀请 token
     */
    @PostMapping("/{circleId}/invite-token")
    public AjaxResult createInviteToken(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (isPrivateCircle(circle)) {
            if (currentMember == null) {
                return forbidden("仅圈子成员可邀请加入私密圈子");
            }
        } else if (!canManageCircle(circle, currentMember, userId)) {
            return forbidden("仅管理员可生成邀请");
        }

        boolean approvalRequired = isPrivateCircle(circle) && !canManageCircle(circle, currentMember, userId);
        CircleInviteTokenService.InviteSession session = circleInviteTokenService.createInvite(circle, userId, approvalRequired);
        return successWithData(buildInvitePreview(session, null));
    }

    /**
     * 解析圈子邀请 token
     */
    @GetMapping("/invite/resolve")
    public AjaxResult resolveInvite(HttpServletRequest request, @RequestParam("token") String token) {
        CircleInviteTokenService.InviteSession session = circleInviteTokenService.resolveInvite(token);
        if (session == null) {
            return AjaxResult.error("邀请已失效，请重新获取");
        }
        WeightCircle circle = circleService.selectWeightCircleById(session.getCircleId());
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        Long userId = getCurrentUserId(request);
        WeightCircleMember member = userId != null ? findMember(circle.getId(), userId) : null;
        return successWithData(buildInvitePreview(session, member));
    }

    /**
     * 接受圈子邀请
     */
    @PostMapping("/invite/accept")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult acceptInvite(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        String token = payload != null ? StringUtils.trimToEmpty(payload.get("token")) : "";
        CircleInviteTokenService.InviteSession session = circleInviteTokenService.resolveInvite(token);
        if (session == null) {
            return AjaxResult.error("邀请已失效，请重新获取");
        }

        WeightCircle circle = circleService.selectWeightCircleById(session.getCircleId());
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        if (circleReadModelService.isCircleExpired(circle)) {
            return AjaxResult.error("圈子已结束");
        }

        if (!isPrivateCircle(circle)) {
            return joinCircle(request, circle.getId());
        }

        WeightCircleMember activeMember = findActiveMember(circle.getId(), userId);
        if (activeMember != null) {
            ApiCircleResponse.CircleMembershipState state = buildMembershipState(circle, activeMember);
            state.setPendingApproval(Boolean.FALSE);
            return successWithData("已加入该圈子", state);
        }

        WeightCircleMember member = findMember(circle.getId(), userId);
        boolean approvalRequired = Boolean.TRUE.equals(session.getApprovalRequired());
        if (member != null && MEMBER_STATUS_PENDING.equals(member.getStatus()) && approvalRequired) {
            ApiCircleResponse.CircleMembershipState state = buildMembershipState(circle, null);
            state.setPendingApproval(Boolean.TRUE);
            return successWithData("已提交申请，请等待管理员审批", state);
        }

        if (!approvalRequired) {
            return activateInviteMember(circle, member, userId);
        }

        if (member == null) {
            member = new WeightCircleMember();
            member.setCircleId(circle.getId());
            member.setUserId(userId);
            member.setRole("1");
            member.setJoinedAt(DateUtils.getNowDate());
            member.setIsPinned(0);
            member.setStreakDays(0L);
            member.setDepositStatus("0");
            member.setTotalCheckins(0L);
            member.setWeightChange(BigDecimal.ZERO);
            member.setStatus(MEMBER_STATUS_PENDING);
            memberService.insertWeightCircleMember(member);
        } else {
            member.setRole("1");
            member.setJoinedAt(DateUtils.getNowDate());
            member.setIsPinned(0);
            member.setStreakDays(0L);
            member.setDepositStatus("0");
            member.setTotalCheckins(0L);
            member.setWeightChange(BigDecimal.ZERO);
            member.setStatus(MEMBER_STATUS_PENDING);
            memberService.updateWeightCircleMember(member);
        }

        notifyCircleManagers(circle, userId, "申请加入私密圈子", buildCirclePreview(circle));
        ApiCircleResponse.CircleMembershipState state = buildMembershipState(circle, null);
        state.setPendingApproval(Boolean.TRUE);
        return successWithData("已提交申请，请等待管理员审批", state);
    }

    private AjaxResult activateInviteMember(WeightCircle circle, WeightCircleMember member, Long userId) {
        if (circle == null || circle.getId() == null || userId == null) {
            return AjaxResult.error("邀请状态异常");
        }

        if (member == null) {
            member = new WeightCircleMember();
            member.setCircleId(circle.getId());
            member.setUserId(userId);
            member.setRole("1");
            member.setJoinedAt(DateUtils.getNowDate());
            member.setIsPinned(0);
            member.setStreakDays(0L);
            member.setDepositStatus("0");
            member.setTotalCheckins(0L);
            member.setWeightChange(BigDecimal.ZERO);
            member.setStatus(MEMBER_STATUS_ACTIVE);
            memberService.insertWeightCircleMember(member);
        } else {
            member.setRole("1");
            member.setJoinedAt(DateUtils.getNowDate());
            member.setIsPinned(0);
            member.setStreakDays(0L);
            member.setDepositStatus("0");
            member.setTotalCheckins(0L);
            member.setWeightChange(BigDecimal.ZERO);
            member.setStatus(MEMBER_STATUS_ACTIVE);
            memberService.updateWeightCircleMember(member);
        }

        WeightCircle updatedCircle = syncCircleMemberCount(circle.getId());
        ensureUserMainCircleAfterJoin(userId, circle.getId());
        WeightCircleMember activeMember = memberService.selectWeightCircleMemberById(member.getId());
        createSystemNotification(userId, "circle", circle.getId(), "已通过管理员邀请加入私密圈子", buildCirclePreview(circle));
        ApiCircleResponse.CircleMembershipState state = buildMembershipState(updatedCircle, activeMember);
        state.setPendingApproval(Boolean.FALSE);
        return successWithData("已加入圈子", state);
    }

    /**
     * 获取待审批申请
     */
    @GetMapping("/{circleId}/join-requests")
    public AjaxResult joinRequests(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (!canManageCircle(circle, currentMember, userId)) {
            return forbidden("仅管理员可处理加入申请");
        }
        return successWithData(buildJoinRequestCards(loadPendingMembers(circleId)));
    }

    /**
     * 审批通过
     */
    @PostMapping("/{circleId}/join-requests/{requestId}/approve")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult approveJoinRequest(HttpServletRequest request,
                                         @PathVariable("circleId") Long circleId,
                                         @PathVariable("requestId") Long requestId) {
        return reviewJoinRequest(request, circleId, requestId, true);
    }

    /**
     * 审批拒绝
     */
    @PostMapping("/{circleId}/join-requests/{requestId}/reject")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult rejectJoinRequest(HttpServletRequest request,
                                        @PathVariable("circleId") Long circleId,
                                        @PathVariable("requestId") Long requestId) {
        return reviewJoinRequest(request, circleId, requestId, false);
    }

    /**
     * 加入圈子
     */
    @PostMapping("/{circleId}/join")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult joinCircle(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (currentMember != null) {
            ApiCircleResponse.CircleMembershipState state = buildMembershipState(circle, currentMember);
            state.setPendingApproval(Boolean.FALSE);
            return successWithData("已加入该圈子", state);
        }

        if (isPrivateCircle(circle)) {
            return forbidden("私密圈子需要通过邀请加入，或提交申请后等待管理员审批");
        }

        WeightCircleMember existingMember = findMember(circleId, userId);
        if (existingMember != null) {
            existingMember.setRole(resolveMemberRole(circle, userId));
            existingMember.setJoinedAt(new Date());
            existingMember.setIsPinned(0);
            existingMember.setStreakDays(0L);
            existingMember.setDepositStatus("0");
            existingMember.setTotalCheckins(0L);
            existingMember.setWeightChange(BigDecimal.ZERO);
            existingMember.setStatus(MEMBER_STATUS_ACTIVE);
            memberService.updateWeightCircleMember(existingMember);

            WeightCircle updatedCircle = syncCircleMemberCount(circleId);
            ensureUserMainCircleAfterJoin(userId, circleId);
            WeightCircleMember rejoinedMember = memberService.selectWeightCircleMemberById(existingMember.getId());
            createSystemNotification(userId, "circle", circleId, "重新加入圈子", buildCirclePreview(circle));
            ApiCircleResponse.CircleMembershipState state = buildMembershipState(updatedCircle, rejoinedMember);
            state.setPendingApproval(Boolean.FALSE);
            return successWithData("重新加入成功", state);
        }

        WeightCircleMember member = new WeightCircleMember();
        member.setCircleId(circleId);
        member.setUserId(userId);
        member.setRole(resolveMemberRole(circle, userId));
        member.setJoinedAt(new Date());
        member.setIsPinned(0);
        member.setStreakDays(0L);
        member.setDepositStatus("0");
        member.setTotalCheckins(0L);
        member.setWeightChange(BigDecimal.ZERO);
        member.setStatus(MEMBER_STATUS_ACTIVE);
        memberService.insertWeightCircleMember(member);

        WeightCircle updatedCircle = syncCircleMemberCount(circleId);
        ensureUserMainCircleAfterJoin(userId, circleId);
        WeightCircleMember insertedMember = memberService.selectWeightCircleMemberById(member.getId());
        createSystemNotification(userId, "circle", circleId, "已加入圈子", buildCirclePreview(circle));
        ApiCircleResponse.CircleMembershipState state = buildMembershipState(updatedCircle, insertedMember);
        state.setPendingApproval(Boolean.FALSE);
        return successWithData("加入成功", state);
    }

    /**
     * 退出圈子
     */
    @PostMapping("/{circleId}/leave")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult leaveCircle(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (currentMember == null) {
            return successWithData("已退出该圈子", buildMembershipState(circle, null));
        }

        currentMember.setStatus(MEMBER_STATUS_LEFT);
        currentMember.setIsPinned(0);
        memberService.updateWeightCircleMember(currentMember);
        WeightCircle updatedCircle = syncCircleMemberCount(circleId);
        ensureUserMainCircleAfterLeave(userId, circleId);
        createSystemNotification(userId, "circle", circleId, "已退出圈子", buildCirclePreview(circle));
        return successWithData("退出成功", buildMembershipState(updatedCircle, null));
    }

    /**
     * 获取圈子成员列表
     */
    @GetMapping("/{circleId}/members")
    public AjaxResult circleMembers(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }

        WeightCircleMember currentMember = userId != null ? findActiveMember(circleId, userId) : null;
        if (isPrivateCircle(circle) && currentMember == null) {
            return forbidden("仅圈子成员可查看私密圈子");
        }
        if (circleReadModelService.isCircleExpired(circle) && currentMember == null) {
            return AjaxResult.error("圈子已结束");
        }

        List<WeightCircleMember> members = loadCircleMembers(circleId);
        Map<Long, WeightUser> userMap = loadUserMap(extractUserIdsFromMembers(members));
        return pageSuccessInMemory(buildMemberItems(
                members,
                userMap,
                canManageCircle(circle, currentMember, userId),
                userId));
    }

    /**
     * 移除圈子成员
     */
    @DeleteMapping("/{circleId}/members/{memberUserId}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult removeCircleMember(HttpServletRequest request,
                                         @PathVariable("circleId") Long circleId,
                                         @PathVariable("memberUserId") Long memberUserId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (!canManageCircle(circle, currentMember, userId)) {
            return forbidden("仅管理员可移除成员");
        }
        if (memberUserId == null) {
            return AjaxResult.error("缺少目标成员");
        }
        if (memberUserId.equals(userId)) {
            return AjaxResult.error("不能移除自己");
        }

        WeightCircleMember targetMember = findActiveMember(circleId, memberUserId);
        if (targetMember == null) {
            return AjaxResult.error("成员不存在或已退出");
        }
        if (!"1".equals(targetMember.getRole())) {
            return AjaxResult.error("不能移除其他管理员");
        }

        targetMember.setStatus(MEMBER_STATUS_LEFT);
        targetMember.setIsPinned(0);
        targetMember.setUpdateBy("api-circle");
        memberService.updateWeightCircleMember(targetMember);

        WeightCircle updatedCircle = syncCircleMemberCount(circleId);
        ensureUserMainCircleAfterLeave(memberUserId, circleId);
        createSystemNotification(memberUserId, "circle", circleId, "你已被移出圈子", buildCirclePreview(circle));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("circleId", circleId);
        result.put("removedUserId", memberUserId);
        result.put("memberCount", updatedCircle != null ? updatedCircle.getMemberCount() : circle.getMemberCount());
        return successWithData("成员已移除", result);
    }

    /**
     * 获取圈子动态列表
     */
    @GetMapping("/{circleId}/feeds")
    public AjaxResult circleFeeds(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }

        WeightCircleMember currentMember = userId != null ? findActiveMember(circleId, userId) : null;
        if (isPrivateCircle(circle) && currentMember == null) {
            return forbidden("仅圈子成员可查看私密圈子");
        }
        if (circleReadModelService.isCircleExpired(circle) && currentMember == null) {
            return AjaxResult.error("圈子已结束");
        }

        List<WeightCircleFeed> feeds = filterFeedsForViewer(loadCircleFeeds(circleId), currentMember != null);
        Map<Long, WeightCircle> circleMap = loadCircleMap(extractOriginCircleIds(feeds));
        circleMap.put(circle.getId(), circle);
        Map<Long, WeightUser> userMap = loadUserMap(extractUserIdsFromFeeds(feeds));

        return pageSuccessInMemory(buildFeedCards(feeds, circleMap, userMap, userId));
    }

    /**
     * 发布圈子动态
     */
    @PostMapping("/{circleId}/feed")
    public AjaxResult postFeed(HttpServletRequest request,
                               @PathVariable("circleId") Long circleId,
                               @RequestBody WeightCircleFeed feed) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }
        if (findActiveMember(circleId, userId) == null) {
            return forbidden("仅圈子成员可发布圈子动态");
        }
        if (feed == null || StringUtils.isBlank(feed.getFeedType())) {
            return AjaxResult.error("feedType 不能为空");
        }
        if (StringUtils.isBlank(feed.getContent()) && StringUtils.isBlank(feed.getImages())) {
            return AjaxResult.error("动态内容和图片不能同时为空");
        }
        String securityMessage = contentSecurityService.validateText(
                userId, feed.getContent(), WechatContentSecurityService.SCENE_SOCIAL_LOG, "circle.feed.publish");
        if (securityMessage != null) {
            return AjaxResult.error(securityMessage);
        }

        feed.setCircleId(circleId);
        feed.setOriginCircleId(circleId);
        feed.setUserId(userId);
        feed.setVisibilityScope("circle");
        if (feed.getLikesCount() == null) {
            feed.setLikesCount(0L);
        }
        if (feed.getCommentsCount() == null) {
            feed.setCommentsCount(0L);
        }
        if (feed.getIsFeatured() == null) {
            feed.setIsFeatured(0);
        }
        feed.setStatus("0");
        feedService.insertWeightCircleFeed(feed);
        return successWithData("发布成功", feedService.selectWeightCircleFeedById(feed.getId()));
    }

    @GetMapping("/{circleId}/tasks/today")
    public AjaxResult todayTasks(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (currentMember == null) {
            return forbidden("加入圈子后才能查看圈子任务");
        }

        ApiCircleResponse.CircleTaskOverview overview = circleReadModelService.buildTaskOverview(circle, currentMember, userId);
        maybeCreateTaskReminder(userId, circle, overview);
        return successWithData(overview);
    }

    @PostMapping("/{circleId}/tasks/{taskId}/complete")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult completeTask(HttpServletRequest request,
                                   @PathVariable("circleId") Long circleId,
                                   @PathVariable("taskId") Long taskId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }

        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (currentMember == null) {
            return forbidden("加入圈子后才能完成圈子任务");
        }

        return successWithData("任务状态已更新", circleReadModelService.completeManualTask(circle, currentMember, userId, taskId));
    }

    @GetMapping("/{circleId}/ranking")
    public AjaxResult circleRanking(HttpServletRequest request, @PathVariable("circleId") Long circleId) {
        Long userId = getCurrentUserId(request);

        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }

        WeightCircleMember currentMember = userId != null ? findActiveMember(circleId, userId) : null;
        if (isPrivateCircle(circle) && currentMember == null) {
            return forbidden("仅圈子成员可查看私密圈子排行");
        }
        if (circleReadModelService.isCircleExpired(circle) && currentMember == null) {
            return AjaxResult.error("圈子已结束");
        }

        return pageSuccessInMemory(circleReadModelService.buildRanking(circle));
    }

    private ApiCircleResponse.CircleDetail buildCircleDetail(WeightCircle circle, WeightCircleMember currentMember, Long currentUserId) {
        Map<Long, WeightCircleCategory> categoryMap = loadCategoryMap(Collections.singleton(circle.getCategoryId()));
        ApiCircleResponse.CircleDetail detail = new ApiCircleResponse.CircleDetail();
        detail.setCircle(toCircleCard(circle, categoryMap.get(circle.getCategoryId()), currentMember != null));
        detail.setJoined(currentMember != null);
        detail.setMemberRole(currentMember != null ? currentMember.getRole() : null);
        detail.setPendingJoinRequestCount(canManageCircle(circle, currentMember, currentMember != null ? currentMember.getUserId() : null)
                ? Long.valueOf(loadPendingMembers(circle.getId()).size()) : 0L);
        detail.setGoalList(buildGoalItems(loadCircleGoals(circle.getId())));
        detail.setRuleSummary(buildRuleSummary(circle));
        detail.setDepositSummary(buildDepositSummary(circle, currentUserId));
        ApiCircleResponse.CircleTaskOverview taskOverview = circleReadModelService.buildTaskOverview(circle, currentMember, currentUserId);
        detail.setTodayTasks(taskOverview.getTasks());
        detail.setTaskSummary(taskOverview.getSummary());
        maybeCreateTaskReminder(currentUserId, circle, taskOverview);
        List<ApiCircleResponse.CircleRankingItem> ranking = circleReadModelService.buildRanking(circle);
        detail.setRankingSummary(circleReadModelService.buildRankingSummary(circle, ranking));
        detail.setPreviewMembers(Collections.<ApiCircleResponse.CirclePreviewMember>emptyList());
        detail.setRecentFeeds(Collections.<ApiCircleResponse.CircleFeedCard>emptyList());
        detail.setRankingPreview(Collections.<ApiCircleResponse.CircleRankingPreviewItem>emptyList());
        return detail;
    }

    private List<WeightCircleMember> loadCircleMembers(Long circleId) {
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setStatus(MEMBER_STATUS_ACTIVE);
        List<WeightCircleMember> members = memberService.selectWeightCircleMemberList(query);
        members.sort(Comparator
                .comparing(WeightCircleMember::getIsPinned, Comparator.nullsLast(Integer::compareTo)).reversed()
                .thenComparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightCircleMember::getId, Comparator.nullsLast(Long::compareTo)));
        return members;
    }

    private List<WeightCircleFeed> loadCircleFeeds(Long circleId) {
        return feedService.selectCircleVisibleFeeds(circleId);
    }

    private List<WeightCircleFeed> filterFeedsForViewer(List<WeightCircleFeed> feeds, boolean joined) {
        if (feeds == null || feeds.isEmpty()) {
            return Collections.emptyList();
        }
        if (joined) {
            return feeds;
        }

        List<WeightCircleFeed> visibleFeeds = new ArrayList<>();
        for (WeightCircleFeed feed : feeds) {
            if (isPublicFeed(feed)) {
                visibleFeeds.add(feed);
            }
        }
        return visibleFeeds;
    }

    private ApiCircleResponse.CircleCard toCircleCard(WeightCircle circle,
                                                      WeightCircleCategory category,
                                                      boolean joined) {
        ApiCircleResponse.CircleCard card = new ApiCircleResponse.CircleCard();
        card.setId(circle.getId());
        card.setName(circle.getName());
        card.setCategoryId(circle.getCategoryId());
        card.setIcon(circle.getIcon());
        card.setCoverUrl(circle.getCoverUrl());
        card.setType(circle.getType());
        card.setDescription(circle.getDescription());
        card.setMemberCount(circle.getMemberCount());
        card.setDepositRequired(circle.getDepositRequired());
        card.setDurationDays(circle.getDurationDays());
        card.setStartDate(formatDateOnly(circle.getStartDate()));
        card.setEndDate(formatDateOnly(circle.getEndDate()));
        card.setCategoryName(category != null ? category.getName() : null);
        card.setStatus(circle.getStatus());
        card.setJoined(joined);
        card.setLifecycleStatus(circleReadModelService.resolveLifecycleStatus(circle));
        card.setReadOnly(Boolean.valueOf(circleReadModelService.isCircleReadOnly(circle)));
        card.setReadOnlyReason(circleReadModelService.resolveReadOnlyReason(circle));
        return card;
    }

    private List<WeightCircle> filterCircles(List<WeightCircle> circles,
                                             String keyword,
                                             String categoryIdText,
                                             String type) {
        if (circles == null || circles.isEmpty()) {
            return Collections.emptyList();
        }

        Long categoryId = parseLong(categoryIdText);
        String normalizedKeyword = StringUtils.trimToEmpty(keyword).toLowerCase();
        List<WeightCircle> filtered = new ArrayList<>();
        for (WeightCircle circle : circles) {
            if (!isActiveCircle(circle)) {
                continue;
            }
            if (circleReadModelService.isCircleExpired(circle)) {
                continue;
            }
            if (StringUtils.isNotBlank(type) && !StringUtils.equals(type, circle.getType())) {
                continue;
            }
            if (categoryId != null && !categoryId.equals(circle.getCategoryId())) {
                continue;
            }
            if (StringUtils.isNotBlank(normalizedKeyword)) {
                String haystack = (StringUtils.defaultString(circle.getName())
                        + " "
                        + StringUtils.defaultString(circle.getDescription())).toLowerCase();
                if (!haystack.contains(normalizedKeyword)) {
                    continue;
                }
            }
            filtered.add(circle);
        }
        return filtered;
    }

    private List<WeightCircle> filterPublicCircles(List<WeightCircle> circles) {
        if (circles == null || circles.isEmpty()) {
            return Collections.emptyList();
        }
        List<WeightCircle> publicCircles = new ArrayList<>();
        for (WeightCircle circle : circles) {
            if (circle != null && !isPrivateCircle(circle)) {
                publicCircles.add(circle);
            }
        }
        return publicCircles;
    }

    private ApiCircleResponse.MyCircleCard toMyCircleCard(WeightCircle circle,
                                                          WeightCircleCategory category,
                                                          WeightCircleMember membership) {
        ApiCircleResponse.MyCircleCard card = new ApiCircleResponse.MyCircleCard();
        ApiCircleResponse.CircleCard baseCard = toCircleCard(circle, category, true);
        card.setId(baseCard.getId());
        card.setName(baseCard.getName());
        card.setCategoryId(baseCard.getCategoryId());
        card.setIcon(baseCard.getIcon());
        card.setCoverUrl(baseCard.getCoverUrl());
        card.setType(baseCard.getType());
        card.setDescription(baseCard.getDescription());
        card.setMemberCount(baseCard.getMemberCount());
        card.setDepositRequired(baseCard.getDepositRequired());
        card.setDurationDays(baseCard.getDurationDays());
        card.setStartDate(baseCard.getStartDate());
        card.setEndDate(baseCard.getEndDate());
        card.setCategoryName(baseCard.getCategoryName());
        card.setStatus(baseCard.getStatus());
        card.setJoined(Boolean.TRUE);
        card.setLifecycleStatus(baseCard.getLifecycleStatus());
        card.setReadOnly(baseCard.getReadOnly());
        card.setReadOnlyReason(baseCard.getReadOnlyReason());
        card.setRole(membership.getRole());
        card.setJoinedAt(formatDateTime(membership.getJoinedAt()));
        card.setStreakDays(membership.getStreakDays());
        card.setTotalCheckins(membership.getTotalCheckins());
        card.setDepositStatus(membership.getDepositStatus());
        return card;
    }

    private List<ApiCircleResponse.CirclePreviewMember> buildPreviewMembers(List<WeightCircleMember> members,
                                                                            Map<Long, WeightUser> userMap) {
        List<ApiCircleResponse.CirclePreviewMember> items = new ArrayList<>();
        for (WeightCircleMember member : limitList(members, DETAIL_PREVIEW_MEMBER_LIMIT)) {
            WeightUser user = userMap.get(member.getUserId());
            ApiCircleResponse.CirclePreviewMember item = new ApiCircleResponse.CirclePreviewMember();
            item.setUserId(member.getUserId());
            item.setNickname(user != null ? user.getNickname() : null);
            item.setAvatar(user != null ? user.getAvatar() : null);
            item.setRole(member.getRole());
            item.setStreakDays(member.getStreakDays());
            item.setTotalCheckins(member.getTotalCheckins());
            items.add(item);
        }
        return items;
    }

    private List<ApiCircleResponse.CircleMemberItem> buildMemberItems(List<WeightCircleMember> members,
                                                                      Map<Long, WeightUser> userMap,
                                                                      boolean canManage,
                                                                      Long currentUserId) {
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        List<ApiCircleResponse.CircleMemberItem> items = new ArrayList<>();
        for (WeightCircleMember member : members) {
            WeightUser user = userMap.get(member.getUserId());
            ApiCircleResponse.CircleMemberItem item = new ApiCircleResponse.CircleMemberItem();
            item.setMembershipId(member.getId());
            item.setUserId(member.getUserId());
            item.setNickname(user != null ? user.getNickname() : null);
            item.setAvatar(user != null ? user.getAvatar() : null);
            item.setRole(member.getRole());
            item.setJoinedAt(formatDateTime(member.getJoinedAt()));
            item.setStreakDays(member.getStreakDays());
            item.setTotalCheckins(member.getTotalCheckins());
            item.setWeightChange(member.getWeightChange());
            item.setDepositStatus(member.getDepositStatus());
            item.setRemovable(Boolean.valueOf(canManage
                    && member.getUserId() != null
                    && !member.getUserId().equals(currentUserId)
                    && "1".equals(member.getRole())));
            items.add(item);
        }
        return items;
    }

    private List<WeightCircleGoal> loadCircleGoals(Long circleId) {
        WeightCircleGoal query = new WeightCircleGoal();
        query.setCircleId(circleId);
        query.setStatus("0");
        List<WeightCircleGoal> goals = circleGoalService.selectWeightCircleGoalList(query);
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }
        goals.sort(Comparator
                .comparing(WeightCircleGoal::getSortOrder, Comparator.nullsLast(Long::compareTo))
                .thenComparing(WeightCircleGoal::getId, Comparator.nullsLast(Long::compareTo)));
        return goals;
    }

    private List<ApiCircleResponse.CircleGoalItem> buildGoalItems(List<WeightCircleGoal> goals) {
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }
        List<ApiCircleResponse.CircleGoalItem> items = new ArrayList<>();
        for (WeightCircleGoal goal : goals) {
            ApiCircleResponse.CircleGoalItem item = new ApiCircleResponse.CircleGoalItem();
            item.setId(goal.getId());
            item.setGoalType(goal.getGoalType());
            item.setGoalName(goal.getGoalName());
            item.setDescription(goal.getDescription());
            item.setMetricCode(circleReadModelService.resolveMetricCode(goal));
            item.setTargetValue(goal.getTargetValue());
            item.setTargetUnit(goal.getTargetUnit());
            item.setPeriod(goal.getPeriod());
            item.setPeriodLabel(circleReadModelService.resolvePeriodLabel(goal.getPeriod()));
            item.setVerificationType(goal.getVerificationType());
            item.setVerificationTypeLabel(circleReadModelService.resolveVerificationTypeLabel(goal.getVerificationType()));
            item.setPenaltyForFailure(goal.getPenaltyForFailure());
            item.setRequired(Boolean.valueOf(Integer.valueOf(1).equals(goal.getIsRequired())));
            items.add(item);
        }
        return items;
    }

    private List<WeightCircleRanking> loadCircleRankings(Long circleId) {
        WeightCircleRanking query = new WeightCircleRanking();
        query.setCircleId(circleId);
        List<WeightCircleRanking> rankings = circleRankingService.selectWeightCircleRankingList(query);
        if (rankings == null || rankings.isEmpty()) {
            return Collections.emptyList();
        }
        rankings.sort(Comparator
                .comparing(WeightCircleRanking::getPeriod, Comparator.nullsLast(String::compareTo))
                .thenComparing(WeightCircleRanking::getRankNum, Comparator.nullsLast(Long::compareTo))
                .thenComparing(WeightCircleRanking::getUserId, Comparator.nullsLast(Long::compareTo)));
        return rankings;
    }

    private List<ApiCircleResponse.CircleRankingPreviewItem> buildRankingPreview(List<WeightCircleRanking> rankings) {
        if (rankings == null || rankings.isEmpty()) {
            return Collections.emptyList();
        }
        List<WeightCircleRanking> previewList = limitList(rankings, DETAIL_RANKING_PREVIEW_LIMIT);
        Map<Long, WeightUser> userMap = loadUserMap(extractUserIdsFromRankings(previewList));
        List<ApiCircleResponse.CircleRankingPreviewItem> items = new ArrayList<>();
        for (WeightCircleRanking ranking : previewList) {
            WeightUser user = userMap.get(ranking.getUserId());
            ApiCircleResponse.CircleRankingPreviewItem item = new ApiCircleResponse.CircleRankingPreviewItem();
            item.setRankNum(ranking.getRankNum());
            item.setUserId(ranking.getUserId());
            item.setNickname(user != null ? user.getNickname() : null);
            item.setAvatar(user != null ? user.getAvatar() : null);
            item.setScore(ranking.getScore());
            items.add(item);
        }
        return items;
    }

    private ApiCircleResponse.CircleRuleSummary buildRuleSummary(WeightCircle circle) {
        ApiCircleResponse.CircleRuleSummary summary = new ApiCircleResponse.CircleRuleSummary();
        summary.setVisibilityText(isPrivateCircle(circle) ? "仅成员可见，需管理员审批" : "公开展示，可直接加入");
        summary.setJoinRuleText(isPrivateCircle(circle) ? "邀请或申请后由管理员审批" : "公开圈子支持直接加入");
        summary.setDurationText(buildRuleDurationText(circle));
        summary.setPenaltyRuleText(buildPenaltyRuleText(circle != null ? circle.getPenaltyRule() : null));
        summary.setStartDate(formatDateOnly(circle != null ? circle.getStartDate() : null));
        summary.setEndDate(formatDateOnly(circle != null ? circle.getEndDate() : null));
        return summary;
    }

    private ApiCircleResponse.CircleDepositSummary buildDepositSummary(WeightCircle circle, Long currentUserId) {
        ApiCircleResponse.CircleDepositSummary summary = new ApiCircleResponse.CircleDepositSummary();
        boolean depositEnabled = circle != null
                && circle.getDepositRequired() != null
                && circle.getDepositRequired().compareTo(BigDecimal.ZERO) > 0;
        summary.setEnabled(Boolean.valueOf(depositEnabled));
        summary.setAmount(circle != null ? circle.getDepositRequired() : null);
        if (!depositEnabled) {
            summary.setCurrentUserStatus("not_required");
            summary.setCurrentUserStatusLabel(buildDepositStatusLabel("not_required"));
            summary.setPendingCount(0L);
            summary.setPaidCount(0L);
            summary.setRefundedCount(0L);
            summary.setDeductedCount(0L);
            return summary;
        }

        WeightCircleDeposit query = new WeightCircleDeposit();
        query.setCircleId(circle != null ? circle.getId() : null);
        List<WeightCircleDeposit> deposits = circle != null ? circleDepositService.selectWeightCircleDepositList(query) : Collections.<WeightCircleDeposit>emptyList();
        Map<Long, String> depositStatusMap = new HashMap<>();
        for (WeightCircleDeposit deposit : deposits) {
            if (deposit != null && deposit.getUserId() != null) {
                depositStatusMap.put(deposit.getUserId(), StringUtils.defaultIfBlank(deposit.getStatus(), "0"));
            }
        }

        long pendingCount = 0L;
        long paidCount = 0L;
        long refundedCount = 0L;
        long deductedCount = 0L;
        for (WeightCircleMember member : loadCircleMembers(circle.getId())) {
            if (member == null || member.getUserId() == null) {
                continue;
            }
            String status = StringUtils.defaultIfBlank(depositStatusMap.get(member.getUserId()),
                    StringUtils.defaultIfBlank(member.getDepositStatus(), "0"));
            if ("1".equals(status)) {
                paidCount++;
            } else if ("2".equals(status)) {
                refundedCount++;
            } else if ("3".equals(status)) {
                deductedCount++;
            } else {
                pendingCount++;
            }
            if (currentUserId != null && currentUserId.equals(member.getUserId())) {
                summary.setCurrentUserStatus(status);
                summary.setCurrentUserStatusLabel(buildDepositStatusLabel(status));
            }
        }
        if (summary.getCurrentUserStatus() == null && currentUserId != null) {
            String currentStatus = StringUtils.defaultIfBlank(depositStatusMap.get(currentUserId), "0");
            summary.setCurrentUserStatus(currentStatus);
            summary.setCurrentUserStatusLabel(buildDepositStatusLabel(currentStatus));
        }
        summary.setPendingCount(pendingCount);
        summary.setPaidCount(paidCount);
        summary.setRefundedCount(refundedCount);
        summary.setDeductedCount(deductedCount);
        return summary;
    }

    private List<ApiCircleResponse.CircleFeedCard> buildFeedCards(List<WeightCircleFeed> feeds,
                                                                  Map<Long, WeightCircle> circleMap,
                                                                  Map<Long, WeightUser> userMap,
                                                                  Long currentUserId) {
        Map<Long, List<Long>> syncCircleIdsMap = loadSyncCircleIdsMap(extractFeedIds(feeds));
        List<ApiCircleResponse.CircleFeedCard> cards = new ArrayList<>();
        for (WeightCircleFeed feed : feeds) {
            WeightCircle circle = circleMap.get(feed.getOriginCircleId());
            WeightUser user = userMap.get(feed.getUserId());
            ApiCircleResponse.CircleFeedCard card = new ApiCircleResponse.CircleFeedCard();
            card.setId(feed.getId());
            card.setCircleId(feed.getOriginCircleId());
            card.setCircleName(circle != null ? circle.getName() : null);
            card.setOriginCircleId(feed.getOriginCircleId());
            card.setOriginCircleName(circle != null ? circle.getName() : null);
            card.setUserId(feed.getUserId());
            card.setUserNickname(user != null ? user.getNickname() : null);
            card.setUserAvatar(user != null ? user.getAvatar() : null);
            card.setFeedType(feed.getFeedType());
            card.setContent(feed.getContent());
            card.setImages(parseImages(feed.getImages()));
            card.setLikesCount(feed.getLikesCount());
            card.setCommentsCount(feed.getCommentsCount());
            card.setIsFeatured(Boolean.valueOf(Integer.valueOf(1).equals(feed.getIsFeatured())));
            card.setCreatedAt(formatDateTime(feed.getCreateTime()));
            card.setVisibilityScope(StringUtils.defaultIfBlank(feed.getVisibilityScope(), "circle"));
            card.setSyncCircleIds(syncCircleIdsMap.getOrDefault(feed.getId(), Collections.<Long>emptyList()));
            card.setCommentEnabled(Boolean.valueOf(canCommentFeed(feed, currentUserId, syncCircleIdsMap.get(feed.getId()))));
            card.setOwnedByMe(Boolean.valueOf(currentUserId != null && currentUserId.equals(feed.getUserId())));
            card.setSourceType(feed.getSourceRecordType());
            card.setSourceId(feed.getSourceRecordId());
            cards.add(card);
        }
        return cards;
    }

    private ApiCircleGoalConfigResponse buildGoalConfigCatalog() {
        ApiCircleGoalConfigResponse catalog = new ApiCircleGoalConfigResponse();
        catalog.setTemplates(buildGoalTemplateItems(loadGoalTemplates()));
        catalog.setMetricOptions(buildMetricOptions());
        catalog.setPeriodOptions(buildPeriodOptions());
        catalog.setVerificationOptions(buildVerificationOptions());
        return catalog;
    }

    private List<WeightCircleGoal> loadGoalTemplates() {
        WeightCircleGoal query = new WeightCircleGoal();
        query.setCircleId(GOAL_TEMPLATE_CIRCLE_ID);
        query.setStatus(MEMBER_STATUS_ACTIVE);
        List<WeightCircleGoal> templates = circleGoalService.selectWeightCircleGoalList(query);
        if (templates == null || templates.isEmpty()) {
            return Collections.emptyList();
        }
        templates.sort(Comparator
                .comparing(WeightCircleGoal::getSortOrder, Comparator.nullsLast(Long::compareTo))
                .thenComparing(WeightCircleGoal::getId, Comparator.nullsLast(Long::compareTo)));
        return templates;
    }

    private List<ApiCircleGoalConfigResponse.GoalTemplateItem> buildGoalTemplateItems(List<WeightCircleGoal> templates) {
        if (templates == null || templates.isEmpty()) {
            return Collections.emptyList();
        }
        List<ApiCircleGoalConfigResponse.GoalTemplateItem> items = new ArrayList<>();
        for (WeightCircleGoal goal : templates) {
            ApiCircleGoalConfigResponse.GoalTemplateItem item = new ApiCircleGoalConfigResponse.GoalTemplateItem();
            item.setId(goal.getId());
            item.setGoalType(goal.getGoalType());
            item.setGoalName(goal.getGoalName());
            item.setDescription(goal.getDescription());
            item.setMetricCode(circleReadModelService.resolveMetricCode(goal));
            item.setTargetValue(goal.getTargetValue());
            item.setTargetUnit(goal.getTargetUnit());
            item.setPeriod(goal.getPeriod());
            item.setPeriodLabel(circleReadModelService.resolvePeriodLabel(goal.getPeriod()));
            item.setVerificationType(goal.getVerificationType());
            item.setVerificationTypeLabel(circleReadModelService.resolveVerificationTypeLabel(goal.getVerificationType()));
            item.setRequired(Boolean.valueOf(Integer.valueOf(1).equals(goal.getIsRequired())));
            items.add(item);
        }
        return items;
    }

    private List<ApiCircleGoalConfigResponse.MetricOptionItem> buildMetricOptions() {
        List<ApiCircleGoalConfigResponse.MetricOptionItem> items = new ArrayList<>();
        items.add(buildMetricOption("weight_record_count", "体重记录次数", "weight_loss", "次", "按体重记录次数累计", Collections.singletonList("0")));
        items.add(buildMetricOption("weight_loss_kg", "减重公斤数", "weight_loss", "kg", "按周期内减重公斤数统计", Collections.singletonList("0")));
        items.add(buildMetricOption("exercise_minutes", "运动分钟数", "exercise", "分钟", "按运动时长累计", Collections.singletonList("0")));
        items.add(buildMetricOption("exercise_count", "运动记录次数", "exercise", "次", "按运动记录次数累计", Collections.singletonList("0")));
        items.add(buildMetricOption("food_record_count", "饮食记录次数", "diet", "次", "按饮食记录次数累计", Collections.singletonList("0")));
        items.add(buildMetricOption("habit_checkin_count", "习惯打卡次数", "habit", "次", "按习惯打卡次数累计", Collections.singletonList("0")));
        items.add(buildMetricOption("circle_checkin_count", "圈内动态次数", "checkin", "次", "按圈内动态或同步动态次数累计", Collections.singletonList("0")));
        items.add(buildMetricOption("manual_circle_checkin", "手动圈内打卡", "checkin", "次", "成员手动完成后计入任务", Collections.singletonList("1")));
        return items;
    }

    private ApiCircleGoalConfigResponse.MetricOptionItem buildMetricOption(String value,
                                                                           String label,
                                                                           String goalType,
                                                                           String defaultUnit,
                                                                           String description,
                                                                           List<String> verificationTypes) {
        ApiCircleGoalConfigResponse.MetricOptionItem item = new ApiCircleGoalConfigResponse.MetricOptionItem();
        item.setValue(value);
        item.setLabel(label);
        item.setGoalType(goalType);
        item.setDefaultUnit(defaultUnit);
        item.setDescription(description);
        item.setVerificationTypes(verificationTypes);
        return item;
    }

    private List<ApiCircleGoalConfigResponse.OptionItem> buildPeriodOptions() {
        List<ApiCircleGoalConfigResponse.OptionItem> items = new ArrayList<>();
        items.add(new ApiCircleGoalConfigResponse.OptionItem("0", "每日"));
        items.add(new ApiCircleGoalConfigResponse.OptionItem("1", "每周"));
        items.add(new ApiCircleGoalConfigResponse.OptionItem("2", "每月"));
        items.add(new ApiCircleGoalConfigResponse.OptionItem("3", "全程"));
        return items;
    }

    private List<ApiCircleGoalConfigResponse.OptionItem> buildVerificationOptions() {
        List<ApiCircleGoalConfigResponse.OptionItem> items = new ArrayList<>();
        items.add(new ApiCircleGoalConfigResponse.OptionItem("0", "自动统计"));
        items.add(new ApiCircleGoalConfigResponse.OptionItem("1", "手动完成"));
        return items;
    }

    private String validateGoalConfiguration(ApiCircleCreateRequest payload) {
        if (payload == null) {
            return null;
        }
        for (Long templateId : normalizeTemplateIds(payload.getSelectedGoalTemplateIds())) {
            WeightCircleGoal template = circleGoalService.selectWeightCircleGoalById(templateId);
            if (!isActiveGoalTemplate(template)) {
                return "存在无效的目标模板";
            }
        }
        for (ApiCircleCreateRequest.CustomGoal customGoal : safeCustomGoals(payload.getCustomGoals())) {
            try {
                buildCustomGoal(null, customGoal, 0L, 0L);
            } catch (IllegalArgumentException error) {
                return error.getMessage();
            }
        }
        return null;
    }

    private void syncCircleGoals(Long circleId, ApiCircleCreateRequest payload, Long userId) {
        if (circleId == null || payload == null) {
            return;
        }
        if (payload.getSelectedGoalTemplateIds() == null && payload.getCustomGoals() == null) {
            return;
        }

        deleteExistingCircleGoals(circleId);

        long sortOrder = 1L;
        for (Long templateId : normalizeTemplateIds(payload.getSelectedGoalTemplateIds())) {
            WeightCircleGoal template = circleGoalService.selectWeightCircleGoalById(templateId);
            if (!isActiveGoalTemplate(template)) {
                throw new IllegalArgumentException("存在无效的目标模板");
            }
            WeightCircleGoal goal = cloneTemplateGoal(circleId, template, userId, sortOrder++);
            circleGoalService.insertWeightCircleGoal(goal);
        }

        for (ApiCircleCreateRequest.CustomGoal customGoal : safeCustomGoals(payload.getCustomGoals())) {
            WeightCircleGoal goal = buildCustomGoal(circleId, customGoal, userId, sortOrder++);
            circleGoalService.insertWeightCircleGoal(goal);
        }
    }

    private List<Long> normalizeTemplateIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> uniqueIds = new java.util.LinkedHashSet<>();
        for (Long id : ids) {
            if (id != null && id > 0) {
                uniqueIds.add(id);
            }
        }
        return new ArrayList<>(uniqueIds);
    }

    private List<ApiCircleCreateRequest.CustomGoal> safeCustomGoals(List<ApiCircleCreateRequest.CustomGoal> goals) {
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }
        List<ApiCircleCreateRequest.CustomGoal> safeGoals = new ArrayList<>();
        for (ApiCircleCreateRequest.CustomGoal goal : goals) {
            if (goal != null) {
                safeGoals.add(goal);
            }
        }
        return safeGoals;
    }

    private boolean isActiveGoalTemplate(WeightCircleGoal goal) {
        return goal != null
                && GOAL_TEMPLATE_CIRCLE_ID.equals(goal.getCircleId())
                && MEMBER_STATUS_ACTIVE.equals(goal.getStatus());
    }

    private WeightCircleGoal cloneTemplateGoal(Long circleId, WeightCircleGoal template, Long userId, long sortOrder) {
        WeightCircleGoal goal = new WeightCircleGoal();
        goal.setCircleId(circleId);
        goal.setGoalType(template.getGoalType());
        goal.setGoalName(template.getGoalName());
        goal.setDescription(template.getDescription());
        goal.setMetricCode(template.getMetricCode());
        goal.setTargetValue(template.getTargetValue());
        goal.setTargetUnit(template.getTargetUnit());
        goal.setPeriod(template.getPeriod());
        goal.setVerificationType(template.getVerificationType());
        goal.setPenaltyForFailure(template.getPenaltyForFailure());
        goal.setIsRequired(template.getIsRequired());
        goal.setSortOrder(sortOrder);
        goal.setStatus(MEMBER_STATUS_ACTIVE);
        goal.setCreateBy("api-circle:" + userId);
        return goal;
    }

    private WeightCircleGoal buildCustomGoal(Long circleId,
                                             ApiCircleCreateRequest.CustomGoal customGoal,
                                             Long userId,
                                             long sortOrder) {
        String metricCode = StringUtils.trimToNull(customGoal.getMetricCode());
        if (!isSupportedMetricCode(metricCode)) {
            throw new IllegalArgumentException("存在不支持的目标指标");
        }
        if (customGoal.getTargetValue() == null || customGoal.getTargetValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("自定义目标值必须大于 0");
        }

        String verificationType = normalizeCustomVerificationType(metricCode, customGoal.getVerificationType());
        String period = normalizeCustomPeriod(customGoal.getPeriod());

        WeightCircleGoal goal = new WeightCircleGoal();
        goal.setCircleId(circleId);
        goal.setGoalType(resolveGoalTypeByMetricCode(metricCode));
        goal.setGoalName(StringUtils.defaultIfBlank(StringUtils.trimToNull(customGoal.getGoalName()), resolveMetricLabel(metricCode)));
        goal.setDescription(null);
        goal.setMetricCode(metricCode);
        goal.setTargetValue(customGoal.getTargetValue().setScale(2, RoundingMode.HALF_UP));
        goal.setTargetUnit(StringUtils.defaultIfBlank(StringUtils.trimToNull(customGoal.getTargetUnit()), resolveMetricDefaultUnit(metricCode)));
        goal.setPeriod(period);
        goal.setVerificationType(verificationType);
        goal.setPenaltyForFailure(null);
        goal.setIsRequired(Boolean.FALSE.equals(customGoal.getIsRequired()) ? 0 : 1);
        goal.setSortOrder(sortOrder);
        goal.setStatus(MEMBER_STATUS_ACTIVE);
        goal.setCreateBy("api-circle:" + userId);
        return goal;
    }

    private void deleteExistingCircleGoals(Long circleId) {
        List<WeightCircleGoal> existingGoals = loadCircleGoals(circleId);
        if (existingGoals.isEmpty()) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        for (WeightCircleGoal goal : existingGoals) {
            if (goal != null && goal.getId() != null) {
                ids.add(goal.getId());
            }
        }
        if (!ids.isEmpty()) {
            circleGoalService.deleteWeightCircleGoalByIds(ids.toArray(new Long[0]));
        }
    }

    private boolean isSupportedMetricCode(String metricCode) {
        return "weight_record_count".equals(metricCode)
                || "weight_loss_kg".equals(metricCode)
                || "exercise_minutes".equals(metricCode)
                || "exercise_count".equals(metricCode)
                || "food_record_count".equals(metricCode)
                || "habit_checkin_count".equals(metricCode)
                || "circle_checkin_count".equals(metricCode)
                || "manual_circle_checkin".equals(metricCode);
    }

    private String resolveGoalTypeByMetricCode(String metricCode) {
        if ("weight_record_count".equals(metricCode) || "weight_loss_kg".equals(metricCode)) {
            return "weight_loss";
        }
        if ("exercise_minutes".equals(metricCode) || "exercise_count".equals(metricCode)) {
            return "exercise";
        }
        if ("food_record_count".equals(metricCode)) {
            return "diet";
        }
        if ("habit_checkin_count".equals(metricCode)) {
            return "habit";
        }
        return "checkin";
    }

    private String resolveMetricLabel(String metricCode) {
        if ("weight_record_count".equals(metricCode)) {
            return "体重记录次数";
        }
        if ("weight_loss_kg".equals(metricCode)) {
            return "减重公斤数";
        }
        if ("exercise_minutes".equals(metricCode)) {
            return "运动分钟数";
        }
        if ("exercise_count".equals(metricCode)) {
            return "运动记录次数";
        }
        if ("food_record_count".equals(metricCode)) {
            return "饮食记录次数";
        }
        if ("habit_checkin_count".equals(metricCode)) {
            return "习惯打卡次数";
        }
        if ("circle_checkin_count".equals(metricCode)) {
            return "圈内动态次数";
        }
        return "手动圈内打卡";
    }

    private String resolveMetricDefaultUnit(String metricCode) {
        if ("weight_loss_kg".equals(metricCode)) {
            return "kg";
        }
        if ("exercise_minutes".equals(metricCode)) {
            return "分钟";
        }
        return "次";
    }

    private String normalizeCustomVerificationType(String metricCode, String verificationType) {
        String normalized = StringUtils.defaultIfBlank(StringUtils.trimToNull(verificationType), "0");
        if ("manual_circle_checkin".equals(metricCode)) {
            return "1";
        }
        if (!"0".equals(normalized)) {
            throw new IllegalArgumentException("该目标指标仅支持自动统计");
        }
        return normalized;
    }

    private String normalizeCustomPeriod(String period) {
        String normalized = StringUtils.defaultIfBlank(StringUtils.trimToNull(period), "0");
        if (!"0".equals(normalized) && !"1".equals(normalized) && !"2".equals(normalized) && !"3".equals(normalized)) {
            throw new IllegalArgumentException("目标周期不合法");
        }
        return normalized;
    }

    private boolean canCommentFeed(WeightCircleFeed feed, Long currentUserId, List<Long> syncCircleIds) {
        if (feed == null || currentUserId == null) {
            return false;
        }
        if (!"public".equals(StringUtils.defaultIfBlank(feed.getVisibilityScope(), "circle"))) {
            return false;
        }

        List<Long> relatedCircleIds = new ArrayList<>();
        if (feed.getOriginCircleId() != null) {
            relatedCircleIds.add(feed.getOriginCircleId());
        }
        if (syncCircleIds != null && !syncCircleIds.isEmpty()) {
            for (Long circleId : syncCircleIds) {
                if (circleId != null && !relatedCircleIds.contains(circleId)) {
                    relatedCircleIds.add(circleId);
                }
            }
        }

        if (relatedCircleIds.isEmpty()) {
            return true;
        }

        for (Long circleId : relatedCircleIds) {
            if (findActiveMember(circleId, currentUserId) != null) {
                return true;
            }
        }
        return false;
    }

    private ApiCircleResponse.CircleMembershipState buildMembershipState(WeightCircle circle, WeightCircleMember membership) {
        ApiCircleResponse.CircleMembershipState state = new ApiCircleResponse.CircleMembershipState();
        state.setCircleId(circle != null ? circle.getId() : null);
        state.setJoined(membership != null);
        state.setMemberRole(membership != null ? membership.getRole() : null);
        state.setMemberCount(circle != null ? circle.getMemberCount() : 0L);
        state.setPendingApproval(Boolean.FALSE);
        return state;
    }

    private Long parseLong(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Map<Long, WeightCircle> loadCircleMap(Collection<Long> circleIds) {
        if (circleIds == null || circleIds.isEmpty()) {
            return new LinkedHashMap<>();
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

    private Map<Long, WeightCircleCategory> loadCategoryMap(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, WeightCircleCategory> map = new HashMap<>();
        for (Long categoryId : categoryIds) {
            if (categoryId == null || map.containsKey(categoryId)) {
                continue;
            }
            WeightCircleCategory category = categoryService.selectWeightCircleCategoryById(categoryId);
            if (category != null) {
                map.put(categoryId, category);
            }
        }
        return map;
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

    private Map<Long, WeightCircleMember> loadActiveMembershipMap(Long userId, Collection<Long> circleIds) {
        if (userId == null || circleIds == null || circleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        WeightCircleMember query = new WeightCircleMember();
        query.setUserId(userId);
        query.setStatus(MEMBER_STATUS_ACTIVE);
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        Map<Long, WeightCircleMember> map = new HashMap<>();
        for (WeightCircleMember membership : memberships) {
            if (circleIds.contains(membership.getCircleId()) && !map.containsKey(membership.getCircleId())) {
                map.put(membership.getCircleId(), membership);
            }
        }
        return map;
    }

    private WeightCircleMember findActiveMember(Long circleId, Long userId) {
        if (circleId == null || userId == null) {
            return null;
        }
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setUserId(userId);
        query.setStatus(MEMBER_STATUS_ACTIVE);
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return null;
        }
        memberships.sort(Comparator.comparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(Date::compareTo)).reversed());
        return memberships.get(0);
    }

    private WeightCircleMember findMember(Long circleId, Long userId) {
        if (circleId == null || userId == null) {
            return null;
        }
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setUserId(userId);
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return null;
        }
        memberships.sort(Comparator.comparing(WeightCircleMember::getUpdateTime, Comparator.nullsLast(Date::compareTo)).reversed()
                .thenComparing(WeightCircleMember::getId, Comparator.nullsLast(Long::compareTo).reversed()));
        return memberships.get(0);
    }

    private String resolveMemberRole(WeightCircle circle, Long userId) {
        return circle != null && circle.getCreatorId() != null && circle.getCreatorId().equals(userId) ? "0" : "1";
    }

    private void ensureUserMainCircleAfterJoin(Long userId, Long circleId) {
        WeightUser user = weightUserService.selectWeightUserByUserId(userId);
        if (user == null) {
            return;
        }
        if (user.getMainCircleId() != null && findActiveMember(user.getMainCircleId(), userId) != null) {
            WeightCircle currentMainCircle = circleService.selectWeightCircleById(user.getMainCircleId());
            if (currentMainCircle != null && !circleReadModelService.isCircleExpired(currentMainCircle)) {
                return;
            }
        }
        weightUserService.updateMainCircleIdByUserId(userId, circleId, "api-circle");
    }

    private void ensureUserMainCircleAfterLeave(Long userId, Long circleId) {
        WeightUser user = weightUserService.selectWeightUserByUserId(userId);
        if (user == null) {
            return;
        }

        List<WeightCircleMember> activeMemberships = loadActiveMembershipsByUserId(userId);
        if (activeMemberships.isEmpty()) {
            if (user.getMainCircleId() != null) {
                weightUserService.clearMainCircleIdByUserId(userId, "api-circle");
            }
            return;
        }

        if (user.getMainCircleId() != null && !user.getMainCircleId().equals(circleId)
                && findActiveMember(user.getMainCircleId(), userId) != null) {
            WeightCircle currentMainCircle = circleService.selectWeightCircleById(user.getMainCircleId());
            if (currentMainCircle != null && !circleReadModelService.isCircleExpired(currentMainCircle)) {
                return;
            }
        }

        weightUserService.updateMainCircleIdByUserId(userId, activeMemberships.get(0).getCircleId(), "api-circle");
    }

    private List<WeightCircleMember> loadActiveMembershipsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        WeightCircleMember query = new WeightCircleMember();
        query.setUserId(userId);
        query.setStatus(MEMBER_STATUS_ACTIVE);
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return Collections.emptyList();
        }
        memberships.removeIf(member -> {
            WeightCircle circle = circleService.selectWeightCircleById(member.getCircleId());
            return circle == null || circleReadModelService.isCircleExpired(circle);
        });
        if (memberships.isEmpty()) {
            return Collections.emptyList();
        }
        memberships.sort(Comparator.comparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(Date::compareTo)).reversed()
                .thenComparing(WeightCircleMember::getId, Comparator.nullsLast(Long::compareTo).reversed()));
        return memberships;
    }

    private List<WeightCircleMember> loadPendingMembers(Long circleId) {
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setStatus(MEMBER_STATUS_PENDING);
        List<WeightCircleMember> memberships = memberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return Collections.emptyList();
        }
        memberships.sort(Comparator.comparing(WeightCircleMember::getUpdateTime, Comparator.nullsLast(Date::compareTo)).reversed()
                .thenComparing(WeightCircleMember::getId, Comparator.nullsLast(Long::compareTo).reversed()));
        return memberships;
    }

    private List<ApiCircleResponse.CircleJoinRequestCard> buildJoinRequestCards(List<WeightCircleMember> members) {
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, WeightUser> userMap = loadUserMap(extractUserIdsFromMembers(members));
        List<ApiCircleResponse.CircleJoinRequestCard> items = new ArrayList<>();
        for (WeightCircleMember member : members) {
            WeightUser user = userMap.get(member.getUserId());
            ApiCircleResponse.CircleJoinRequestCard item = new ApiCircleResponse.CircleJoinRequestCard();
            item.setId(member.getId());
            item.setUserId(member.getUserId());
            item.setNickname(user != null ? user.getNickname() : null);
            item.setAvatar(user != null ? user.getAvatar() : null);
            item.setRequestedAt(formatDateTime(member.getUpdateTime() != null ? member.getUpdateTime() : member.getCreateTime()));
            items.add(item);
        }
        return items;
    }

    private ApiCircleResponse.CircleInvitePreview buildInvitePreview(CircleInviteTokenService.InviteSession session,
                                                                    WeightCircleMember member) {
        ApiCircleResponse.CircleInvitePreview preview = new ApiCircleResponse.CircleInvitePreview();
        preview.setToken(session.getToken());
        preview.setCircleId(session.getCircleId());
        preview.setCircleName(session.getCircleName());
        preview.setCircleType(session.getCircleType());
        preview.setApprovalRequired(Boolean.TRUE.equals(session.getApprovalRequired()));
        preview.setExpiresAt(session.getExpiresAt());
        if (member == null) {
            preview.setMembershipStatus("none");
        } else if (MEMBER_STATUS_ACTIVE.equals(member.getStatus())) {
            preview.setMembershipStatus("joined");
        } else if (MEMBER_STATUS_PENDING.equals(member.getStatus())) {
            preview.setMembershipStatus("pending");
        } else {
            preview.setMembershipStatus("none");
        }
        return preview;
    }

    private AjaxResult reviewJoinRequest(HttpServletRequest request, Long circleId, Long requestId, boolean approve) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (!isActiveCircle(circle)) {
            return AjaxResult.error("圈子不存在");
        }
        AjaxResult readOnlyResult = rejectReadOnlyCircle(circle);
        if (readOnlyResult != null) {
            return readOnlyResult;
        }
        WeightCircleMember currentMember = findActiveMember(circleId, userId);
        if (!canManageCircle(circle, currentMember, userId)) {
            return forbidden("仅管理员可处理加入申请");
        }

        WeightCircleMember requestMember = memberService.selectWeightCircleMemberById(requestId);
        if (requestMember == null || !circleId.equals(requestMember.getCircleId()) || !MEMBER_STATUS_PENDING.equals(requestMember.getStatus())) {
            return AjaxResult.error("申请不存在或已处理");
        }

        if (approve) {
            requestMember.setStatus(MEMBER_STATUS_ACTIVE);
            requestMember.setJoinedAt(DateUtils.getNowDate());
            requestMember.setRole("1");
            memberService.updateWeightCircleMember(requestMember);
            WeightCircle updatedCircle = syncCircleMemberCount(circleId);
            ensureUserMainCircleAfterJoin(requestMember.getUserId(), circleId);
            createSystemNotification(requestMember.getUserId(), "circle", circleId, "私密圈子申请已通过", buildCirclePreview(circle));
            return successWithData("已通过申请", buildMembershipState(updatedCircle, requestMember));
        }

        requestMember.setStatus(MEMBER_STATUS_LEFT);
        memberService.updateWeightCircleMember(requestMember);
        createSystemNotification(requestMember.getUserId(), "circle", circleId, "私密圈子申请未通过", buildCirclePreview(circle));
        return successWithData("已拒绝申请", buildJoinRequestCards(loadPendingMembers(circleId)));
    }

    private void notifyCircleManagers(WeightCircle circle, Long fromUserId, String content, String preview) {
        if (circle == null) {
            return;
        }
        List<WeightCircleMember> members = loadCircleMembers(circle.getId());
        for (WeightCircleMember member : members) {
            if (member == null || !"0".equals(member.getRole()) || member.getUserId() == null) {
                continue;
            }
            WeightNotification notification = new WeightNotification();
            notification.setUserId(member.getUserId());
            notification.setType("system");
            notification.setFromUserId(fromUserId);
            notification.setTargetType("circle");
            notification.setTargetId(circle.getId());
            notification.setContent(content);
            notification.setPreview(preview);
            notification.setIsRead(0);
            notificationService.insertWeightNotification(notification);
        }
    }

    private AjaxResult rejectReadOnlyCircle(WeightCircle circle) {
        if (circleReadModelService.isCircleReadOnly(circle)) {
            return AjaxResult.error(circleReadModelService.resolveReadOnlyReason(circle));
        }
        return null;
    }

    private boolean isActiveCircle(WeightCircle circle) {
        return circle != null && MEMBER_STATUS_ACTIVE.equals(circle.getStatus());
    }

    private boolean isPrivateCircle(WeightCircle circle) {
        return circle != null && "1".equals(circle.getType());
    }

    private String normalizeCircleType(String type) {
        return "1".equals(type) ? "1" : "0";
    }

    private String resolveCircleIcon(WeightCircleCategory category, String type) {
        if (category != null && StringUtils.isNotBlank(category.getIcon())) {
            return category.getIcon();
        }
        return "1".equals(type) ? "🔒" : "⭕";
    }

    private String normalizeCoverUrl(String coverUrl) {
        return StringUtils.abbreviate(StringUtils.trimToNull(coverUrl), 512);
    }

    private boolean canManageCircle(WeightCircle circle, WeightCircleMember currentMember, Long userId) {
        if (circle == null || userId == null) {
            return false;
        }
        if (circle.getCreatorId() != null && circle.getCreatorId().equals(userId)) {
            return true;
        }
        return currentMember != null && "0".equals(currentMember.getRole());
    }

    private void createSystemNotification(Long userId, String targetType, Long targetId, String content, String preview) {
        if (userId == null || StringUtils.isBlank(content)) {
            return;
        }
        WeightNotification notification = new WeightNotification();
        notification.setUserId(userId);
        notification.setType("system");
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setContent(content);
        notification.setPreview(preview);
        notification.setIsRead(0);
        notificationService.insertWeightNotification(notification);
    }

    private void maybeCreateTaskReminder(Long userId, WeightCircle circle, ApiCircleResponse.CircleTaskOverview overview) {
        if (userId == null || circle == null || circle.getId() == null || circleReadModelService.isCircleReadOnly(circle) || overview == null || overview.getSummary() == null) {
            return;
        }

        Long remainingCount = overview.getSummary().getRemainingCount();
        if (remainingCount == null || remainingCount <= 0) {
            return;
        }
        if (hasTaskReminderToday(userId, circle.getId())) {
            return;
        }

        createSystemNotification(
                userId,
                "circle",
                circle.getId(),
                "今日任务提醒：还有 " + remainingCount + " 项圈子任务待完成",
                buildCirclePreview(circle)
        );
    }

    private boolean hasTaskReminderToday(Long userId, Long circleId) {
        WeightNotification query = new WeightNotification();
        query.setUserId(userId);
        query.setType("system");
        query.setTargetType("circle");
        query.setTargetId(circleId);
        List<WeightNotification> notifications = notificationService.selectWeightNotificationList(query);
        if (notifications == null || notifications.isEmpty()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        for (WeightNotification notification : notifications) {
            if (notification == null || notification.getCreateTime() == null) {
                continue;
            }
            if (!StringUtils.startsWith(notification.getContent(), "今日任务提醒")) {
                continue;
            }
            if (toLocalDateOnly(notification.getCreateTime()).isEqual(today)) {
                return true;
            }
        }
        return false;
    }

    private LocalDate toLocalDateOnly(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private String buildCirclePreview(WeightCircle circle) {
        if (circle == null) {
            return "圈子状态已更新";
        }
        StringBuilder builder = new StringBuilder(StringUtils.defaultIfBlank(circle.getName(), "圈子"));
        if (circle.getDurationDays() != null && circle.getDurationDays() > 0) {
            builder.append(" · ").append(circle.getDurationDays()).append(" 天计划");
        } else {
            builder.append(" · 长期挑战");
        }
        return builder.toString();
    }

    private String buildRuleDurationText(WeightCircle circle) {
        if (circle == null || circle.getDurationDays() == null || circle.getDurationDays() <= 0) {
            return "长期挑战";
        }
        if (circle.getStartDate() != null && circle.getEndDate() != null) {
            return circle.getDurationDays() + " 天 · " + formatDateOnly(circle.getStartDate()) + " 至 " + formatDateOnly(circle.getEndDate());
        }
        return circle.getDurationDays() + " 天挑战";
    }

    private String buildPenaltyRuleText(String penaltyRule) {
        if ("1".equals(penaltyRule)) {
            return "未完成将收到提醒";
        }
        if ("2".equals(penaltyRule)) {
            return "未完成可能扣除押金";
        }
        if ("3".equals(penaltyRule)) {
            return "未完成可能被移出圈子";
        }
        return "当前未设置额外惩罚";
    }

    private String buildDepositStatusLabel(String status) {
        if ("not_required".equals(status)) {
            return "无需押金";
        }
        if ("1".equals(status)) {
            return "已支付";
        }
        if ("2".equals(status)) {
            return "已退还";
        }
        if ("3".equals(status)) {
            return "已扣除";
        }
        return "待支付";
    }

    private boolean isPublicFeed(WeightCircleFeed feed) {
        return "public".equals(StringUtils.defaultIfBlank(feed != null ? feed.getVisibilityScope() : null, "circle"));
    }

    private Collection<Long> extractCircleIds(Collection<WeightCircle> circles) {
        List<Long> ids = new ArrayList<>();
        if (circles == null) {
            return ids;
        }
        for (WeightCircle circle : circles) {
            if (circle != null && circle.getId() != null) {
                ids.add(circle.getId());
            }
        }
        return ids;
    }

    private Collection<Long> extractUserIdsFromRankings(Collection<WeightCircleRanking> rankings) {
        Set<Long> ids = new java.util.LinkedHashSet<>();
        if (rankings == null) {
            return ids;
        }
        for (WeightCircleRanking ranking : rankings) {
            if (ranking != null && ranking.getUserId() != null) {
                ids.add(ranking.getUserId());
            }
        }
        return ids;
    }

    private Collection<Long> extractCategoryIds(Collection<WeightCircle> circles) {
        List<Long> ids = new ArrayList<>();
        if (circles == null) {
            return ids;
        }
        for (WeightCircle circle : circles) {
            if (circle != null && circle.getCategoryId() != null) {
                ids.add(circle.getCategoryId());
            }
        }
        return ids;
    }

    private Collection<Long> extractCircleIdsFromMembers(Collection<WeightCircleMember> members) {
        List<Long> ids = new ArrayList<>();
        if (members == null) {
            return ids;
        }
        for (WeightCircleMember member : members) {
            if (member != null && member.getCircleId() != null) {
                ids.add(member.getCircleId());
            }
        }
        return ids;
    }

    private Collection<Long> extractUserIdsFromMembers(Collection<WeightCircleMember> members) {
        List<Long> ids = new ArrayList<>();
        if (members == null) {
            return ids;
        }
        for (WeightCircleMember member : members) {
            if (member != null && member.getUserId() != null) {
                ids.add(member.getUserId());
            }
        }
        return ids;
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

    private List<String> parseImages(String images) {
        if (images == null || images.trim().isEmpty()) {
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

    private String formatDateOnly(Date date) {
        return date != null ? DateUtils.parseDateToStr("yyyy-MM-dd", date) : null;
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        int safeLimit = Math.max(limit, 0);
        int end = Math.min(list.size(), safeLimit);
        return new ArrayList<>(list.subList(0, end));
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

    private WeightCircle syncCircleMemberCount(Long circleId) {
        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (circle == null) {
            return null;
        }

        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setStatus("0");
        List<WeightCircleMember> members = memberService.selectWeightCircleMemberList(query);
        circle.setMemberCount((long) members.size());
        circleService.updateWeightCircle(circle);
        return circleService.selectWeightCircleById(circleId);
    }
}
