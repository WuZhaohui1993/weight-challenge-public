package com.ruoyi.api.service;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.api.dto.ApiCircleResponse;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.domain.WeightCircleFeed;
import com.ruoyi.weight.domain.WeightCircleGoal;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightCircleRanking;
import com.ruoyi.weight.domain.WeightDailyTask;
import com.ruoyi.weight.domain.WeightExerciseRecord;
import com.ruoyi.weight.domain.WeightFoodRecord;
import com.ruoyi.weight.domain.WeightHabitCheckin;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightCircleFeedService;
import com.ruoyi.weight.service.IWeightCircleGoalService;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightCircleRankingService;
import com.ruoyi.weight.service.IWeightDailyTaskService;
import com.ruoyi.weight.service.IWeightExerciseRecordService;
import com.ruoyi.weight.service.IWeightFoodRecordService;
import com.ruoyi.weight.service.IWeightHabitCheckinService;
import com.ruoyi.weight.service.IWeightRecordService;
import com.ruoyi.weight.service.IWeightUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CircleReadModelService {

    private static final String ACTIVE_STATUS = "0";
    private static final String TASK_TYPE_CIRCLE = "circle";
    private static final String VERIFICATION_AUTO = "0";
    private static final String VERIFICATION_MANUAL = "1";
    private static final String RANKING_PERIOD_WEEKLY = "1";
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Autowired
    private IWeightCircleGoalService circleGoalService;

    @Autowired
    private IWeightDailyTaskService dailyTaskService;

    @Autowired
    private IWeightRecordService recordService;

    @Autowired
    private IWeightFoodRecordService foodRecordService;

    @Autowired
    private IWeightExerciseRecordService exerciseRecordService;

    @Autowired
    private IWeightHabitCheckinService habitCheckinService;

    @Autowired
    private IWeightCircleFeedService circleFeedService;

    @Autowired
    private IWeightCircleMemberService circleMemberService;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private IWeightCircleRankingService circleRankingService;

    public boolean isCircleExpired(WeightCircle circle) {
        if (!isCircleActive(circle)) {
            return false;
        }
        if (circle.getDurationDays() == null || circle.getDurationDays() <= 0 || circle.getEndDate() == null) {
            return false;
        }
        return toLocalDate(circle.getEndDate()).isBefore(LocalDate.now());
    }

    public boolean isCircleActive(WeightCircle circle) {
        return circle != null && ACTIVE_STATUS.equals(circle.getStatus());
    }

    public String resolveLifecycleStatus(WeightCircle circle) {
        return isCircleExpired(circle) ? "expired" : "active";
    }

    public boolean isCircleReadOnly(WeightCircle circle) {
        return isCircleExpired(circle);
    }

    public String resolveReadOnlyReason(WeightCircle circle) {
        if (!isCircleReadOnly(circle)) {
            return null;
        }
        if (circle != null && circle.getEndDate() != null) {
            return "圈子已于 " + formatDateOnly(circle.getEndDate()) + " 到期，当前仅支持查看历史内容";
        }
        return "当前圈子已到期，暂不支持继续写入";
    }

    public List<WeightCircleGoal> loadEnabledGoals(Long circleId) {
        if (circleId == null) {
            return Collections.emptyList();
        }
        WeightCircleGoal query = new WeightCircleGoal();
        query.setCircleId(circleId);
        query.setStatus(ACTIVE_STATUS);
        List<WeightCircleGoal> goals = circleGoalService.selectWeightCircleGoalList(query);
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }
        goals.sort(Comparator
                .comparing(WeightCircleGoal::getSortOrder, Comparator.nullsLast(Long::compareTo))
                .thenComparing(WeightCircleGoal::getId, Comparator.nullsLast(Long::compareTo)));
        return goals;
    }

    public String resolveMetricCode(WeightCircleGoal goal) {
        if (goal == null) {
            return null;
        }
        if (StringUtils.isNotBlank(goal.getMetricCode())) {
            return goal.getMetricCode().trim();
        }

        String goalType = StringUtils.defaultString(goal.getGoalType()).trim();
        String targetUnit = StringUtils.defaultString(goal.getTargetUnit()).trim();
        String verificationType = StringUtils.defaultString(goal.getVerificationType()).trim();

        if ("weight_loss".equals(goalType)) {
            if (containsAny(targetUnit, "kg", "KG", "公斤")) {
                return "weight_loss_kg";
            }
            return "weight_record_count";
        }
        if ("exercise".equals(goalType)) {
            if (containsAny(targetUnit, "分钟", "min", "MIN")) {
                return "exercise_minutes";
            }
            return "exercise_count";
        }
        if ("diet".equals(goalType)) {
            return "food_record_count";
        }
        if ("habit".equals(goalType)) {
            return "habit_checkin_count";
        }
        if ("checkin".equals(goalType)) {
            return VERIFICATION_MANUAL.equals(verificationType) ? "manual_circle_checkin" : "circle_checkin_count";
        }
        if (VERIFICATION_MANUAL.equals(verificationType)) {
            return "manual_circle_checkin";
        }
        return null;
    }

    public String resolveVerificationTypeLabel(String verificationType) {
        if ("1".equals(verificationType)) {
            return "手动完成";
        }
        if ("2".equals(verificationType)) {
            return "拍照证明";
        }
        if ("3".equals(verificationType)) {
            return "管理员审核";
        }
        return "自动统计";
    }

    public String resolvePeriodLabel(String period) {
        if ("1".equals(period)) {
            return "每周";
        }
        if ("2".equals(period)) {
            return "每月";
        }
        if ("3".equals(period)) {
            return "全程";
        }
        return "每日";
    }

    public ApiCircleResponse.CircleTaskOverview buildTaskOverview(WeightCircle circle,
                                                                 WeightCircleMember membership,
                                                                 Long userId) {
        ApiCircleResponse.CircleTaskOverview overview = new ApiCircleResponse.CircleTaskOverview();
        List<ApiCircleResponse.CircleTaskItem> items = new ArrayList<>();
        ApiCircleResponse.CircleTaskSummary summary = new ApiCircleResponse.CircleTaskSummary();
        summary.setReadOnly(Boolean.valueOf(isCircleReadOnly(circle)));
        summary.setReadOnlyReason(resolveReadOnlyReason(circle));

        if (circle == null || membership == null || userId == null) {
            summary.setTotalCount(0L);
            summary.setCompletedCount(0L);
            summary.setRemainingCount(0L);
            summary.setPendingPenaltyCount(0L);
            summary.setPenaltyRuleText(buildPenaltyRuleText(circle != null ? circle.getPenaltyRule() : null));
            overview.setTasks(items);
            overview.setSummary(summary);
            return overview;
        }

        LocalDate referenceDate = resolveReferenceDate(circle);
        List<WeightCircleGoal> goals = loadEnabledGoals(circle.getId());
        for (WeightCircleGoal goal : goals) {
            if (!supportsMiniTask(goal)) {
                continue;
            }
            items.add(upsertCircleTask(circle, membership, userId, goal, referenceDate));
        }

        long completedCount = items.stream().filter(item -> Boolean.TRUE.equals(item.getCompleted())).count();
        long pendingPenaltyCount = items.stream().filter(item -> Boolean.TRUE.equals(item.getPendingPenalty())).count();
        summary.setTotalCount((long) items.size());
        summary.setCompletedCount(completedCount);
        summary.setRemainingCount(Math.max(0L, items.size() - completedCount));
        summary.setPendingPenaltyCount(pendingPenaltyCount);
        summary.setPenaltyRuleText(buildPenaltyRuleText(circle.getPenaltyRule()));
        overview.setTasks(items);
        overview.setSummary(summary);
        return overview;
    }

    public ApiCircleResponse.CircleTaskOverview completeManualTask(WeightCircle circle,
                                                                   WeightCircleMember membership,
                                                                   Long userId,
                                                                   Long taskId) {
        ApiCircleResponse.CircleTaskOverview overview = buildTaskOverview(circle, membership, userId);
        if (taskId == null || circle == null || membership == null || userId == null) {
            return overview;
        }

        WeightDailyTask task = dailyTaskService.selectWeightDailyTaskById(taskId);
        if (task == null
                || !userId.equals(task.getUserId())
                || !circle.getId().equals(task.getCircleId())
                || !TASK_TYPE_CIRCLE.equals(task.getTaskType())) {
            return overview;
        }

        WeightCircleGoal goal = findGoalById(circle.getId(), task.getSourceId());
        if (goal == null || !VERIFICATION_MANUAL.equals(StringUtils.defaultString(goal.getVerificationType()))) {
            return overview;
        }

        BigDecimal targetValue = safeTargetValue(goal.getTargetValue());
        task.setCurrentValue(targetValue);
        task.setIsCompleted(1);
        task.setCompletedAt(DateUtils.getNowDate());
        task.setUpdateBy("api-circle-task");
        dailyTaskService.updateWeightDailyTask(task);
        return buildTaskOverview(circle, membership, userId);
    }

    public List<ApiCircleResponse.CircleRankingItem> buildRanking(WeightCircle circle) {
        if (circle == null || circle.getId() == null) {
            return Collections.emptyList();
        }

        List<WeightCircleMember> members = loadActiveMembers(circle.getId());
        if (members.isEmpty()) {
            return Collections.emptyList();
        }

        List<ApiCircleResponse.CircleRankingItem> items = new ArrayList<>();
        for (WeightCircleMember member : members) {
            ApiCircleResponse.CircleTaskOverview taskOverview = buildTaskOverview(circle, member, member.getUserId());
            WeightUser user = weightUserService.selectWeightUserByUserId(member.getUserId());
            ApiCircleResponse.CircleRankingItem item = new ApiCircleResponse.CircleRankingItem();
            item.setUserId(member.getUserId());
            item.setNickname(user != null ? user.getNickname() : null);
            item.setAvatar(user != null ? user.getAvatar() : null);
            item.setCompletedTaskCount(taskOverview.getSummary() != null ? taskOverview.getSummary().getCompletedCount() : 0L);
            item.setCircleCheckins(countCircleCheckins(circle.getId(), member.getUserId(), buildRankingWindow(circle, member, resolveReferenceDate(circle))));
            item.setStreakDays(member.getStreakDays() != null ? member.getStreakDays() : 0L);
            item.setJoinedAt(formatDateOnly(member.getJoinedAt()));
            item.setScore(buildRankingScore(item));
            items.add(item);
        }

        items.sort(Comparator
                .comparing(ApiCircleResponse.CircleRankingItem::getCircleCheckins, Comparator.nullsLast(Long::compareTo)).reversed()
                .thenComparing(ApiCircleResponse.CircleRankingItem::getCompletedTaskCount, Comparator.nullsLast(Long::compareTo)).reversed()
                .thenComparing(ApiCircleResponse.CircleRankingItem::getStreakDays, Comparator.nullsLast(Long::compareTo)).reversed()
                .thenComparing(item -> parseJoinedAt(item.getJoinedAt()), Comparator.nullsLast(LocalDate::compareTo))
                .thenComparing(ApiCircleResponse.CircleRankingItem::getUserId, Comparator.nullsLast(Long::compareTo)));

        long rank = 1L;
        for (ApiCircleResponse.CircleRankingItem item : items) {
            item.setRankNum(rank++);
        }
        syncRankingSnapshot(circle, items);
        return items;
    }

    public ApiCircleResponse.CircleRankingSummary buildRankingSummary(WeightCircle circle,
                                                                      List<ApiCircleResponse.CircleRankingItem> ranking) {
        ApiCircleResponse.CircleRankingSummary summary = new ApiCircleResponse.CircleRankingSummary();
        summary.setPeriodLabel("本周排行");
        summary.setRuleText("按圈内打卡次数、已完成任务数、连续打卡天数排序");
        summary.setLeader(ranking != null && !ranking.isEmpty() ? ranking.get(0) : null);
        summary.setUpdatedAt(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", DateUtils.getNowDate()));
        return summary;
    }

    private ApiCircleResponse.CircleTaskItem upsertCircleTask(WeightCircle circle,
                                                              WeightCircleMember membership,
                                                              Long userId,
                                                              WeightCircleGoal goal,
                                                              LocalDate referenceDate) {
        PeriodWindow window = buildGoalWindow(goal, circle, membership, referenceDate);
        WeightDailyTask task = findOrCreateTask(circle, userId, goal, window);
        String metricCode = resolveMetricCode(goal);
        BigDecimal targetValue = safeTargetValue(goal.getTargetValue());

        if (VERIFICATION_AUTO.equals(StringUtils.defaultString(goal.getVerificationType()))) {
            BigDecimal currentValue = calculateProgress(metricCode, circle, membership, userId, window);
            task.setCurrentValue(currentValue);
            boolean completed = currentValue.compareTo(targetValue) >= 0;
            task.setIsCompleted(completed ? 1 : 0);
            task.setCompletedAt(completed ? (task.getCompletedAt() != null ? task.getCompletedAt() : DateUtils.getNowDate()) : null);
            task.setUpdateBy("api-circle-task");
            persistTask(task);
        } else if (task.getCurrentValue() == null) {
            task.setCurrentValue(BigDecimal.ZERO);
            task.setUpdateBy("api-circle-task");
            persistTask(task);
        }

        boolean completed = Integer.valueOf(1).equals(task.getIsCompleted());
        boolean pendingPenalty = Boolean.TRUE.equals(isCircleReadOnly(circle)) && !completed && !"0".equals(StringUtils.defaultIfBlank(circle.getPenaltyRule(), "0"));

        ApiCircleResponse.CircleTaskItem item = new ApiCircleResponse.CircleTaskItem();
        item.setId(task.getId());
        item.setGoalId(goal.getId());
        item.setGoalName(StringUtils.defaultIfBlank(goal.getGoalName(), "未命名任务"));
        item.setDescription(StringUtils.defaultIfBlank(goal.getDescription(), buildGoalFallbackDescription(goal)));
        item.setMetricCode(metricCode);
        item.setTargetValue(targetValue);
        item.setCurrentValue(task.getCurrentValue() != null ? task.getCurrentValue() : BigDecimal.ZERO);
        item.setTargetUnit(goal.getTargetUnit());
        item.setPeriod(goal.getPeriod());
        item.setPeriodLabel(resolvePeriodLabel(goal.getPeriod()));
        item.setVerificationType(goal.getVerificationType());
        item.setVerificationTypeLabel(resolveVerificationTypeLabel(goal.getVerificationType()));
        item.setCompleted(Boolean.valueOf(completed));
        item.setManual(Boolean.valueOf(VERIFICATION_MANUAL.equals(StringUtils.defaultString(goal.getVerificationType()))));
        item.setReadOnly(Boolean.valueOf(isCircleReadOnly(circle)));
        item.setReadOnlyReason(resolveReadOnlyReason(circle));
        item.setCompletedAt(formatDateTime(task.getCompletedAt()));
        item.setPendingPenalty(Boolean.valueOf(pendingPenalty));
        item.setPenaltyRuleText(StringUtils.defaultIfBlank(goal.getPenaltyForFailure(), buildPenaltyRuleText(circle.getPenaltyRule())));
        return item;
    }

    private WeightDailyTask findOrCreateTask(WeightCircle circle,
                                             Long userId,
                                             WeightCircleGoal goal,
                                             PeriodWindow window) {
        WeightDailyTask query = new WeightDailyTask();
        query.setUserId(userId);
        query.setCircleId(circle.getId());
        query.setTaskType(TASK_TYPE_CIRCLE);
        query.setSourceId(goal.getId());
        query.setPeriodKey(window.key);
        List<WeightDailyTask> tasks = dailyTaskService.selectWeightDailyTaskList(query);
        if (tasks != null && !tasks.isEmpty()) {
            tasks.sort(Comparator.comparing(WeightDailyTask::getId, Comparator.nullsLast(Long::compareTo)));
            return tasks.get(0);
        }

        WeightDailyTask task = new WeightDailyTask();
        task.setUserId(userId);
        task.setCircleId(circle.getId());
        task.setTaskDate(DateUtils.toDate(window.start));
        task.setTaskType(TASK_TYPE_CIRCLE);
        task.setTaskName(StringUtils.defaultIfBlank(goal.getGoalName(), "圈子任务"));
        task.setPeriodKey(window.key);
        task.setSourceId(goal.getId());
        task.setTargetValue(safeTargetValue(goal.getTargetValue()));
        task.setCurrentValue(BigDecimal.ZERO);
        task.setIsCompleted(0);
        task.setPointsReward(0L);
        task.setCreateBy("api-circle-task");
        dailyTaskService.insertWeightDailyTask(task);
        return task;
    }

    private void persistTask(WeightDailyTask task) {
        if (task.getId() == null) {
            dailyTaskService.insertWeightDailyTask(task);
        } else {
            dailyTaskService.updateWeightDailyTask(task);
        }
    }

    private BigDecimal calculateProgress(String metricCode,
                                         WeightCircle circle,
                                         WeightCircleMember membership,
                                         Long userId,
                                         PeriodWindow window) {
        if (StringUtils.isBlank(metricCode) || userId == null || window == null) {
            return BigDecimal.ZERO;
        }
        if ("weight_record_count".equals(metricCode)) {
            return BigDecimal.valueOf(countWeightRecords(circle != null ? circle.getId() : null, userId, window));
        }
        if ("weight_loss_kg".equals(metricCode)) {
            return calculateWeightLoss(circle != null ? circle.getId() : null, userId, window);
        }
        if ("exercise_minutes".equals(metricCode)) {
            return BigDecimal.valueOf(sumExerciseMinutes(circle != null ? circle.getId() : null, userId, window));
        }
        if ("exercise_count".equals(metricCode)) {
            return BigDecimal.valueOf(countExerciseRecords(circle != null ? circle.getId() : null, userId, window));
        }
        if ("food_record_count".equals(metricCode)) {
            return BigDecimal.valueOf(countFoodRecords(circle != null ? circle.getId() : null, userId, window));
        }
        if ("habit_checkin_count".equals(metricCode)) {
            return BigDecimal.valueOf(countHabitCheckins(userId, window));
        }
        if ("circle_checkin_count".equals(metricCode) && circle != null && circle.getId() != null) {
            return BigDecimal.valueOf(countCircleCheckins(circle.getId(), userId, window));
        }
        if ("manual_circle_checkin".equals(metricCode)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private long countWeightRecords(Long circleId, Long userId, PeriodWindow window) {
        WeightRecord query = new WeightRecord();
        query.setUserId(userId);
        List<WeightRecord> records = recordService.selectWeightRecordList(query);
        Set<Long> sourceRecordIds = loadCircleSourceRecordIds(circleId, userId, "weight_record");
        return filterWeightRecordsByCircle(filterByWindow(records, window, new DateExtractor<WeightRecord>() {
            @Override
            public Date extract(WeightRecord item) {
                return item != null ? item.getRecordedAt() : null;
            }
        }), circleId, sourceRecordIds).size();
    }

    private BigDecimal calculateWeightLoss(Long circleId, Long userId, PeriodWindow window) {
        WeightRecord query = new WeightRecord();
        query.setUserId(userId);
        List<WeightRecord> records = recordService.selectWeightRecordList(query);
        Set<Long> sourceRecordIds = loadCircleSourceRecordIds(circleId, userId, "weight_record");
        List<WeightRecord> periodRecords = filterWeightRecordsByCircle(filterByWindow(records, window, new DateExtractor<WeightRecord>() {
            @Override
            public Date extract(WeightRecord item) {
                return item != null ? item.getRecordedAt() : null;
            }
        }), circleId, sourceRecordIds);
        if (periodRecords.size() < 2) {
            return BigDecimal.ZERO;
        }
        periodRecords.sort(Comparator
                .comparing(WeightRecord::getRecordedAt, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightRecord::getId, Comparator.nullsLast(Long::compareTo)));
        BigDecimal startWeight = periodRecords.get(0).getWeight();
        BigDecimal endWeight = periodRecords.get(periodRecords.size() - 1).getWeight();
        if (startWeight == null || endWeight == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal diff = startWeight.subtract(endWeight).setScale(2, RoundingMode.HALF_UP);
        return diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO;
    }

    private long sumExerciseMinutes(Long circleId, Long userId, PeriodWindow window) {
        WeightExerciseRecord query = new WeightExerciseRecord();
        query.setUserId(userId);
        List<WeightExerciseRecord> records = exerciseRecordService.selectWeightExerciseRecordList(query);
        long total = 0L;
        Set<Long> sourceRecordIds = loadCircleSourceRecordIds(circleId, userId, "exercise_record");
        for (WeightExerciseRecord record : filterExerciseRecordsByCircle(filterByWindow(records, window, new DateExtractor<WeightExerciseRecord>() {
            @Override
            public Date extract(WeightExerciseRecord item) {
                return item != null ? item.getRecordedAt() : null;
            }
        }), circleId, sourceRecordIds)) {
            total += record.getDurationMinutes() != null ? record.getDurationMinutes() : 0L;
        }
        return total;
    }

    private long countExerciseRecords(Long circleId, Long userId, PeriodWindow window) {
        WeightExerciseRecord query = new WeightExerciseRecord();
        query.setUserId(userId);
        List<WeightExerciseRecord> records = exerciseRecordService.selectWeightExerciseRecordList(query);
        Set<Long> sourceRecordIds = loadCircleSourceRecordIds(circleId, userId, "exercise_record");
        return filterExerciseRecordsByCircle(filterByWindow(records, window, new DateExtractor<WeightExerciseRecord>() {
            @Override
            public Date extract(WeightExerciseRecord item) {
                return item != null ? item.getRecordedAt() : null;
            }
        }), circleId, sourceRecordIds).size();
    }

    private long countFoodRecords(Long circleId, Long userId, PeriodWindow window) {
        WeightFoodRecord query = new WeightFoodRecord();
        query.setUserId(userId);
        List<WeightFoodRecord> records = foodRecordService.selectWeightFoodRecordList(query);
        Set<Long> sourceRecordIds = loadCircleSourceRecordIds(circleId, userId, "food_record");
        return filterFoodRecordsByCircle(filterByWindow(records, window, new DateExtractor<WeightFoodRecord>() {
            @Override
            public Date extract(WeightFoodRecord item) {
                return item != null ? item.getRecordedAt() : null;
            }
        }), circleId, sourceRecordIds).size();
    }

    private long countHabitCheckins(Long userId, PeriodWindow window) {
        WeightHabitCheckin query = new WeightHabitCheckin();
        query.setUserId(userId);
        List<WeightHabitCheckin> checkins = habitCheckinService.selectWeightHabitCheckinList(query);
        return filterByWindow(checkins, window, new DateExtractor<WeightHabitCheckin>() {
            @Override
            public Date extract(WeightHabitCheckin item) {
                return item != null ? item.getCheckedAt() : null;
            }
        }).size();
    }

    private long countCircleCheckins(Long circleId, Long userId, PeriodWindow window) {
        List<WeightCircleFeed> feeds = circleFeedService.selectCircleVisibleFeeds(circleId);
        if (feeds == null || feeds.isEmpty()) {
            return 0L;
        }
        long count = 0L;
        for (WeightCircleFeed feed : feeds) {
            if (feed == null || !userId.equals(feed.getUserId()) || feed.getCreateTime() == null) {
                continue;
            }
            if (window.contains(toLocalDate(feed.getCreateTime()))) {
                count++;
            }
        }
        return count;
    }

    private List<WeightRecord> filterWeightRecordsByCircle(List<WeightRecord> records,
                                                           Long circleId,
                                                           Set<Long> sourceRecordIds) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        if (circleId == null) {
            return records;
        }
        List<WeightRecord> matched = new ArrayList<>();
        for (WeightRecord record : records) {
            if (record != null && recordBelongsToCircle(record.getId(), record.getSyncToCircles(), circleId, sourceRecordIds)) {
                matched.add(record);
            }
        }
        return matched;
    }

    private List<WeightExerciseRecord> filterExerciseRecordsByCircle(List<WeightExerciseRecord> records,
                                                                     Long circleId,
                                                                     Set<Long> sourceRecordIds) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        if (circleId == null) {
            return records;
        }
        List<WeightExerciseRecord> matched = new ArrayList<>();
        for (WeightExerciseRecord record : records) {
            if (record != null && recordBelongsToCircle(record.getId(), record.getSyncToCircles(), circleId, sourceRecordIds)) {
                matched.add(record);
            }
        }
        return matched;
    }

    private List<WeightFoodRecord> filterFoodRecordsByCircle(List<WeightFoodRecord> records,
                                                             Long circleId,
                                                             Set<Long> sourceRecordIds) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        if (circleId == null) {
            return records;
        }
        List<WeightFoodRecord> matched = new ArrayList<>();
        for (WeightFoodRecord record : records) {
            if (record != null && recordBelongsToCircle(record.getId(), record.getSyncToCircles(), circleId, sourceRecordIds)) {
                matched.add(record);
            }
        }
        return matched;
    }

    private boolean recordBelongsToCircle(Long recordId,
                                          String syncToCircles,
                                          Long circleId,
                                          Set<Long> sourceRecordIds) {
        if (circleId == null) {
            return true;
        }
        if (includesCircleId(syncToCircles, circleId)) {
            return true;
        }
        return recordId != null && sourceRecordIds != null && sourceRecordIds.contains(recordId);
    }

    private Set<Long> loadCircleSourceRecordIds(Long circleId, Long userId, String sourceRecordType) {
        if (circleId == null || userId == null || StringUtils.isBlank(sourceRecordType)) {
            return Collections.emptySet();
        }
        List<WeightCircleFeed> feeds = circleFeedService.selectCircleVisibleFeeds(circleId);
        if (feeds == null || feeds.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> sourceRecordIds = new LinkedHashSet<>();
        for (WeightCircleFeed feed : feeds) {
            if (feed == null
                    || !userId.equals(feed.getUserId())
                    || feed.getSourceRecordId() == null
                    || !sourceRecordType.equals(StringUtils.defaultString(feed.getSourceRecordType()))) {
                continue;
            }
            sourceRecordIds.add(feed.getSourceRecordId());
        }
        return sourceRecordIds;
    }

    private boolean includesCircleId(String rawCircleIds, Long circleId) {
        if (StringUtils.isBlank(rawCircleIds) || circleId == null) {
            return false;
        }
        try {
            List<Long> ids = JSON.parseArray(rawCircleIds, Long.class);
            return ids != null && ids.contains(circleId);
        } catch (Exception ignored) {
            return includesCircleIdFallback(rawCircleIds, circleId);
        }
    }

    private boolean includesCircleIdFallback(String rawCircleIds, Long circleId) {
        String[] parts = StringUtils.split(StringUtils.replaceChars(rawCircleIds, "[]\"'", ""), ',');
        if (parts == null) {
            return false;
        }
        for (String part : parts) {
            try {
                if (circleId.equals(Long.valueOf(StringUtils.trimToEmpty(part)))) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
                // Ignore malformed legacy fragments.
            }
        }
        return false;
    }

    private void syncRankingSnapshot(WeightCircle circle, List<ApiCircleResponse.CircleRankingItem> ranking) {
        if (circle == null || circle.getId() == null) {
            return;
        }
        WeightCircleRanking query = new WeightCircleRanking();
        query.setCircleId(circle.getId());
        query.setPeriod(RANKING_PERIOD_WEEKLY);
        List<WeightCircleRanking> existing = circleRankingService.selectWeightCircleRankingList(query);
        Map<Long, WeightCircleRanking> existingMap = new LinkedHashMap<>();
        if (existing != null) {
            for (WeightCircleRanking item : existing) {
                if (item != null && item.getUserId() != null) {
                    existingMap.put(item.getUserId(), item);
                }
            }
        }
        Set<Long> activeUserIds = new LinkedHashSet<>();
        Date now = DateUtils.getNowDate();
        for (ApiCircleResponse.CircleRankingItem item : ranking) {
            if (item.getUserId() == null) {
                continue;
            }
            activeUserIds.add(item.getUserId());
            WeightCircleRanking snapshot = existingMap.get(item.getUserId());
            if (snapshot == null) {
                snapshot = new WeightCircleRanking();
                snapshot.setCircleId(circle.getId());
                snapshot.setUserId(item.getUserId());
                snapshot.setPeriod(RANKING_PERIOD_WEEKLY);
                snapshot.setRankNum(item.getRankNum());
                snapshot.setScore(item.getScore());
                snapshot.setUpdateTime(now);
                circleRankingService.insertWeightCircleRanking(snapshot);
            } else {
                snapshot.setRankNum(item.getRankNum());
                snapshot.setScore(item.getScore());
                snapshot.setUpdateTime(now);
                circleRankingService.updateWeightCircleRanking(snapshot);
            }
        }
        for (WeightCircleRanking snapshot : existingMap.values()) {
            if (snapshot != null && snapshot.getUserId() != null && !activeUserIds.contains(snapshot.getUserId())) {
                circleRankingService.deleteWeightCircleRankingById(snapshot.getId());
            }
        }
    }

    private BigDecimal buildRankingScore(ApiCircleResponse.CircleRankingItem item) {
        long completed = item.getCompletedTaskCount() != null ? item.getCompletedTaskCount() : 0L;
        long checkins = item.getCircleCheckins() != null ? item.getCircleCheckins() : 0L;
        long streak = item.getStreakDays() != null ? item.getStreakDays() : 0L;
        return BigDecimal.valueOf(checkins * 100L + completed * 10L + streak).setScale(2, RoundingMode.HALF_UP);
    }

    private List<WeightCircleMember> loadActiveMembers(Long circleId) {
        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(circleId);
        query.setStatus(ACTIVE_STATUS);
        List<WeightCircleMember> members = circleMemberService.selectWeightCircleMemberList(query);
        return members != null ? members : Collections.<WeightCircleMember>emptyList();
    }

    private WeightCircleGoal findGoalById(Long circleId, Long goalId) {
        if (goalId == null) {
            return null;
        }
        for (WeightCircleGoal goal : loadEnabledGoals(circleId)) {
            if (goalId.equals(goal.getId())) {
                return goal;
            }
        }
        return null;
    }

    private boolean supportsMiniTask(WeightCircleGoal goal) {
        if (goal == null) {
            return false;
        }
        String verificationType = StringUtils.defaultString(goal.getVerificationType());
        if (!VERIFICATION_AUTO.equals(verificationType) && !VERIFICATION_MANUAL.equals(verificationType)) {
            return false;
        }
        return StringUtils.isNotBlank(resolveMetricCode(goal));
    }

    private LocalDate resolveReferenceDate(WeightCircle circle) {
        if (isCircleExpired(circle) && circle != null && circle.getEndDate() != null) {
            return toLocalDate(circle.getEndDate());
        }
        return LocalDate.now();
    }

    private PeriodWindow buildGoalWindow(WeightCircleGoal goal,
                                         WeightCircle circle,
                                         WeightCircleMember membership,
                                         LocalDate referenceDate) {
        String period = goal != null ? StringUtils.defaultIfBlank(goal.getPeriod(), "0") : "0";
        if ("1".equals(period)) {
            LocalDate start = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = referenceDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return clampWindow(new PeriodWindow("W:" + start, start, end), circle, membership, referenceDate);
        }
        if ("2".equals(period)) {
            YearMonth month = YearMonth.from(referenceDate);
            return clampWindow(new PeriodWindow("M:" + month, month.atDay(1), month.atEndOfMonth()), circle, membership, referenceDate);
        }
        if ("3".equals(period)) {
            LocalDate start = circle != null && circle.getStartDate() != null ? toLocalDate(circle.getStartDate()) : referenceDate;
            if (membership != null && membership.getJoinedAt() != null) {
                LocalDate joinedAt = toLocalDate(membership.getJoinedAt());
                if (joinedAt.isAfter(start)) {
                    start = joinedAt;
                }
            }
            return clampWindow(new PeriodWindow("T:" + start, start, referenceDate), circle, membership, referenceDate);
        }
        return clampWindow(new PeriodWindow("D:" + referenceDate, referenceDate, referenceDate), circle, membership, referenceDate);
    }

    private PeriodWindow buildRankingWindow(WeightCircle circle, WeightCircleMember membership, LocalDate referenceDate) {
        LocalDate start = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = referenceDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return clampWindow(new PeriodWindow("W:" + start, start, end), circle, membership, referenceDate);
    }

    private PeriodWindow clampWindow(PeriodWindow window,
                                     WeightCircle circle,
                                     WeightCircleMember membership,
                                     LocalDate referenceDate) {
        if (window == null) {
            return null;
        }
        LocalDate start = window.start;
        LocalDate end = window.end;
        if (circle != null && circle.getStartDate() != null) {
            LocalDate circleStart = toLocalDate(circle.getStartDate());
            if (circleStart.isAfter(start)) {
                start = circleStart;
            }
        }
        if (membership != null && membership.getJoinedAt() != null) {
            LocalDate joinedAt = toLocalDate(membership.getJoinedAt());
            if (joinedAt.isAfter(start)) {
                start = joinedAt;
            }
        }
        if (circle != null && circle.getEndDate() != null) {
            LocalDate circleEnd = toLocalDate(circle.getEndDate());
            if (circleEnd.isBefore(end)) {
                end = circleEnd;
            }
        }
        if (referenceDate != null && referenceDate.isBefore(end)) {
            end = referenceDate;
        }
        return new PeriodWindow(window.key, start, end);
    }

    private String buildGoalFallbackDescription(WeightCircleGoal goal) {
        if (goal == null) {
            return "按当前圈子目标执行";
        }
        return safeTargetValue(goal.getTargetValue()).stripTrailingZeros().toPlainString()
                + StringUtils.defaultString(goal.getTargetUnit())
                + " / "
                + resolvePeriodLabel(goal.getPeriod());
    }

    private String buildPenaltyRuleText(String penaltyRule) {
        if ("1".equals(penaltyRule)) {
            return "未完成会收到提醒通知";
        }
        if ("2".equals(penaltyRule)) {
            return "未完成会进入待处理扣押金名单";
        }
        if ("3".equals(penaltyRule)) {
            return "未完成会进入待处理移出名单";
        }
        return "当前未配置额外处罚";
    }

    private BigDecimal safeTargetValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ONE;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZONE_ID).toLocalDate();
    }

    private String formatDateOnly(Date date) {
        return date != null ? DateUtils.parseDateToStr("yyyy-MM-dd", date) : null;
    }

    private String formatDateTime(Date date) {
        return date != null ? DateUtils.parseDateToStr("yyyy-MM-dd HH:mm", date) : null;
    }

    private LocalDate parseJoinedAt(String joinedAt) {
        if (StringUtils.isBlank(joinedAt)) {
            return null;
        }
        try {
            return LocalDate.parse(joinedAt);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean containsAny(String text, String... needles) {
        if (StringUtils.isBlank(text) || needles == null) {
            return false;
        }
        for (String needle : needles) {
            if (needle != null && text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private <T> List<T> filterByWindow(Collection<T> source, PeriodWindow window, DateExtractor<T> extractor) {
        if (source == null || source.isEmpty() || window == null) {
            return Collections.emptyList();
        }
        List<T> items = new ArrayList<>();
        for (T item : source) {
            Date date = extractor.extract(item);
            if (date != null && window.contains(toLocalDate(date))) {
                items.add(item);
            }
        }
        return items;
    }

    private interface DateExtractor<T> {
        Date extract(T item);
    }

    private static class PeriodWindow {

        private final String key;

        private final LocalDate start;

        private final LocalDate end;

        private PeriodWindow(String key, LocalDate start, LocalDate end) {
            this.key = key;
            this.start = start;
            this.end = end;
        }

        private boolean contains(LocalDate day) {
            return day != null && (day.isEqual(start) || day.isAfter(start)) && (day.isEqual(end) || day.isBefore(end));
        }
    }
}
