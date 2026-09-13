package com.ruoyi.api.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.ruoyi.api.dto.ApiHabitRequest;
import com.ruoyi.api.dto.ApiHabitResponse;
import com.ruoyi.api.dto.ApiPageResponse;
import com.ruoyi.api.service.WechatContentSecurityService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.weight.domain.WeightHabit;
import com.ruoyi.weight.domain.WeightHabitCheckin;
import com.ruoyi.weight.service.IWeightHabitCheckinService;
import com.ruoyi.weight.service.IWeightHabitService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 移动端 - 习惯相关 API
 */
@RestController
@RequestMapping("/api/habits")
public class ApiHabitController extends ApiBaseController {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String DATE_PARAM_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm";
    @Autowired
    private IWeightHabitService habitService;

    @Autowired
    private IWeightHabitCheckinService habitCheckinService;

    @Autowired
    private WechatContentSecurityService contentSecurityService;

    @GetMapping
    public AjaxResult habits(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        List<WeightHabit> habits = loadUserHabits(userId);
        return successWithData(buildHabitItems(habits, loadUserCheckins(userId)));
    }

    @GetMapping("/today")
    public AjaxResult todayHabits(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        LocalDate today = LocalDate.now();
        List<WeightHabit> activeHabits = new ArrayList<>();
        for (WeightHabit habit : loadUserHabits(userId)) {
            if (shouldCompleteOnDay(habit, today)) {
                activeHabits.add(habit);
            }
        }
        return successWithData(buildHabitItems(activeHabits, loadUserCheckins(userId)));
    }

    @GetMapping("/stats")
    public AjaxResult habitStats(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        List<WeightHabit> habits = loadUserHabits(userId);
        return successWithData(buildHabitStats(habits, loadUserCheckins(userId)));
    }

    @GetMapping("/checkins")
    public AjaxResult habitCheckins(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        startPage();
        WeightHabitCheckin query = new WeightHabitCheckin();
        query.setUserId(userId);
        applyCheckedAtRange(request, query);
        List<WeightHabitCheckin> checkins = habitCheckinService.selectWeightHabitCheckinList(query);
        PageInfo<WeightHabitCheckin> pageInfo = new PageInfo<>(checkins != null ? checkins : Collections.<WeightHabitCheckin>emptyList());
        ApiPageResponse<ApiHabitResponse.HabitCheckinItem> response = new ApiPageResponse<>();
        response.setList(buildHabitCheckinItems(pageInfo.getList(), loadUserHabits(userId)));
        response.setTotal(pageInfo.getTotal());
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        return successWithData("查询成功", response);
    }

    @PostMapping
    public AjaxResult createHabit(HttpServletRequest request, @RequestBody ApiHabitRequest body) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (body == null || StringUtils.isBlank(body.getName())) {
            return AjaxResult.error("习惯名称不能为空");
        }

        String name = body.getName().trim();
        AjaxResult securityResult = validateHabitName(userId, name, "habit.create");
        if (securityResult != null) {
            return securityResult;
        }

        WeightHabit habit = new WeightHabit();
        habit.setUserId(userId);
        habit.setName(name);
        habit.setIcon(normalizeIcon(body.getIcon()));
        habit.setPeriod(normalizePeriod(body.getPeriod()));
        habit.setFrequency(normalizeFrequency(body.getFrequency()));
        try {
            habit.setReminderTime(parseReminderTime(body.getReminderTime()));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
        try {
            habit.setRemark(buildReminderRemark(habit.getFrequency(), body.getReminderWeekday(), null));
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
        habit.setIsActive(Boolean.FALSE.equals(body.getActive()) ? 0 : 1);
        habit.setCurrentStreak(0L);
        habit.setLongestStreak(0L);
        habit.setTotalCheckins(0L);
        habit.setStatus(Boolean.FALSE.equals(body.getActive()) ? "1" : "0");
        habitService.insertWeightHabit(habit);

        WeightHabit saved = habitService.selectWeightHabitById(habit.getId());
        return successWithData("创建成功", buildHabitItem(saved, loadUserCheckins(userId)));
    }

    @PutMapping("/{habitId}")
    public AjaxResult updateHabit(HttpServletRequest request,
                                  @PathVariable("habitId") Long habitId,
                                  @RequestBody ApiHabitRequest body) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightHabit habit = requireOwnedHabit(userId, habitId);
        if (habit == null) {
            return AjaxResult.error("习惯不存在");
        }
        if (body == null) {
            return AjaxResult.error("请求参数不能为空");
        }
        if (body.getName() != null && StringUtils.isBlank(body.getName())) {
            return AjaxResult.error("习惯名称不能为空");
        }

        String name = body.getName() != null ? body.getName().trim() : null;
        AjaxResult securityResult = validateHabitName(userId, name, "habit.update");
        if (securityResult != null) {
            return securityResult;
        }

        if (StringUtils.isNotBlank(body.getName())) {
            habit.setName(name);
        }
        if (body.getIcon() != null) {
            habit.setIcon(normalizeIcon(body.getIcon()));
        }
        if (body.getPeriod() != null) {
            habit.setPeriod(normalizePeriod(body.getPeriod()));
        }
        if (body.getFrequency() != null) {
            habit.setFrequency(normalizeFrequency(body.getFrequency()));
        }
        if (body.getReminderTime() != null) {
            try {
                habit.setReminderTime(parseReminderTime(body.getReminderTime()));
            } catch (IllegalArgumentException e) {
                return AjaxResult.error(e.getMessage());
            }
        }
        if (body.getReminderWeekday() != null || body.getFrequency() != null) {
            try {
                habit.setRemark(buildReminderRemark(habit.getFrequency(), body.getReminderWeekday(), habit.getRemark()));
            } catch (IllegalArgumentException e) {
                return AjaxResult.error(e.getMessage());
            }
        }
        if (body.getActive() != null) {
            habit.setIsActive(body.getActive() ? 1 : 0);
            habit.setStatus(body.getActive() ? "0" : "1");
        }
        habitService.updateWeightHabit(habit);

        WeightHabit updated = habitService.selectWeightHabitById(habitId);
        return successWithData("更新成功", buildHabitItem(updated, loadUserCheckins(userId)));
    }

    @PostMapping("/{habitId}/checkin")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult checkinHabit(HttpServletRequest request,
                                   @PathVariable("habitId") Long habitId,
                                   @RequestBody(required = false) Map<String, Object> body) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightHabit habit = requireOwnedHabit(userId, habitId);
        if (habit == null) {
            return AjaxResult.error("习惯不存在");
        }
        if (!isHabitEnabled(habit)) {
            return AjaxResult.error("请先启用该习惯");
        }

        List<WeightHabitCheckin> checkins = loadHabitCheckins(userId, habitId);
        WeightHabitCheckin todayCheckin = findTodayCheckin(checkins);
        if (todayCheckin == null) {
            WeightHabitCheckin checkin = new WeightHabitCheckin();
            checkin.setHabitId(habitId);
            checkin.setUserId(userId);
            checkin.setCheckedAt(new Date());
            try {
                checkin.setImages(normalizeCheckinImages(body != null ? body.get("images") : null));
            } catch (IllegalArgumentException e) {
                return AjaxResult.error(e.getMessage());
            }
            habitCheckinService.insertWeightHabitCheckin(checkin);
        } else if (body != null && body.containsKey("images")) {
            try {
                todayCheckin.setImages(normalizeCheckinImages(body.get("images")));
            } catch (IllegalArgumentException e) {
                return AjaxResult.error(e.getMessage());
            }
            habitCheckinService.updateWeightHabitCheckin(todayCheckin);
        }

        recalculateHabitStats(habit);
        WeightHabit updated = habitService.selectWeightHabitById(habitId);
        return successWithData(todayCheckin == null ? "打卡成功" : "今天已经打过卡了", buildHabitItem(updated, loadUserCheckins(userId)));
    }

    @DeleteMapping("/{habitId}/checkin")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult undoCheckin(HttpServletRequest request, @PathVariable("habitId") Long habitId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightHabit habit = requireOwnedHabit(userId, habitId);
        if (habit == null) {
            return AjaxResult.error("习惯不存在");
        }

        List<WeightHabitCheckin> checkins = loadHabitCheckins(userId, habitId);
        List<WeightHabitCheckin> todayCheckins = findTodayCheckins(checkins);
        for (WeightHabitCheckin checkin : todayCheckins) {
            habitCheckinService.deleteWeightHabitCheckinById(checkin.getId());
        }

        recalculateHabitStats(habit);
        WeightHabit updated = habitService.selectWeightHabitById(habitId);
        return successWithData(todayCheckins.isEmpty() ? "今天还没有打卡" : "已撤销今日打卡", buildHabitItem(updated, loadUserCheckins(userId)));
    }

    @DeleteMapping("/{habitId}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteHabit(HttpServletRequest request, @PathVariable("habitId") Long habitId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightHabit habit = requireOwnedHabit(userId, habitId);
        if (habit == null) {
            return AjaxResult.error("习惯不存在");
        }

        for (WeightHabitCheckin checkin : loadHabitCheckins(userId, habitId)) {
            if (checkin != null && checkin.getId() != null) {
                habitCheckinService.deleteWeightHabitCheckinById(checkin.getId());
            }
        }
        habitService.deleteWeightHabitById(habitId);
        return successWithData("删除成功", habitId);
    }

    private AjaxResult validateHabitName(Long userId, String name, String source) {
        String securityMessage = contentSecurityService.validateText(
                userId, name, WechatContentSecurityService.SCENE_SOCIAL_LOG, source);
        return securityMessage != null ? AjaxResult.error(securityMessage) : null;
    }

    private WeightHabit requireOwnedHabit(Long userId, Long habitId) {
        WeightHabit habit = habitService.selectWeightHabitById(habitId);
        if (habit == null || habit.getUserId() == null || !habit.getUserId().equals(userId)) {
            return null;
        }
        return habit;
    }

    private List<WeightHabit> loadUserHabits(Long userId) {
        WeightHabit query = new WeightHabit();
        query.setUserId(userId);
        List<WeightHabit> habits = habitService.selectWeightHabitList(query);
        List<WeightHabit> safeHabits = habits != null ? new ArrayList<>(habits) : new ArrayList<WeightHabit>();
        safeHabits.sort((left, right) -> {
            int activeCompare = Boolean.compare(isHabitEnabled(right), isHabitEnabled(left));
            if (activeCompare != 0) {
                return activeCompare;
            }
            int updateCompare = compareDateDesc(left.getUpdateTime(), right.getUpdateTime());
            if (updateCompare != 0) {
                return updateCompare;
            }
            int createCompare = compareDateDesc(left.getCreateTime(), right.getCreateTime());
            if (createCompare != 0) {
                return createCompare;
            }
            return compareLongDesc(left.getId(), right.getId());
        });
        return safeHabits;
    }

    private List<WeightHabitCheckin> loadUserCheckins(Long userId) {
        WeightHabitCheckin query = new WeightHabitCheckin();
        query.setUserId(userId);
        List<WeightHabitCheckin> checkins = habitCheckinService.selectWeightHabitCheckinList(query);
        return checkins != null ? checkins : Collections.<WeightHabitCheckin>emptyList();
    }

    private List<WeightHabitCheckin> loadHabitCheckins(Long userId, Long habitId) {
        WeightHabitCheckin query = new WeightHabitCheckin();
        query.setUserId(userId);
        query.setHabitId(habitId);
        List<WeightHabitCheckin> checkins = habitCheckinService.selectWeightHabitCheckinList(query);
        return checkins != null ? checkins : Collections.<WeightHabitCheckin>emptyList();
    }

    private void applyCheckedAtRange(HttpServletRequest request, WeightHabitCheckin query) {
        Date beginCheckedAt = parseDateStart(request.getParameter("startDate"));
        Date endCheckedAt = parseDateEnd(request.getParameter("endDate"));
        if (beginCheckedAt != null) {
            query.getParams().put("beginCheckedAt", beginCheckedAt);
        }
        if (endCheckedAt != null) {
            query.getParams().put("endCheckedAt", endCheckedAt);
        }
    }

    private List<ApiHabitResponse.HabitItem> buildHabitItems(List<WeightHabit> habits, List<WeightHabitCheckin> checkins) {
        if (habits == null || habits.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, WeightHabitCheckin> todayCheckinMap = buildTodayCheckinMap(checkins);
        List<ApiHabitResponse.HabitItem> items = new ArrayList<>();
        for (WeightHabit habit : habits) {
            items.add(buildHabitItem(habit, todayCheckinMap.get(habit.getId())));
        }
        return items;
    }

    private ApiHabitResponse.HabitStats buildHabitStats(List<WeightHabit> habits, List<WeightHabitCheckin> checkins) {
        List<WeightHabit> safeHabits = habits != null ? habits : Collections.<WeightHabit>emptyList();
        List<WeightHabitCheckin> safeCheckins = checkins != null ? checkins : Collections.<WeightHabitCheckin>emptyList();
        Map<LocalDate, Set<Long>> dailyCheckinMap = buildDailyCheckinMap(safeCheckins);
        LocalDate today = LocalDate.now();

        int totalHabits = safeHabits.size();
        int activeHabits = 0;
        long bestStreak = 0L;
        for (WeightHabit habit : safeHabits) {
            if (isHabitEnabled(habit)) {
                if (shouldCompleteOnDay(habit, today)) {
                    activeHabits += 1;
                }
            }
            if (habit != null && habit.getLongestStreak() != null) {
                bestStreak = Math.max(bestStreak, habit.getLongestStreak());
            }
        }

        int completedToday = dailyCompletedCount(dailyCheckinMap, today, filterHabitsForDay(safeHabits, today));
        int todayCompletionRate = percentage(completedToday, activeHabits);

        int weeklyDone = 0;
        int weeklyCapacity = 0;
        List<ApiHabitResponse.HabitProgressDay> weeklyProgress = new ArrayList<>();
        for (int index = 6; index >= 0; index--) {
            LocalDate day = today.minusDays(index);
            List<WeightHabit> dailyHabits = filterHabitsForDay(safeHabits, day);
            int totalCount = dailyHabits.size();
            int completedCount = dailyCompletedCount(dailyCheckinMap, day, dailyHabits);
            weeklyDone += completedCount;
            weeklyCapacity += totalCount;

            ApiHabitResponse.HabitProgressDay progressDay = new ApiHabitResponse.HabitProgressDay();
            progressDay.setDate(day.format(DateTimeFormatter.ISO_LOCAL_DATE));
            progressDay.setLabel(dayOfWeekLabel(day));
            progressDay.setDay(String.format(Locale.ROOT, "%02d", day.getDayOfMonth()));
            progressDay.setCompletedCount(completedCount);
            progressDay.setTotalCount(totalCount);
            progressDay.setActive(day.equals(today));
            progressDay.setDone(completedCount > 0);
            weeklyProgress.add(progressDay);
        }

        ApiHabitResponse.HabitStats stats = new ApiHabitResponse.HabitStats();
        stats.setTotalHabits(totalHabits);
        stats.setActiveHabits(activeHabits);
        stats.setCompletedToday(completedToday);
        stats.setBestStreak(bestStreak);
        stats.setTodayCompletionRate(todayCompletionRate);
        stats.setWeeklyCompletionRate(percentage(weeklyDone, weeklyCapacity));
        stats.setWeeklyProgress(weeklyProgress);
        return stats;
    }

    private ApiHabitResponse.HabitItem buildHabitItem(WeightHabit habit, List<WeightHabitCheckin> userCheckins) {
        if (habit == null) {
            return null;
        }
        WeightHabitCheckin checkin = buildTodayCheckinMap(userCheckins).get(habit.getId());
        return buildHabitItem(habit, checkin);
    }

    private ApiHabitResponse.HabitItem buildHabitItem(WeightHabit habit, WeightHabitCheckin checkin) {
        ApiHabitResponse.HabitItem item = new ApiHabitResponse.HabitItem();
        Date checkedAt = checkin != null ? checkin.getCheckedAt() : null;
        item.setId(habit.getId());
        item.setName(habit.getName());
        item.setIcon(StringUtils.defaultIfBlank(habit.getIcon(), "✅"));
        item.setPeriod(StringUtils.defaultIfBlank(habit.getPeriod(), "1"));
        item.setFrequency(StringUtils.defaultIfBlank(habit.getFrequency(), "0"));
        item.setReminderTime(formatReminderTime(habit.getReminderTime()));
        item.setReminderWeekday(parseReminderWeekday(habit.getRemark()));
        item.setActive(isHabitEnabled(habit));
        item.setCurrentStreak(habit.getCurrentStreak() != null ? habit.getCurrentStreak() : 0L);
        item.setLongestStreak(habit.getLongestStreak() != null ? habit.getLongestStreak() : 0L);
        item.setTotalCheckins(habit.getTotalCheckins() != null ? habit.getTotalCheckins() : 0L);
        item.setCheckedToday(checkedAt != null);
        item.setCheckedAt(formatDateTime(checkedAt));
        item.setCheckinId(checkin != null ? checkin.getId() : null);
        item.setImages(checkin != null ? parseCheckinImages(checkin.getImages()) : Collections.<String>emptyList());
        return item;
    }

    private List<ApiHabitResponse.HabitCheckinItem> buildHabitCheckinItems(List<WeightHabitCheckin> checkins, List<WeightHabit> habits) {
        if (checkins == null || checkins.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, WeightHabit> habitMap = new HashMap<>();
        if (habits != null) {
            for (WeightHabit habit : habits) {
                if (habit != null && habit.getId() != null) {
                    habitMap.put(habit.getId(), habit);
                }
            }
        }

        List<ApiHabitResponse.HabitCheckinItem> items = new ArrayList<>();
        for (WeightHabitCheckin checkin : checkins) {
            if (checkin == null || checkin.getId() == null) {
                continue;
            }
            WeightHabit habit = habitMap.get(checkin.getHabitId());
            ApiHabitResponse.HabitCheckinItem item = new ApiHabitResponse.HabitCheckinItem();
            item.setId(checkin.getId());
            item.setHabitId(checkin.getHabitId());
            item.setHabitName(habit != null ? StringUtils.defaultIfBlank(habit.getName(), "习惯打卡") : "习惯打卡");
            item.setHabitIcon(habit != null ? StringUtils.defaultIfBlank(habit.getIcon(), "✅") : "✅");
            item.setCheckedAt(formatDateTime(checkin.getCheckedAt()));
            item.setNote(checkin.getNote());
            item.setImages(parseCheckinImages(checkin.getImages()));
            items.add(item);
        }
        return items;
    }

    private Map<Long, WeightHabitCheckin> buildTodayCheckinMap(List<WeightHabitCheckin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return Collections.emptyMap();
        }

        LocalDate today = LocalDate.now();
        Map<Long, WeightHabitCheckin> map = new HashMap<>();
        for (WeightHabitCheckin checkin : checkins) {
            if (checkin == null || checkin.getHabitId() == null || checkin.getCheckedAt() == null) {
                continue;
            }
            if (!today.equals(toLocalDate(checkin.getCheckedAt()))) {
                continue;
            }
            WeightHabitCheckin existing = map.get(checkin.getHabitId());
            if (existing == null || existing.getCheckedAt() == null || existing.getCheckedAt().before(checkin.getCheckedAt())) {
                map.put(checkin.getHabitId(), checkin);
            }
        }
        return map;
    }

    private String normalizeCheckinImages(Object rawImages) {
        List<String> images = parseCheckinImages(rawImages);
        if (images.isEmpty()) {
            return null;
        }
        return JSON.toJSONString(images);
    }

    private List<String> parseCheckinImages(Object rawImages) {
        if (rawImages == null) {
            return Collections.emptyList();
        }

        List<String> parsedImages;
        if (rawImages instanceof String) {
            String rawValue = StringUtils.trimToNull((String) rawImages);
            if (rawValue == null) {
                return Collections.emptyList();
            }
            try {
                parsedImages = JSON.parseArray(rawValue, String.class);
            } catch (Exception error) {
                throw new IllegalArgumentException("打卡图片参数格式不正确");
            }
        } else if (rawImages instanceof List) {
            parsedImages = new ArrayList<>();
            for (Object item : (List<?>) rawImages) {
                parsedImages.add(item != null ? String.valueOf(item) : null);
            }
        } else {
            throw new IllegalArgumentException("打卡图片参数格式不正确");
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
                throw new IllegalArgumentException("打卡图片最多上传 9 张");
            }
        }
        return new ArrayList<>(uniqueImages);
    }

    private Map<LocalDate, Set<Long>> buildDailyCheckinMap(List<WeightHabitCheckin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<LocalDate, Set<Long>> map = new HashMap<>();
        for (WeightHabitCheckin checkin : checkins) {
            if (checkin == null || checkin.getHabitId() == null || checkin.getCheckedAt() == null) {
                continue;
            }
            LocalDate day = toLocalDate(checkin.getCheckedAt());
            map.computeIfAbsent(day, key -> new TreeSet<>()).add(checkin.getHabitId());
        }
        return map;
    }

    private WeightHabitCheckin findTodayCheckin(List<WeightHabitCheckin> checkins) {
        List<WeightHabitCheckin> todayCheckins = findTodayCheckins(checkins);
        return todayCheckins.isEmpty() ? null : todayCheckins.get(0);
    }

    private List<WeightHabitCheckin> findTodayCheckins(List<WeightHabitCheckin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDate today = LocalDate.now();
        List<WeightHabitCheckin> todayCheckins = new ArrayList<>();
        for (WeightHabitCheckin checkin : checkins) {
            if (checkin != null && checkin.getCheckedAt() != null && today.equals(toLocalDate(checkin.getCheckedAt()))) {
                todayCheckins.add(checkin);
            }
        }
        todayCheckins.sort(Comparator.comparing(WeightHabitCheckin::getCheckedAt, Comparator.nullsLast(Date::compareTo)).reversed());
        return todayCheckins;
    }

    private void recalculateHabitStats(WeightHabit habit) {
        if (habit == null || habit.getId() == null || habit.getUserId() == null) {
            return;
        }

        List<WeightHabitCheckin> checkins = loadHabitCheckins(habit.getUserId(), habit.getId());
        Set<LocalDate> uniqueDays = new TreeSet<>();
        for (WeightHabitCheckin checkin : checkins) {
            if (checkin != null && checkin.getCheckedAt() != null) {
                uniqueDays.add(toLocalDate(checkin.getCheckedAt()));
            }
        }

        long currentStreak = 0L;
        long longestStreak = 0L;
        long runningStreak = 0L;
        LocalDate previousDay = null;
        for (LocalDate day : uniqueDays) {
            if (previousDay == null || previousDay.plusDays(1).equals(day)) {
                runningStreak += 1L;
            } else {
                runningStreak = 1L;
            }
            longestStreak = Math.max(longestStreak, runningStreak);
            currentStreak = runningStreak;
            previousDay = day;
        }

        habit.setCurrentStreak(currentStreak);
        habit.setLongestStreak(longestStreak);
        habit.setTotalCheckins((long) uniqueDays.size());
        habitService.updateWeightHabit(habit);
    }

    private int dailyCompletedCount(Map<LocalDate, Set<Long>> dailyCheckinMap, LocalDate day) {
        Set<Long> completedHabits = dailyCheckinMap.get(day);
        return completedHabits != null ? completedHabits.size() : 0;
    }

    private int dailyCompletedCount(Map<LocalDate, Set<Long>> dailyCheckinMap, LocalDate day, List<WeightHabit> habits) {
        Set<Long> completedHabits = dailyCheckinMap.get(day);
        if (completedHabits == null || completedHabits.isEmpty() || habits == null || habits.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (WeightHabit habit : habits) {
            if (habit != null && habit.getId() != null && completedHabits.contains(habit.getId())) {
                count += 1;
            }
        }
        return count;
    }

    private List<WeightHabit> filterHabitsForDay(List<WeightHabit> habits, LocalDate day) {
        if (habits == null || habits.isEmpty()) {
            return Collections.emptyList();
        }
        List<WeightHabit> filtered = new ArrayList<>();
        for (WeightHabit habit : habits) {
            if (shouldCompleteOnDay(habit, day)) {
                filtered.add(habit);
            }
        }
        return filtered;
    }

    private int percentage(int numerator, int denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return 0;
        }
        return (int) Math.round((double) numerator * 100D / (double) denominator);
    }

    private String dayOfWeekLabel(LocalDate day) {
        switch (day.getDayOfWeek()) {
            case MONDAY:
                return "一";
            case TUESDAY:
                return "二";
            case WEDNESDAY:
                return "三";
            case THURSDAY:
                return "四";
            case FRIDAY:
                return "五";
            case SATURDAY:
                return "六";
            default:
                return "日";
        }
    }

    private String normalizeIcon(String icon) {
        return StringUtils.defaultIfBlank(StringUtils.trimToNull(icon), "✅");
    }

    private String normalizePeriod(String period) {
        String normalized = StringUtils.trimToNull(period);
        if ("0".equals(normalized) || "1".equals(normalized) || "2".equals(normalized)) {
            return normalized;
        }
        return "1";
    }

    private String normalizeFrequency(String frequency) {
        String normalized = StringUtils.trimToNull(frequency);
        if ("0".equals(normalized) || "1".equals(normalized)) {
            return normalized;
        }
        return "0";
    }

    private String normalizeReminderWeekday(String reminderWeekday) {
        String normalized = StringUtils.trimToNull(reminderWeekday);
        if (normalized == null) {
            return null;
        }

        String[] parts = StringUtils.split(normalized, ',');
        if (parts == null || parts.length == 0) {
            return null;
        }

        Set<String> weekdays = new TreeSet<>();
        for (String part : parts) {
            String item = StringUtils.trimToNull(part);
            if (!"0".equals(item)
                    && !"1".equals(item)
                    && !"2".equals(item)
                    && !"3".equals(item)
                    && !"4".equals(item)
                    && !"5".equals(item)
                    && !"6".equals(item)) {
                throw new IllegalArgumentException("每周提醒应选择至少一天");
            }
            weekdays.add(item);
        }
        return String.join(",", weekdays);
    }

    private Date parseReminderTime(String raw) {
        String value = StringUtils.trimToNull(raw);
        if (value == null) {
            return null;
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(TIME_PATTERN, Locale.ROOT);
            formatter.setLenient(false);
            return formatter.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("提醒时间格式应为 HH:mm");
        }
    }

    private boolean isHabitEnabled(WeightHabit habit) {
        return habit != null
                && !"1".equals(StringUtils.defaultString(habit.getStatus(), "0"))
                && !Integer.valueOf(0).equals(habit.getIsActive());
    }

    private boolean shouldCompleteOnDay(WeightHabit habit, LocalDate day) {
        if (!isHabitEnabled(habit) || day == null) {
            return false;
        }
        String frequency = StringUtils.defaultString(habit.getFrequency(), "0");
        if (!"1".equals(frequency)) {
            return true;
        }
        String reminderWeekday = parseReminderWeekday(habit.getRemark());
        if (StringUtils.isBlank(reminderWeekday)) {
            return true;
        }
        String todayWeekday = String.valueOf(day.getDayOfWeek().getValue() % 7);
        List<String> weekdays = java.util.Arrays.asList(reminderWeekday.split(","));
        return weekdays.contains(todayWeekday);
    }

    private String parseReminderWeekday(String remark) {
        String value = StringUtils.trimToNull(remark);
        if (value == null || !value.startsWith("reminderWeekday=")) {
            return null;
        }
        try {
            return normalizeReminderWeekday(StringUtils.substringAfter(value, "="));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private String buildReminderRemark(String frequency, String reminderWeekday, String existingRemark) {
        if (!"1".equals(frequency)) {
            return null;
        }
        String normalizedWeekday = normalizeReminderWeekday(reminderWeekday);
        if (normalizedWeekday != null) {
            return "reminderWeekday=" + normalizedWeekday;
        }
        String existingWeekday = parseReminderWeekday(existingRemark);
        if (existingWeekday != null) {
            return "reminderWeekday=" + existingWeekday;
        }
        throw new IllegalArgumentException("每周提醒应选择星期几");
    }

    private String formatReminderTime(Date reminderTime) {
        return reminderTime != null ? new SimpleDateFormat(TIME_PATTERN, Locale.ROOT).format(reminderTime) : null;
    }

    private String formatDateTime(Date date) {
        return date != null ? DateUtils.parseDateToStr(DATE_TIME_PATTERN, date) : null;
    }

    private Date parseDateStart(String raw) {
        return parseDate(raw, false);
    }

    private Date parseDateEnd(String raw) {
        return parseDate(raw, true);
    }

    private Date parseDate(String raw, boolean endOfDay) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PARAM_PATTERN, Locale.ROOT);
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

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private int compareDateDesc(Date left, Date right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return right.compareTo(left);
    }

    private int compareLongDesc(Long left, Long right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return right.compareTo(left);
    }
}
