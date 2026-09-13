package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户扩展信息对象 weight_user
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 用户ID，关联sys_user.user_id */
    private Long userId;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 头像URL */
    @Excel(name = "头像URL")
    private String avatar;

    /** 性别（0未知 1男 2女） */
    @Excel(name = "性别", readConverterExp = "0=未知,1=男,2=女")
    private String gender;

    /** 身高(cm) */
    @Excel(name = "身高(cm)")
    private BigDecimal height;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthday;

    /** 目标体重(kg) */
    @Excel(name = "目标体重(kg)")
    private BigDecimal targetWeight;

    /** 目标完成日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "目标完成日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date targetCompletionDate;

    /** 当前体重(kg) */
    @Excel(name = "当前体重(kg)")
    private BigDecimal currentWeight;

    /** BMI指数 */
    @Excel(name = "BMI指数")
    private BigDecimal bmi;

    /** 建议每日摄入上限 */
    @Excel(name = "建议每日摄入上限")
    private Long dailyCalorieTarget;

    /** 每日热量缺口目标 */
    @Excel(name = "每日热量缺口目标")
    private Long dailyCalorieDeficitTarget;

    /** 每日饮水目标(杯) */
    @Excel(name = "每日饮水目标(杯)")
    private Long dailyWaterTarget;

    /** 连续打卡天数 */
    @Excel(name = "连续打卡天数")
    private Long streakDays;

    /** 成就积分 */
    @Excel(name = "成就积分")
    private Long achievementPoints;

    /** 首页展示的主圈子ID */
    @Excel(name = "首页展示的主圈子ID")
    private Long mainCircleId;

    /** 是否启用圈子功能 */
    @Excel(name = "是否启用圈子功能")
    private Integer circleFeatureEnabled;

    /** 同步模式（0实时 1手动） */
    @Excel(name = "同步模式", readConverterExp = "0=实时,1=手动")
    private String syncMode;

    /** 注册来源（0微信小程序 1iOS 2Android 3H5 4后台创建） */
    @Excel(name = "来源", readConverterExp = "0=微信小程序,1=iOS,2=Android,3=H5,4=后台")
    private String source;

    /** 微信openid */
    private String openid;

    /** 微信unionid */
    private String unionid;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setTargetWeight(BigDecimal targetWeight) {
        this.targetWeight = targetWeight;
    }

    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public void setTargetCompletionDate(Date targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Date getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setDailyCalorieTarget(Long dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public Long getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieDeficitTarget(Long dailyCalorieDeficitTarget) {
        this.dailyCalorieDeficitTarget = dailyCalorieDeficitTarget;
    }

    public Long getDailyCalorieDeficitTarget() {
        return dailyCalorieDeficitTarget;
    }

    public void setDailyWaterTarget(Long dailyWaterTarget) {
        this.dailyWaterTarget = dailyWaterTarget;
    }

    public Long getDailyWaterTarget() {
        return dailyWaterTarget;
    }

    public void setStreakDays(Long streakDays) {
        this.streakDays = streakDays;
    }

    public Long getStreakDays() {
        return streakDays;
    }

    public void setAchievementPoints(Long achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public Long getAchievementPoints() {
        return achievementPoints;
    }

    public void setMainCircleId(Long mainCircleId) {
        this.mainCircleId = mainCircleId;
    }

    public Long getMainCircleId() {
        return mainCircleId;
    }

    public void setCircleFeatureEnabled(Integer circleFeatureEnabled) {
        this.circleFeatureEnabled = circleFeatureEnabled;
    }

    public Integer getCircleFeatureEnabled() {
        return circleFeatureEnabled;
    }

    public void setSyncMode(String syncMode) {
        this.syncMode = syncMode;
    }

    public String getSyncMode() {
        return syncMode;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("nickname", getNickname())
                .append("avatar", getAvatar())
                .append("gender", getGender())
                .append("height", getHeight())
                .append("birthday", getBirthday())
                .append("targetWeight", getTargetWeight())
                .append("targetCompletionDate", getTargetCompletionDate())
                .append("currentWeight", getCurrentWeight())
                .append("bmi", getBmi())
                .append("dailyCalorieTarget", getDailyCalorieTarget())
                .append("dailyCalorieDeficitTarget", getDailyCalorieDeficitTarget())
                .append("dailyWaterTarget", getDailyWaterTarget())
                .append("streakDays", getStreakDays())
                .append("achievementPoints", getAchievementPoints())
                .append("mainCircleId", getMainCircleId())
                .append("circleFeatureEnabled", getCircleFeatureEnabled())
                .append("syncMode", getSyncMode())
                .append("source", getSource())
                .append("openid", getOpenid())
                .append("unionid", getUnionid())
                .append("phone", getPhone())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
