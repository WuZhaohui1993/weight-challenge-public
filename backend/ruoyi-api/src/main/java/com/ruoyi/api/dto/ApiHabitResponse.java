package com.ruoyi.api.dto;

import java.util.List;

/**
 * 移动端习惯响应 DTO
 */
public class ApiHabitResponse {

    public static class HabitItem {

        private Long id;

        private String name;

        private String icon;

        private String period;

        private String frequency;

        private String reminderTime;

        private String reminderWeekday;

        private Boolean active;

        private Long currentStreak;

        private Long longestStreak;

        private Long totalCheckins;

        private Boolean checkedToday;

        private String checkedAt;

        private Long checkinId;

        private List<String> images;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getReminderTime() {
            return reminderTime;
        }

        public void setReminderTime(String reminderTime) {
            this.reminderTime = reminderTime;
        }

        public String getReminderWeekday() {
            return reminderWeekday;
        }

        public void setReminderWeekday(String reminderWeekday) {
            this.reminderWeekday = reminderWeekday;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Long getCurrentStreak() {
            return currentStreak;
        }

        public void setCurrentStreak(Long currentStreak) {
            this.currentStreak = currentStreak;
        }

        public Long getLongestStreak() {
            return longestStreak;
        }

        public void setLongestStreak(Long longestStreak) {
            this.longestStreak = longestStreak;
        }

        public Long getTotalCheckins() {
            return totalCheckins;
        }

        public void setTotalCheckins(Long totalCheckins) {
            this.totalCheckins = totalCheckins;
        }

        public Boolean getCheckedToday() {
            return checkedToday;
        }

        public void setCheckedToday(Boolean checkedToday) {
            this.checkedToday = checkedToday;
        }

        public String getCheckedAt() {
            return checkedAt;
        }

        public void setCheckedAt(String checkedAt) {
            this.checkedAt = checkedAt;
        }

        public Long getCheckinId() {
            return checkinId;
        }

        public void setCheckinId(Long checkinId) {
            this.checkinId = checkinId;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class HabitStats {

        private Integer totalHabits;

        private Integer activeHabits;

        private Integer completedToday;

        private Long bestStreak;

        private Integer todayCompletionRate;

        private Integer weeklyCompletionRate;

        private List<HabitProgressDay> weeklyProgress;

        public Integer getTotalHabits() {
            return totalHabits;
        }

        public void setTotalHabits(Integer totalHabits) {
            this.totalHabits = totalHabits;
        }

        public Integer getActiveHabits() {
            return activeHabits;
        }

        public void setActiveHabits(Integer activeHabits) {
            this.activeHabits = activeHabits;
        }

        public Integer getCompletedToday() {
            return completedToday;
        }

        public void setCompletedToday(Integer completedToday) {
            this.completedToday = completedToday;
        }

        public Long getBestStreak() {
            return bestStreak;
        }

        public void setBestStreak(Long bestStreak) {
            this.bestStreak = bestStreak;
        }

        public Integer getTodayCompletionRate() {
            return todayCompletionRate;
        }

        public void setTodayCompletionRate(Integer todayCompletionRate) {
            this.todayCompletionRate = todayCompletionRate;
        }

        public Integer getWeeklyCompletionRate() {
            return weeklyCompletionRate;
        }

        public void setWeeklyCompletionRate(Integer weeklyCompletionRate) {
            this.weeklyCompletionRate = weeklyCompletionRate;
        }

        public List<HabitProgressDay> getWeeklyProgress() {
            return weeklyProgress;
        }

        public void setWeeklyProgress(List<HabitProgressDay> weeklyProgress) {
            this.weeklyProgress = weeklyProgress;
        }
    }

    public static class HabitCheckinItem {

        private Long id;

        private Long habitId;

        private String habitName;

        private String habitIcon;

        private String checkedAt;

        private String note;

        private List<String> images;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getHabitId() {
            return habitId;
        }

        public void setHabitId(Long habitId) {
            this.habitId = habitId;
        }

        public String getHabitName() {
            return habitName;
        }

        public void setHabitName(String habitName) {
            this.habitName = habitName;
        }

        public String getHabitIcon() {
            return habitIcon;
        }

        public void setHabitIcon(String habitIcon) {
            this.habitIcon = habitIcon;
        }

        public String getCheckedAt() {
            return checkedAt;
        }

        public void setCheckedAt(String checkedAt) {
            this.checkedAt = checkedAt;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class HabitProgressDay {

        private String date;

        private String label;

        private String day;

        private Integer completedCount;

        private Integer totalCount;

        private Boolean active;

        private Boolean done;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public Integer getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(Integer completedCount) {
            this.completedCount = completedCount;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Boolean getDone() {
            return done;
        }

        public void setDone(Boolean done) {
            this.done = done;
        }
    }
}
