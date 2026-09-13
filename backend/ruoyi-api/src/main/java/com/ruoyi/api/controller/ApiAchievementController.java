package com.ruoyi.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.api.dto.ApiAchievementResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.weight.domain.WeightAchievement;
import com.ruoyi.weight.domain.WeightCircleMember;
import com.ruoyi.weight.domain.WeightHabit;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightAchievementService;
import com.ruoyi.weight.service.IWeightCircleMemberService;
import com.ruoyi.weight.service.IWeightHabitService;
import com.ruoyi.weight.service.IWeightUserService;

/**
 * 移动端 - 成就徽章 API。
 */
@RestController
@RequestMapping("/api/achievements")
public class ApiAchievementController extends ApiBaseController {

    @Autowired
    private IWeightAchievementService weightAchievementService;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private IWeightHabitService habitService;

    @Autowired
    private IWeightCircleMemberService circleMemberService;

    @GetMapping
    public AjaxResult list(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        if (weightUser == null) {
            return AjaxResult.error("用户不存在");
        }

        ensureDefaultAchievements(weightUser);

        WeightAchievement query = new WeightAchievement();
        query.setUserId(userId);
        List<WeightAchievement> achievements = weightAchievementService.selectWeightAchievementList(query);
        if (achievements == null) {
            achievements = Collections.emptyList();
        }

        achievements.sort(this::compareAchievementForMobile);
        List<ApiAchievementResponse> response = achievements.stream()
            .map(ApiAchievementResponse::fromWeightAchievement)
            .collect(Collectors.toList());
        return successWithData(response);
    }

    private void ensureDefaultAchievements(WeightUser weightUser) {
        Long userId = weightUser.getUserId();
        if (userId == null) {
            return;
        }

        WeightAchievement query = new WeightAchievement();
        query.setUserId(userId);
        List<WeightAchievement> existingAchievements = weightAchievementService.selectWeightAchievementList(query);
        Map<String, WeightAchievement> existingByType = new HashMap<>();
        if (existingAchievements != null) {
            for (WeightAchievement achievement : existingAchievements) {
                if (StringUtils.isNotBlank(achievement.getBadgeType())) {
                    existingByType.put(achievement.getBadgeType(), achievement);
                }
            }
        }

        List<AchievementDefinition> definitions = buildDefaultDefinitions(
            weightUser,
            loadActiveHabitCount(userId),
            loadJoinedCircleCount(userId)
        );
        for (AchievementDefinition definition : definitions) {
            WeightAchievement existing = existingByType.get(definition.getBadgeType());
            if (existing == null) {
                insertDefaultAchievement(userId, definition);
            } else {
                updateDefaultAchievement(existing, definition);
            }
        }
    }

    private List<AchievementDefinition> buildDefaultDefinitions(WeightUser user, int activeHabits, int joinedCircles) {
        long points = user.getAchievementPoints() == null ? 0L : user.getAchievementPoints();
        long streakDays = user.getStreakDays() == null ? 0L : user.getStreakDays();
        long dailyWaterTarget = user.getDailyWaterTarget() == null ? 0L : user.getDailyWaterTarget();
        boolean hasTargetWeight = user.getTargetWeight() != null && BigDecimal.ZERO.compareTo(user.getTargetWeight()) < 0;
        boolean hasCurrentWeight = user.getCurrentWeight() != null && BigDecimal.ZERO.compareTo(user.getCurrentWeight()) < 0;

        List<AchievementDefinition> definitions = new ArrayList<>();
        definitions.add(new AchievementDefinition(
            "first-record", "新手起步", "🌟", "完成首次真实记录",
            points >= 1L, points >= 1L ? 100 : 0
        ));
        definitions.add(new AchievementDefinition(
            "streak-week", "连续打卡", "🔥", "连续 7 天保持记录",
            streakDays >= 7L, percentage(streakDays, 7)
        ));
        definitions.add(new AchievementDefinition(
            "water-plan", "补水计划", "💧", "设置每日饮水目标",
            dailyWaterTarget > 0L, dailyWaterTarget > 0L ? 100 : 0
        ));
        definitions.add(new AchievementDefinition(
            "weight-goal", "目标明确", "⚖️", "补齐当前体重和目标体重",
            hasTargetWeight && hasCurrentWeight,
            (hasTargetWeight ? 50 : 0) + (hasCurrentWeight ? 50 : 0)
        ));
        definitions.add(new AchievementDefinition(
            "habit-builder", "习惯养成", "✅", "启用 3 个习惯",
            activeHabits >= 3, percentage(activeHabits, 3)
        ));
        definitions.add(new AchievementDefinition(
            "circle-companion", "圈子同行", "⭕", "加入至少 1 个圈子",
            joinedCircles >= 1, joinedCircles >= 1 ? 100 : 0
        ));
        return definitions;
    }

    private void insertDefaultAchievement(Long userId, AchievementDefinition definition) {
        WeightAchievement achievement = new WeightAchievement();
        achievement.setUserId(userId);
        achievement.setBadgeType(definition.getBadgeType());
        achievement.setBadgeName(definition.getBadgeName());
        achievement.setBadgeIcon(definition.getBadgeIcon());
        achievement.setBadgeDescription(definition.getBadgeDescription());
        achievement.setProgress(toProgress(definition.getProgress()));
        achievement.setIsUnlocked(definition.isUnlocked() ? 1 : 0);
        if (definition.isUnlocked()) {
            achievement.setUnlockedAt(new Date());
        }
        weightAchievementService.insertWeightAchievement(achievement);
    }

    private void updateDefaultAchievement(WeightAchievement existing, AchievementDefinition definition) {
        boolean unlocked = isUnlocked(existing) || definition.isUnlocked();
        int progress = Math.max(normalizeProgress(existing).intValue(), definition.getProgress());
        if (unlocked) {
            progress = 100;
        }

        if (StringUtils.isBlank(existing.getBadgeName())) {
            existing.setBadgeName(definition.getBadgeName());
        }
        if (StringUtils.isBlank(existing.getBadgeIcon())) {
            existing.setBadgeIcon(definition.getBadgeIcon());
        }
        if (StringUtils.isBlank(existing.getBadgeDescription())) {
            existing.setBadgeDescription(definition.getBadgeDescription());
        }
        existing.setProgress(toProgress(progress));
        existing.setIsUnlocked(unlocked ? 1 : 0);
        if (unlocked && existing.getUnlockedAt() == null) {
            existing.setUnlockedAt(new Date());
        }
        weightAchievementService.updateWeightAchievement(existing);
    }

    private int loadActiveHabitCount(Long userId) {
        WeightHabit query = new WeightHabit();
        query.setUserId(userId);
        List<WeightHabit> habits = habitService.selectWeightHabitList(query);
        if (habits == null || habits.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (WeightHabit habit : habits) {
            if (habit != null
                && !"1".equals(StringUtils.defaultString(habit.getStatus(), "0"))
                && !Integer.valueOf(0).equals(habit.getIsActive())) {
                count += 1;
            }
        }
        return count;
    }

    private int loadJoinedCircleCount(Long userId) {
        WeightCircleMember query = new WeightCircleMember();
        query.setUserId(userId);
        query.setStatus("0");
        List<WeightCircleMember> members = circleMemberService.selectWeightCircleMemberList(query);
        return members == null ? 0 : members.size();
    }

    private int compareAchievementForMobile(WeightAchievement left, WeightAchievement right) {
        int unlockedCompare = Boolean.compare(isUnlocked(right), isUnlocked(left));
        if (unlockedCompare != 0) {
            return unlockedCompare;
        }

        int progressCompare = normalizeProgress(right).compareTo(normalizeProgress(left));
        if (progressCompare != 0) {
            return progressCompare;
        }

        return normalizeDate(right).compareTo(normalizeDate(left));
    }

    private boolean isUnlocked(WeightAchievement achievement) {
        return achievement != null && Integer.valueOf(1).equals(achievement.getIsUnlocked());
    }

    private BigDecimal normalizeProgress(WeightAchievement achievement) {
        if (achievement == null || achievement.getProgress() == null) {
            return BigDecimal.ZERO;
        }
        return achievement.getProgress();
    }

    private BigDecimal toProgress(int progress) {
        return BigDecimal.valueOf(Math.max(0, Math.min(100, progress)));
    }

    private int percentage(long current, long target) {
        if (target <= 0L) {
            return 0;
        }
        return (int) Math.max(0L, Math.min(100L, Math.round((current * 100.0D) / target)));
    }

    private Date normalizeDate(WeightAchievement achievement) {
        if (achievement == null || achievement.getUnlockedAt() == null) {
            return new Date(0L);
        }
        return achievement.getUnlockedAt();
    }

    private static class AchievementDefinition {
        private final String badgeType;
        private final String badgeName;
        private final String badgeIcon;
        private final String badgeDescription;
        private final boolean unlocked;
        private final int progress;

        AchievementDefinition(String badgeType, String badgeName, String badgeIcon, String badgeDescription,
            boolean unlocked, int progress) {
            this.badgeType = badgeType;
            this.badgeName = badgeName;
            this.badgeIcon = badgeIcon;
            this.badgeDescription = badgeDescription;
            this.unlocked = unlocked;
            this.progress = progress;
        }

        public String getBadgeType() {
            return badgeType;
        }

        public String getBadgeName() {
            return badgeName;
        }

        public String getBadgeIcon() {
            return badgeIcon;
        }

        public String getBadgeDescription() {
            return badgeDescription;
        }

        public boolean isUnlocked() {
            return unlocked;
        }

        public int getProgress() {
            return progress;
        }
    }
}
