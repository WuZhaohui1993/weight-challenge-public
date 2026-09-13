package com.ruoyi.api.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.api.dto.ApiFeedResponse;
import com.ruoyi.api.dto.ApiUserPublicProfileResponse;
import com.ruoyi.api.dto.ApiUserProfileResponse;
import com.ruoyi.api.dto.UpdateUserSettingsRequest;
import com.ruoyi.api.service.CircleReadModelService;
import com.ruoyi.api.service.WechatContentSecurityService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.domain.WeightCircleFeed;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightCircleFeedService;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightCircleService;
import com.ruoyi.weight.service.IWeightUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 移动端 - 当前用户相关 API
 */
@RestController
@RequestMapping({"/api/user", "/api/users"})
public class ApiUserController extends ApiBaseController {

    private static final String PROFILE_PATH_SEGMENT = "/profile/";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private IWeightCircleMemberService circleMemberService;

    @Autowired
    private IWeightCircleService circleService;

    @Autowired
    private IWeightCircleFeedService feedService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private CircleReadModelService circleReadModelService;

    @Autowired
    private WechatContentSecurityService contentSecurityService;

    @GetMapping("/me")
    public AjaxResult me(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        if (weightUser == null) {
            return AjaxResult.error("用户不存在");
        }

        return successWithData(ApiUserProfileResponse.fromWeightUser(weightUser));
    }

    @GetMapping("/{userId}/profile")
    public AjaxResult publicProfile(HttpServletRequest request, @PathVariable("userId") Long userId) {
        Long currentUserId = getCurrentUserId(request);
        if (userId == null) {
            return AjaxResult.error("用户不存在");
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        if (weightUser == null) {
            return AjaxResult.error("用户不存在");
        }

        ApiUserPublicProfileResponse response = ApiUserPublicProfileResponse.fromWeightUser(weightUser);
        response.setSelf(Boolean.valueOf(userId.equals(currentUserId)));
        response.setJoinedCircleCount(loadJoinedCircleCount(userId));

        if (weightUser.getMainCircleId() != null) {
            WeightCircle mainCircle = circleService.selectWeightCircleById(weightUser.getMainCircleId());
            response.setMainCircleName(mainCircle != null ? mainCircle.getName() : null);
        }

        response.setRecentPublicFeeds(buildRecentPublicFeeds(userId, currentUserId));
        return successWithData(response);
    }

    @PutMapping("/settings")
    public AjaxResult updateSettings(HttpServletRequest request, @RequestBody UpdateUserSettingsRequest updateRequest) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        if (weightUser == null) {
            return AjaxResult.error("用户不存在");
        }

        if (updateRequest.getNickname() != null) {
            String securityMessage = contentSecurityService.validateText(
                    userId, updateRequest.getNickname(), WechatContentSecurityService.SCENE_PROFILE, "user.nickname");
            if (securityMessage != null) {
                return AjaxResult.error(securityMessage);
            }
        }

        if (updateRequest.getNickname() != null) {
            weightUser.setNickname(updateRequest.getNickname());
        }
        if (updateRequest.getAvatar() != null) {
            weightUser.setAvatar(normalizeProfileAsset(updateRequest.getAvatar()));
        }
        if (updateRequest.getGender() != null) {
            weightUser.setGender(updateRequest.getGender());
        }
        if (updateRequest.getHeight() != null) {
            weightUser.setHeight(updateRequest.getHeight());
        }
        if (updateRequest.getBirthday() != null) {
            weightUser.setBirthday(updateRequest.getBirthday());
        }
        if (updateRequest.getTargetWeight() != null) {
            weightUser.setTargetWeight(updateRequest.getTargetWeight());
        }
        if (updateRequest.getTargetCompletionDate() != null) {
            weightUser.setTargetCompletionDate(updateRequest.getTargetCompletionDate());
        }
        if (updateRequest.getCurrentWeight() != null) {
            weightUser.setCurrentWeight(updateRequest.getCurrentWeight());
        }
        if (updateRequest.getDailyWaterTarget() != null) {
            weightUser.setDailyWaterTarget(updateRequest.getDailyWaterTarget());
        }
        if (updateRequest.getMainCircleId() != null) {
            WeightCircle mainCircle = circleService.selectWeightCircleById(updateRequest.getMainCircleId());
            if (mainCircle == null || !"0".equals(mainCircle.getStatus())) {
                return AjaxResult.error("主圈子不存在");
            }
            if (circleReadModelService.isCircleExpired(mainCircle)) {
                return AjaxResult.error(circleReadModelService.resolveReadOnlyReason(mainCircle));
            }
            WeightCircleMember membership = findActiveMember(updateRequest.getMainCircleId(), userId);
            if (membership == null) {
                return AjaxResult.error("加入圈子后才能设为主圈子");
            }
            weightUser.setMainCircleId(updateRequest.getMainCircleId());
        }
        if (updateRequest.getCircleFeatureEnabled() != null) {
            weightUser.setCircleFeatureEnabled(updateRequest.getCircleFeatureEnabled());
        }
        if (updateRequest.getSyncMode() != null) {
            weightUser.setSyncMode(updateRequest.getSyncMode());
        }
        if (updateRequest.getPhone() != null) {
            weightUser.setPhone(updateRequest.getPhone());
        }

        weightUser.setUpdateBy("api");
        weightUserService.updateWeightUser(weightUser);

        WeightUser updatedUser = weightUserService.selectWeightUserByUserId(userId);
        return successWithData("更新成功", ApiUserProfileResponse.fromWeightUser(updatedUser));
    }

    @PostMapping("/avatar/upload")
    public AjaxResult uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请选择头像文件");
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        if (weightUser == null) {
            return AjaxResult.error("用户不存在");
        }

        try {
            String fileName = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            String securityMessage = contentSecurityService.validateImage(
                    resolveUploadedProfileFile(fileName), "user.avatar");
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
            return AjaxResult.error(resolveUploadErrorMessage(e, "头像"));
        }
    }

    private String normalizeProfileAsset(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            return value;
        }

        int profileIndex = value.indexOf(PROFILE_PATH_SEGMENT);
        if (profileIndex >= 0) {
            return value.substring(profileIndex);
        }

        return value;
    }

    private Long loadJoinedCircleCount(Long userId) {
        WeightCircleMember query = new WeightCircleMember();
        query.setUserId(userId);
        query.setStatus("0");
        List<WeightCircleMember> memberships = circleMemberService.selectWeightCircleMemberList(query);
        return Long.valueOf(memberships == null ? 0 : memberships.size());
    }

    private WeightCircleMember findActiveMember(Long circleId, Long userId) {
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setUserId(userId);
        query.setStatus("0");
        List<WeightCircleMember> memberships = circleMemberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return null;
        }
        memberships.sort(Comparator.comparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(java.util.Date::compareTo)).reversed());
        return memberships.get(0);
    }

    private List<ApiFeedResponse.FeedCard> buildRecentPublicFeeds(Long userId, Long currentUserId) {
        List<WeightCircleFeed> publicFeeds = feedService.selectPublicFeedList();
        if (publicFeeds == null || publicFeeds.isEmpty()) {
            return Collections.emptyList();
        }

        List<WeightCircleFeed> userFeeds = new ArrayList<>();
        for (WeightCircleFeed feed : publicFeeds) {
            if (feed != null && userId.equals(feed.getUserId())) {
                userFeeds.add(feed);
            }
        }
        userFeeds.sort(Comparator
                .comparing((WeightCircleFeed feed) -> Integer.valueOf(1).equals(feed.getIsFeatured()) ? 0 : 1)
                .thenComparing(WeightCircleFeed::getCreateTime, Comparator.nullsLast(java.util.Date::compareTo).reversed())
                .thenComparing(WeightCircleFeed::getId, Comparator.nullsLast(Long::compareTo).reversed()));

        WeightUser feedAuthor = weightUserService.selectWeightUserByUserId(userId);
        List<ApiFeedResponse.FeedCard> cards = new ArrayList<>();
        for (WeightCircleFeed feed : limitList(userFeeds, 10)) {
            WeightCircle circle = feed.getOriginCircleId() != null ? circleService.selectWeightCircleById(feed.getOriginCircleId()) : null;
            ApiFeedResponse.FeedCard card = new ApiFeedResponse.FeedCard();
            card.setId(feed.getId());
            card.setCircleId(feed.getOriginCircleId());
            card.setCircleName(circle != null ? circle.getName() : null);
            card.setOriginCircleId(feed.getOriginCircleId());
            card.setOriginCircleName(circle != null ? circle.getName() : null);
            card.setVisibilityScope("public");
            card.setCommentEnabled(Boolean.FALSE);
            card.setUserId(feed.getUserId());
            card.setUserNickname(feedAuthor != null ? feedAuthor.getNickname() : null);
            card.setUserAvatar(feedAuthor != null ? feedAuthor.getAvatar() : null);
            card.setFeedType(feed.getFeedType());
            card.setContent(feed.getContent());
            card.setImages(parseImages(feed.getImages()));
            card.setLikesCount(feed.getLikesCount());
            card.setCommentsCount(feed.getCommentsCount());
            card.setIsFeatured(Boolean.valueOf(Integer.valueOf(1).equals(feed.getIsFeatured())));
            card.setLikedByMe(Boolean.FALSE);
            card.setCreatedAt(formatDateTime(feed.getCreateTime()));
            card.setSyncCircleIds(Collections.<Long>emptyList());
            card.setOwnedByMe(Boolean.valueOf(currentUserId != null && currentUserId.equals(feed.getUserId())));
            card.setSourceType(feed.getSourceRecordType());
            card.setSourceId(feed.getSourceRecordId());
            cards.add(card);
        }
        return cards;
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

    private String formatDateTime(java.util.Date date) {
        return date != null ? DateUtils.parseDateToStr(DATE_TIME_PATTERN, date) : null;
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        int safeLimit = Math.max(limit, 0);
        int end = Math.min(list.size(), safeLimit);
        return new ArrayList<>(list.subList(0, end));
    }
}
