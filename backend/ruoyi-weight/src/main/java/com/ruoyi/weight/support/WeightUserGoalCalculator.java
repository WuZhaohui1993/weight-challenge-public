package com.ruoyi.weight.support;

import com.ruoyi.weight.domain.WeightUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 用户目标相关热量估算。
 */
public final class WeightUserGoalCalculator {

    private static final BigDecimal KCAL_PER_KG = BigDecimal.valueOf(7700L);
    private static final BigDecimal WEIGHT_FACTOR = BigDecimal.valueOf(10);
    private static final BigDecimal HEIGHT_FACTOR = BigDecimal.valueOf(6.25);
    private static final BigDecimal AGE_FACTOR = BigDecimal.valueOf(5);
    private static final BigDecimal LIGHT_ACTIVITY_FACTOR = BigDecimal.valueOf(1.2);

    private WeightUserGoalCalculator() {
    }

    public static Long calculateDailyCalorieDeficit(WeightUser weightUser) {
        if (weightUser == null) {
            return 0L;
        }
        return calculateDailyCalorieDeficit(
                weightUser.getCurrentWeight(),
                weightUser.getTargetWeight(),
                weightUser.getTargetCompletionDate()
        );
    }

    public static Long calculateDailyCalorieDeficit(BigDecimal currentWeight, BigDecimal targetWeight, Date targetCompletionDate) {
        if (currentWeight == null || targetWeight == null || targetCompletionDate == null) {
            return 0L;
        }

        BigDecimal weightGap = currentWeight.subtract(targetWeight);
        if (weightGap.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }

        LocalDate targetDate = toLocalDate(targetCompletionDate);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        if (days <= 0) {
            return 0L;
        }

        return weightGap.multiply(KCAL_PER_KG)
                .divide(BigDecimal.valueOf(days), 0, RoundingMode.HALF_UP)
                .longValue();
    }

    public static Long calculateDailyCalorieTarget(WeightUser weightUser) {
        if (weightUser == null) {
            return 0L;
        }
        return calculateDailyCalorieTarget(
                weightUser.getCurrentWeight(),
                weightUser.getHeight(),
                weightUser.getBirthday(),
                weightUser.getGender(),
                weightUser.getTargetWeight(),
                weightUser.getTargetCompletionDate()
        );
    }

    public static Long calculateDailyCalorieTarget(BigDecimal currentWeight,
                                                   BigDecimal height,
                                                   Date birthday,
                                                   String gender,
                                                   BigDecimal targetWeight,
                                                   Date targetCompletionDate) {
        if (currentWeight == null || height == null || birthday == null) {
            return 0L;
        }
        if (currentWeight.compareTo(BigDecimal.ZERO) <= 0 || height.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }

        Integer age = calculateAge(birthday);
        if (age == null) {
            return 0L;
        }

        BigDecimal bmr = currentWeight.multiply(WEIGHT_FACTOR)
                .add(height.multiply(HEIGHT_FACTOR))
                .subtract(BigDecimal.valueOf(age).multiply(AGE_FACTOR))
                .add(BigDecimal.valueOf(resolveGenderOffset(gender)));
        if (bmr.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }

        Long deficit = calculateDailyCalorieDeficit(currentWeight, targetWeight, targetCompletionDate);
        BigDecimal maintenanceCalories = bmr.multiply(LIGHT_ACTIVITY_FACTOR);
        long suggestedTarget = maintenanceCalories
                .subtract(BigDecimal.valueOf(deficit == null ? 0L : deficit))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        return Math.max(resolveMinimumIntake(gender), suggestedTarget);
    }

    private static Integer calculateAge(Date birthday) {
        LocalDate birthdayDate = toLocalDate(birthday);
        LocalDate today = LocalDate.now();
        if (!birthdayDate.isBefore(today)) {
            return null;
        }

        int age = Period.between(birthdayDate, today).getYears();
        if (age < 10 || age > 100) {
            return null;
        }
        return age;
    }

    private static int resolveGenderOffset(String gender) {
        if ("1".equals(gender)) {
            return 5;
        }
        if ("2".equals(gender)) {
            return -161;
        }
        return -78;
    }

    private static long resolveMinimumIntake(String gender) {
        return "1".equals(gender) ? 1500L : 1200L;
    }

    private static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
