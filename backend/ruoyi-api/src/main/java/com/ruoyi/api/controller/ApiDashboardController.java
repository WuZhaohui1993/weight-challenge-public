package com.ruoyi.api.controller;

import com.ruoyi.api.dto.ApiDashboardResponse;
import com.ruoyi.api.service.CircleReadModelService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightExerciseRecord;
import com.ruoyi.weight.domain.WeightFoodRecord;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.domain.WeightWaterRecord;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightCircleService;
import com.ruoyi.weight.service.IWeightExerciseRecordService;
import com.ruoyi.weight.service.IWeightFoodRecordService;
import com.ruoyi.weight.service.IWeightNotificationService;
import com.ruoyi.weight.service.IWeightRecordService;
import com.ruoyi.weight.service.IWeightUserService;
import com.ruoyi.weight.service.IWeightWaterRecordService;
import com.ruoyi.weight.support.WeightUserGoalCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 移动端首页 dashboard API
 */
@RestController
@RequestMapping("/api/dashboard")
public class ApiDashboardController extends ApiBaseController {

    private static final Long DEFAULT_WATER_TARGET = 8L;
    private static final DateTimeFormatter TREND_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Autowired
    private IWeightUserService weightUserService;

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
    private IWeightCircleMemberService memberService;

    @Autowired
    private IWeightNotificationService notificationService;

    @Autowired
    private CircleReadModelService circleReadModelService;

    @GetMapping
    public AjaxResult dashboard(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        if (weightUser == null) {
            return AjaxResult.error("用户不存在");
        }

        List<WeightRecord> weightRecords = loadWeightRecords(userId);
        List<WeightFoodRecord> foodRecords = loadFoodRecords(userId);
        List<WeightExerciseRecord> exerciseRecords = loadExerciseRecords(userId);
        List<WeightWaterRecord> waterRecords = loadWaterRecords(userId);

        ApiDashboardResponse response = new ApiDashboardResponse();
        response.setSummary(buildSummary(weightUser, weightRecords, foodRecords, exerciseRecords, waterRecords));
        response.setWeightTrend(buildWeightTrend(weightRecords));
        response.setMainCircle(buildMainCircle(userId, weightUser.getMainCircleId()));
        response.setUnreadNotificationCount(countUnreadNotifications(userId));

        return successWithData(response);
    }

    private Long countUnreadNotifications(Long userId) {
        if (userId == null) {
            return 0L;
        }

        WeightNotification query = new WeightNotification();
        query.setUserId(userId);
        query.setIsRead(0);
        List<WeightNotification> notifications = notificationService.selectWeightNotificationList(query);
        return notifications == null ? 0L : (long) notifications.size();
    }

    private ApiDashboardResponse.Summary buildSummary(WeightUser weightUser,
                                                      List<WeightRecord> weightRecords,
                                                      List<WeightFoodRecord> foodRecords,
                                                      List<WeightExerciseRecord> exerciseRecords,
                                                      List<WeightWaterRecord> waterRecords) {
        ApiDashboardResponse.Summary summary = new ApiDashboardResponse.Summary();

        WeightRecord latestWeightRecord = weightRecords.isEmpty() ? null : weightRecords.get(0);
        BigDecimal currentWeight = latestWeightRecord != null ? latestWeightRecord.getWeight() : weightUser.getCurrentWeight();
        summary.setCurrentWeight(currentWeight);
        summary.setBmi(calculateBmi(currentWeight, weightUser.getHeight()));
        summary.setTargetWeight(weightUser.getTargetWeight());
        summary.setTargetCompletionDate(weightUser.getTargetCompletionDate());
        long dailyCalorieDeficitTarget = WeightUserGoalCalculator.calculateDailyCalorieDeficit(
                currentWeight,
                weightUser.getTargetWeight(),
                weightUser.getTargetCompletionDate()
        );
        summary.setDailyCalorieDeficitTarget(dailyCalorieDeficitTarget);

        LocalDate today = LocalDate.now();
        long calorieIntake = foodRecords.stream()
                .filter(record -> isSameDay(record.getRecordedAt(), today))
                .map(WeightFoodRecord::getCalories)
                .filter(value -> value != null)
                .mapToLong(Long::longValue)
                .sum();
        long exerciseCalories = exerciseRecords.stream()
                .filter(record -> isSameDay(record.getRecordedAt(), today))
                .map(WeightExerciseRecord::getCaloriesBurned)
                .filter(value -> value != null)
                .mapToLong(Long::longValue)
                .sum();
        long exerciseMinutes = exerciseRecords.stream()
                .filter(record -> isSameDay(record.getRecordedAt(), today))
                .map(WeightExerciseRecord::getDurationMinutes)
                .filter(value -> value != null)
                .mapToLong(Long::longValue)
                .sum();
        long waterCups = waterRecords.stream()
                .filter(record -> isSameDay(record.getRecordedAt(), today))
                .map(WeightWaterRecord::getCups)
                .filter(value -> value != null)
                .mapToLong(Long::longValue)
                .sum();

        long calorieTarget = WeightUserGoalCalculator.calculateDailyCalorieTarget(
                currentWeight,
                weightUser.getHeight(),
                weightUser.getBirthday(),
                weightUser.getGender(),
                weightUser.getTargetWeight(),
                weightUser.getTargetCompletionDate()
        );
        long waterTarget = weightUser.getDailyWaterTarget() != null ? weightUser.getDailyWaterTarget() : DEFAULT_WATER_TARGET;

        summary.setCalorieIntake(calorieIntake);
        summary.setCalorieTarget(calorieTarget);
        summary.setCalorieRemaining(calorieTarget > 0 ? Math.max(0L, calorieTarget - calorieIntake) : 0L);
        summary.setExerciseCalories(exerciseCalories);
        summary.setExerciseMinutes(exerciseMinutes);
        summary.setWaterCups(waterCups);
        summary.setWaterTarget(waterTarget);

        return summary;
    }

    private BigDecimal calculateBmi(BigDecimal currentWeight, BigDecimal height) {
        if (currentWeight == null || height == null || height.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal heightMeter = height.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal heightSquare = heightMeter.multiply(heightMeter);
        return currentWeight.divide(heightSquare, 1, RoundingMode.HALF_UP);
    }

    private List<ApiDashboardResponse.WeightTrendItem> buildWeightTrend(List<WeightRecord> weightRecords) {
        Map<LocalDate, WeightRecord> dailyLastRecordMap = new TreeMap<>();
        weightRecords.stream()
                .filter(record -> record.getRecordedAt() != null)
                .sorted(Comparator.comparing(WeightRecord::getRecordedAt, Comparator.nullsLast(Date::compareTo))
                        .thenComparing(WeightRecord::getId, Comparator.nullsLast(Long::compareTo)))
                .forEach(record -> dailyLastRecordMap.put(toLocalDate(record.getRecordedAt()), record));

        List<WeightRecord> dailyRecords = new ArrayList<>(dailyLastRecordMap.values());
        int startIndex = Math.max(0, dailyRecords.size() - 7);
        List<WeightRecord> recentRecords = dailyRecords.subList(startIndex, dailyRecords.size());

        List<ApiDashboardResponse.WeightTrendItem> trend = new ArrayList<>();
        for (WeightRecord record : recentRecords) {
            ApiDashboardResponse.WeightTrendItem item = new ApiDashboardResponse.WeightTrendItem();
            item.setDate(formatTrendDate(record.getRecordedAt()));
            item.setWeight(record.getWeight());
            trend.add(item);
        }
        return trend;
    }

    private ApiDashboardResponse.MainCircle buildMainCircle(Long userId, Long mainCircleId) {
        if (userId == null || mainCircleId == null) {
            return null;
        }

        WeightCircleMember membershipQuery = new WeightCircleMember();
        membershipQuery.setCircleId(mainCircleId);
        membershipQuery.setUserId(userId);
        membershipQuery.setStatus("0");
        List<WeightCircleMember> currentUserMemberships = memberService.selectWeightCircleMemberList(membershipQuery);
        if (currentUserMemberships == null || currentUserMemberships.isEmpty()) {
            return null;
        }

        WeightCircle circle = circleService.selectWeightCircleById(mainCircleId);
        if (circle == null || !"0".equals(circle.getStatus()) || circleReadModelService.isCircleExpired(circle)) {
            return null;
        }

        WeightCircleMember query = new WeightCircleMember();
        query.setCircleId(mainCircleId);
        query.setStatus("0");
        List<WeightCircleMember> members = memberService.selectWeightCircleMemberList(query);
        members.sort(Comparator.comparing(WeightCircleMember::getIsPinned, Comparator.nullsLast(Integer::compareTo)).reversed()
                .thenComparing(WeightCircleMember::getJoinedAt, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightCircleMember::getId, Comparator.nullsLast(Long::compareTo)));

        ApiDashboardResponse.MainCircle mainCircle = new ApiDashboardResponse.MainCircle();
        mainCircle.setId(circle.getId());
        mainCircle.setName(circle.getName());
        mainCircle.setMembers(buildCircleMembers(members));
        mainCircle.setRanking(buildCircleRanking(circle));
        return mainCircle;
    }

    private List<ApiDashboardResponse.CircleMemberItem> buildCircleMembers(List<WeightCircleMember> members) {
        List<ApiDashboardResponse.CircleMemberItem> items = new ArrayList<>();
        for (WeightCircleMember member : members) {
            WeightUser user = weightUserService.selectWeightUserByUserId(member.getUserId());
            ApiDashboardResponse.CircleMemberItem item = new ApiDashboardResponse.CircleMemberItem();
            item.setUserId(member.getUserId());
            item.setNickname(user != null ? user.getNickname() : null);
            item.setAvatar(user != null ? user.getAvatar() : null);
            item.setRole(member.getRole());
            item.setStreakDays(member.getStreakDays());
            item.setWeightChange(member.getWeightChange());
            items.add(item);
        }
        return items;
    }

    private List<ApiDashboardResponse.CircleRankingItem> buildCircleRanking(WeightCircle circle) {
        List<com.ruoyi.api.dto.ApiCircleResponse.CircleRankingItem> rankingSource = circleReadModelService.buildRanking(circle);
        List<ApiDashboardResponse.CircleRankingItem> ranking = new ArrayList<>();
        for (com.ruoyi.api.dto.ApiCircleResponse.CircleRankingItem source : rankingSource) {
            ApiDashboardResponse.CircleRankingItem item = new ApiDashboardResponse.CircleRankingItem();
            item.setRank(source.getRankNum() != null ? source.getRankNum().intValue() : null);
            item.setUserId(source.getUserId());
            item.setNickname(source.getNickname());
            item.setAvatar(source.getAvatar());
            item.setCompletedTaskCount(source.getCompletedTaskCount());
            item.setCircleCheckins(source.getCircleCheckins());
            ranking.add(item);
        }
        return ranking;
    }

    private List<WeightRecord> loadWeightRecords(Long userId) {
        WeightRecord query = new WeightRecord();
        query.setUserId(userId);
        List<WeightRecord> records = weightRecordService.selectWeightRecordList(query);
        records.sort(Comparator.comparing(WeightRecord::getRecordedAt, Comparator.nullsLast(Date::compareTo))
                .thenComparing(WeightRecord::getId, Comparator.nullsLast(Long::compareTo))
                .reversed());
        return records;
    }

    private List<WeightFoodRecord> loadFoodRecords(Long userId) {
        WeightFoodRecord query = new WeightFoodRecord();
        query.setUserId(userId);
        return foodRecordService.selectWeightFoodRecordList(query);
    }

    private List<WeightExerciseRecord> loadExerciseRecords(Long userId) {
        WeightExerciseRecord query = new WeightExerciseRecord();
        query.setUserId(userId);
        return exerciseRecordService.selectWeightExerciseRecordList(query);
    }

    private List<WeightWaterRecord> loadWaterRecords(Long userId) {
        WeightWaterRecord query = new WeightWaterRecord();
        query.setUserId(userId);
        return waterRecordService.selectWeightWaterRecordList(query);
    }

    private boolean isSameDay(Date date, LocalDate expectedDate) {
        if (date == null) {
            return false;
        }
        return toLocalDate(date).equals(expectedDate);
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String formatTrendDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(TREND_DATE_FORMATTER);
    }
}
