package com.ruoyi.api.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.api.dto.ApiRecordResponse;
import com.github.pagehelper.PageHelper;
import com.ruoyi.api.service.WechatContentSecurityService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightExerciseRecord;
import com.ruoyi.weight.domain.WeightFoodRecord;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.domain.WeightWaterRecord;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightCircleService;
import com.ruoyi.weight.service.IWeightExerciseRecordService;
import com.ruoyi.weight.service.IWeightFoodRecordService;
import com.ruoyi.weight.service.IWeightRecordService;
import com.ruoyi.weight.service.IWeightUserService;
import com.ruoyi.weight.service.IWeightWaterRecordService;
import com.ruoyi.api.service.CircleReadModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 移动端 - 体重记录 API
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/api/record")
public class ApiRecordController extends ApiBaseController {

    private static final String DATE_PARAM_PATTERN = "yyyy-MM-dd";

    @Autowired
    private IWeightRecordService weightRecordService;

    @Autowired
    private IWeightFoodRecordService foodRecordService;

    @Autowired
    private IWeightExerciseRecordService exerciseRecordService;

    @Autowired
    private IWeightWaterRecordService waterRecordService;

    @Autowired
    private IWeightCircleService circleService;

    @Autowired
    private IWeightCircleMemberService circleMemberService;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private CircleReadModelService circleReadModelService;

    @Autowired
    private WechatContentSecurityService contentSecurityService;

    // ==================== 体重记录 ====================

    /**
     * 获取体重记录列表
     */
    @GetMapping("/weight/list")
    public AjaxResult weightList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        startPage();
        PageHelper.orderBy("recorded_at desc, id desc");
        WeightRecord query = new WeightRecord();
        query.setUserId(userId);
        applyRecordedAtRange(request, query);
        List<WeightRecord> list = weightRecordService.selectWeightRecordList(query);
        return pageSuccess(list);
    }

    /**
     * 添加体重记录
     */
    @PostMapping("/weight")
    public AjaxResult addWeight(HttpServletRequest request, @RequestBody WeightRecord record) {
        Long userId = getCurrentUserId(request);
        if (record == null) {
            return AjaxResult.error("请求参数不能为空");
        }
        NormalizedSyncSelection syncSelection = normalizeRecordSyncSelection(record != null ? record.getSyncToCircles() : null, userId);
        if (syncSelection.getErrorMessage() != null) {
            return AjaxResult.error(syncSelection.getErrorMessage());
        }
        try {
            record.setImages(normalizeRecordImages(record.getImages()));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
        AjaxResult securityResult = validateRecordTexts(userId, "record.weight", record.getRemark());
        if (securityResult != null) {
            return securityResult;
        }
        record.setUserId(userId);
        if (record.getRecordedAt() == null) {
            record.setRecordedAt(new Date());
        }
        record.setSyncToCircles(syncSelection.getSerializedValue());
        weightRecordService.insertWeightRecord(record);
        WeightRecord savedRecord = weightRecordService.selectWeightRecordById(record.getId());
        ApiRecordResponse.RecordSubmitResult<WeightRecord> result = new ApiRecordResponse.RecordSubmitResult<>();
        result.setRecord(savedRecord);
        result.setDraft(buildWeightDraft(savedRecord, userId, syncSelection.getCircleIds()));
        return successWithData("新增成功", result);
    }

    /**
     * 获取最新体重
     */
    @GetMapping("/weight/latest")
    public AjaxResult latestWeight(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        WeightRecord query = new WeightRecord();
        query.setUserId(userId);
        List<WeightRecord> list = weightRecordService.selectWeightRecordList(query);
        list.sort(Comparator.comparing(WeightRecord::getRecordedAt, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightRecord::getId, Comparator.nullsLast(Long::compareTo))
                .reversed());
        if (!list.isEmpty()) {
            return successWithData(list.get(0));
        }
        return successWithData(null);
    }

    // ==================== 饮食记录 ====================

    /**
     * 获取饮食记录列表
     */
    @GetMapping("/food/list")
    public AjaxResult foodList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        startPage();
        PageHelper.orderBy("recorded_at desc, id desc");
        WeightFoodRecord query = new WeightFoodRecord();
        query.setUserId(userId);
        applyRecordedAtRange(request, query);
        List<WeightFoodRecord> list = foodRecordService.selectWeightFoodRecordList(query);
        return pageSuccess(list);
    }

    /**
     * 添加饮食记录
     */
    @PostMapping("/food")
    public AjaxResult addFood(HttpServletRequest request, @RequestBody WeightFoodRecord record) {
        Long userId = getCurrentUserId(request);
        if (record == null) {
            return AjaxResult.error("请求参数不能为空");
        }
        NormalizedSyncSelection syncSelection = normalizeRecordSyncSelection(record != null ? record.getSyncToCircles() : null, userId);
        if (syncSelection.getErrorMessage() != null) {
            return AjaxResult.error(syncSelection.getErrorMessage());
        }
        try {
            record.setImages(normalizeRecordImages(record.getImages()));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
        AjaxResult securityResult = validateRecordTexts(userId, "record.food", record.getFoodName(), record.getRemark());
        if (securityResult != null) {
            return securityResult;
        }
        record.setUserId(userId);
        if (record.getRecordedAt() == null) {
            record.setRecordedAt(new Date());
        }
        record.setSyncToCircles(syncSelection.getSerializedValue());
        foodRecordService.insertWeightFoodRecord(record);
        WeightFoodRecord savedRecord = foodRecordService.selectWeightFoodRecordById(record.getId());
        ApiRecordResponse.RecordSubmitResult<WeightFoodRecord> result = new ApiRecordResponse.RecordSubmitResult<>();
        result.setRecord(savedRecord);
        result.setDraft(buildFoodDraft(savedRecord, userId, syncSelection.getCircleIds()));
        return successWithData("新增成功", result);
    }

    // ==================== 运动记录 ====================

    /**
     * 获取运动记录列表
     */
    @GetMapping("/exercise/list")
    public AjaxResult exerciseList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        startPage();
        PageHelper.orderBy("recorded_at desc, id desc");
        WeightExerciseRecord query = new WeightExerciseRecord();
        query.setUserId(userId);
        applyRecordedAtRange(request, query);
        List<WeightExerciseRecord> list = exerciseRecordService.selectWeightExerciseRecordList(query);
        return pageSuccess(list);
    }

    /**
     * 添加运动记录
     */
    @PostMapping("/exercise")
    public AjaxResult addExercise(HttpServletRequest request, @RequestBody WeightExerciseRecord record) {
        Long userId = getCurrentUserId(request);
        if (record == null) {
            return AjaxResult.error("请求参数不能为空");
        }
        NormalizedSyncSelection syncSelection = normalizeRecordSyncSelection(record != null ? record.getSyncToCircles() : null, userId);
        if (syncSelection.getErrorMessage() != null) {
            return AjaxResult.error(syncSelection.getErrorMessage());
        }
        try {
            record.setImages(normalizeRecordImages(record.getImages()));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
        AjaxResult securityResult = validateRecordTexts(userId, "record.exercise", record.getExerciseType(), record.getRemark());
        if (securityResult != null) {
            return securityResult;
        }
        record.setUserId(userId);
        if (record.getRecordedAt() == null) {
            record.setRecordedAt(new Date());
        }
        record.setSyncToCircles(syncSelection.getSerializedValue());
        exerciseRecordService.insertWeightExerciseRecord(record);
        WeightExerciseRecord savedRecord = exerciseRecordService.selectWeightExerciseRecordById(record.getId());
        ApiRecordResponse.RecordSubmitResult<WeightExerciseRecord> result = new ApiRecordResponse.RecordSubmitResult<>();
        result.setRecord(savedRecord);
        result.setDraft(buildExerciseDraft(savedRecord, userId, syncSelection.getCircleIds()));
        return successWithData("新增成功", result);
    }

    // ==================== 饮水记录 ====================

    /**
     * 获取饮水记录列表
     */
    @GetMapping("/water/list")
    public AjaxResult waterList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        startPage();
        PageHelper.orderBy("recorded_at desc, id desc");
        WeightWaterRecord query = new WeightWaterRecord();
        query.setUserId(userId);
        applyRecordedAtRange(request, query);
        List<WeightWaterRecord> list = waterRecordService.selectWeightWaterRecordList(query);
        return pageSuccess(list);
    }

    /**
     * 添加饮水记录
     */
    @PostMapping("/water")
    public AjaxResult addWater(HttpServletRequest request, @RequestBody WeightWaterRecord record) {
        Long userId = getCurrentUserId(request);
        if (record == null) {
            return AjaxResult.error("请求参数不能为空");
        }
        try {
            record.setImages(normalizeRecordImages(record.getImages()));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
        record.setUserId(userId);
        if (record.getRecordedAt() == null) {
            record.setRecordedAt(new Date());
        }
        waterRecordService.insertWeightWaterRecord(record);
        WeightWaterRecord savedRecord = waterRecordService.selectWeightWaterRecordById(record.getId());
        ApiRecordResponse.RecordSubmitResult<WeightWaterRecord> result = new ApiRecordResponse.RecordSubmitResult<>();
        result.setRecord(savedRecord);
        result.setDraft(buildWaterDraft(savedRecord, userId));
        return successWithData("新增成功", result);
    }

    private AjaxResult validateRecordTexts(Long userId, String source, String... contents) {
        String securityMessage = contentSecurityService.validateTexts(
                userId, WechatContentSecurityService.SCENE_SOCIAL_LOG, source, contents);
        return securityMessage != null ? AjaxResult.error(securityMessage) : null;
    }

    private ApiRecordResponse.FeedDraft buildWeightDraft(WeightRecord record, Long userId, List<Long> syncCircleIds) {
        if (record == null || record.getId() == null) {
            return null;
        }
        StringBuilder content = new StringBuilder("今天记录体重 ");
        if (record.getWeight() != null) {
            content.append(formatDecimal(record.getWeight())).append("kg");
        } else {
            content.append("一次");
        }
        if (record.getBodyFatRate() != null) {
            content.append("，体脂 ").append(formatDecimal(record.getBodyFatRate())).append("%");
        }
        if (StringUtils.isNotBlank(record.getRemark())) {
            content.append("。").append(StringUtils.abbreviate(record.getRemark().trim(), 40));
        }
        return buildFeedDraft("weight", content.toString(), userId, syncCircleIds, parseRecordImages(record.getImages()), "weight_record", record.getId());
    }

    private ApiRecordResponse.FeedDraft buildFoodDraft(WeightFoodRecord record, Long userId, List<Long> syncCircleIds) {
        if (record == null || record.getId() == null) {
            return null;
        }
        StringBuilder content = new StringBuilder("今天");
        content.append(resolveMealLabel(record.getMealType())).append("记录了");
        content.append(StringUtils.defaultIfBlank(StringUtils.trimToNull(record.getFoodName()), "一餐"));
        if (record.getCalories() != null) {
            content.append("，约 ").append(record.getCalories()).append(" kcal");
        }
        if (StringUtils.isNotBlank(record.getRemark())) {
            content.append("。").append(StringUtils.abbreviate(record.getRemark().trim(), 40));
        }
        List<String> images = parseRecordImages(record.getImages());
        if (images.isEmpty() && StringUtils.isNotBlank(record.getImageUrl())) {
            images = Collections.singletonList(record.getImageUrl());
        }
        return buildFeedDraft("food", content.toString(), userId, syncCircleIds, images, "food_record", record.getId());
    }

    private ApiRecordResponse.FeedDraft buildExerciseDraft(WeightExerciseRecord record, Long userId, List<Long> syncCircleIds) {
        if (record == null || record.getId() == null) {
            return null;
        }
        StringBuilder content = new StringBuilder("今天完成了");
        content.append(StringUtils.defaultIfBlank(StringUtils.trimToNull(record.getExerciseType()), "一次运动"));
        if (record.getDurationMinutes() != null) {
            content.append("，时长 ").append(record.getDurationMinutes()).append(" 分钟");
        }
        if (record.getCaloriesBurned() != null) {
            content.append("，消耗 ").append(record.getCaloriesBurned()).append(" kcal");
        }
        if (StringUtils.isNotBlank(record.getRemark())) {
            content.append("。").append(StringUtils.abbreviate(record.getRemark().trim(), 40));
        }
        return buildFeedDraft("exercise", content.toString(), userId, syncCircleIds, parseRecordImages(record.getImages()), "exercise_record", record.getId());
    }

    private ApiRecordResponse.FeedDraft buildWaterDraft(WeightWaterRecord record, Long userId) {
        if (record == null || record.getId() == null) {
            return null;
        }
        StringBuilder content = new StringBuilder("今天补水 ");
        if (record.getMl() != null) {
            content.append(record.getMl()).append(" ml");
        } else if (record.getCups() != null) {
            content.append(record.getCups()).append(" 杯");
        } else {
            content.append("一次");
        }
        if (record.getCups() != null && record.getMl() != null) {
            content.append("（").append(record.getCups()).append(" 杯）");
        }
        return buildFeedDraft("water", content.toString(), userId, Collections.<Long>emptyList(), parseRecordImages(record.getImages()), "water_record", record.getId());
    }

    private ApiRecordResponse.FeedDraft buildFeedDraft(String feedType,
                                                       String content,
                                                       Long userId,
                                                       List<Long> syncCircleIds,
                                                       List<String> images,
                                                       String sourceType,
                                                       Long sourceId) {
        ApiRecordResponse.FeedDraft draft = new ApiRecordResponse.FeedDraft();
        draft.setFeedType(feedType);
        draft.setContent(StringUtils.abbreviate(StringUtils.trimToEmpty(content), 160));
        draft.setVisibilityScope("public");
        draft.setSyncCircleIds(syncCircleIds != null ? new ArrayList<>(syncCircleIds) : Collections.<Long>emptyList());
        draft.setOriginCircleId(resolveDefaultOriginCircleId(userId, syncCircleIds));
        draft.setImages(images != null ? new ArrayList<>(images) : Collections.<String>emptyList());
        draft.setSourceType(sourceType);
        draft.setSourceId(sourceId);
        return draft;
    }

    private Long resolveDefaultOriginCircleId(Long userId, List<Long> syncCircleIds) {
        if (syncCircleIds != null) {
            for (Long circleId : syncCircleIds) {
                if (circleId != null && canSyncToCircle(circleId, userId)) {
                    return circleId;
                }
            }
        }
        WeightUser user = userId != null ? weightUserService.selectWeightUserByUserId(userId) : null;
        if (user == null || user.getMainCircleId() == null) {
            return null;
        }
        return canSyncToCircle(user.getMainCircleId(), userId) ? user.getMainCircleId() : null;
    }

    private NormalizedSyncSelection normalizeRecordSyncSelection(String rawValue, Long userId) {
        if (StringUtils.isBlank(rawValue)) {
            return new NormalizedSyncSelection(null, Collections.<Long>emptyList(), null);
        }
        List<Long> parsedIds;
        try {
            parsedIds = JSON.parseArray(rawValue, Long.class);
        } catch (Exception error) {
            return new NormalizedSyncSelection(null, Collections.<Long>emptyList(), "同步圈子参数格式不正确");
        }
        if (parsedIds == null || parsedIds.isEmpty()) {
            return new NormalizedSyncSelection(null, Collections.<Long>emptyList(), null);
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();
        for (Long circleId : parsedIds) {
            if (circleId == null) {
                continue;
            }
            if (!canSyncToCircle(circleId, userId)) {
                return new NormalizedSyncSelection(null, Collections.<Long>emptyList(), "只能同步到自己已加入且仍在进行中的圈子");
            }
            uniqueIds.add(circleId);
        }
        List<Long> normalizedIds = new ArrayList<>(uniqueIds);
        return new NormalizedSyncSelection(
                normalizedIds.isEmpty() ? null : JSON.toJSONString(normalizedIds),
                normalizedIds,
                null
        );
    }

    private String normalizeRecordImages(String rawImages) {
        List<String> images = parseRecordImages(rawImages);
        if (images.isEmpty()) {
            return null;
        }
        if (images.size() > 9) {
            throw new IllegalArgumentException("记录图片最多上传 9 张");
        }
        return JSON.toJSONString(images);
    }

    private List<String> parseRecordImages(String rawImages) {
        if (StringUtils.isBlank(rawImages)) {
            return Collections.emptyList();
        }
        List<String> parsedImages;
        try {
            parsedImages = JSON.parseArray(rawImages, String.class);
        } catch (Exception error) {
            throw new IllegalArgumentException("记录图片参数格式不正确");
        }
        if (parsedImages == null || parsedImages.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> uniqueImages = new LinkedHashSet<>();
        for (String image : parsedImages) {
            String normalized = StringUtils.trimToNull(image);
            if (normalized != null) {
                uniqueImages.add(StringUtils.abbreviate(normalized, 512));
            }
            if (uniqueImages.size() > 9) {
                throw new IllegalArgumentException("记录图片最多上传 9 张");
            }
        }
        return new ArrayList<>(uniqueImages);
    }

    private boolean canSyncToCircle(Long circleId, Long userId) {
        if (circleId == null || userId == null) {
            return false;
        }
        WeightCircle circle = circleService.selectWeightCircleById(circleId);
        if (circle == null || !"0".equals(circle.getStatus()) || circleReadModelService.isCircleReadOnly(circle)) {
            return false;
        }
        return findActiveMember(circleId, userId) != null;
    }

    private WeightCircleMember findActiveMember(Long circleId, Long userId) {
        if (circleId == null || userId == null) {
            return null;
        }
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setUserId(userId);
        query.setStatus("0");
        List<WeightCircleMember> memberships = circleMemberService.selectWeightCircleMemberList(query);
        if (memberships == null || memberships.isEmpty()) {
            return null;
        }
        memberships.sort(Comparator.comparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(Date::compareTo)).reversed());
        return memberships.get(0);
    }

    private String resolveMealLabel(String mealType) {
        if ("1".equals(mealType)) {
            return "午餐";
        }
        if ("2".equals(mealType)) {
            return "晚餐";
        }
        if ("3".equals(mealType)) {
            return "加餐";
        }
        return "早餐";
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private void applyRecordedAtRange(HttpServletRequest request, com.ruoyi.common.core.domain.BaseEntity query) {
        Date beginRecordedAt = parseDateStart(request.getParameter("startDate"));
        Date endRecordedAt = parseDateEnd(request.getParameter("endDate"));
        if (beginRecordedAt != null) {
            query.getParams().put("beginRecordedAt", beginRecordedAt);
        }
        if (endRecordedAt != null) {
            query.getParams().put("endRecordedAt", endRecordedAt);
        }
    }

    private Date parseDateStart(String raw) {
        return parseDate(raw, false);
    }

    private Date parseDateEnd(String raw) {
        return parseDate(raw, true);
    }

    private Date parseDate(String raw, boolean endOfDay) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PARAM_PATTERN);
            formatter.setLenient(false);
            Date parsed = formatter.parse(raw.trim());
            if (!endOfDay) {
                return parsed;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsed);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        } catch (ParseException ignored) {
            return null;
        }
    }

    private static class NormalizedSyncSelection {

        private final String serializedValue;

        private final List<Long> circleIds;

        private final String errorMessage;

        private NormalizedSyncSelection(String serializedValue, List<Long> circleIds, String errorMessage) {
            this.serializedValue = serializedValue;
            this.circleIds = circleIds;
            this.errorMessage = errorMessage;
        }

        public String getSerializedValue() {
            return serializedValue;
        }

        public List<Long> getCircleIds() {
            return circleIds;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
