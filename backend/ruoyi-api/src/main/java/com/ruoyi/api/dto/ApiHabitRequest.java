package com.ruoyi.api.dto;

/**
 * 移动端习惯创建 / 更新请求 DTO
 */
public class ApiHabitRequest {

    private String name;

    private String icon;

    private String period;

    private String frequency;

    private String reminderTime;

    private String reminderWeekday;

    private Boolean active;

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
}
