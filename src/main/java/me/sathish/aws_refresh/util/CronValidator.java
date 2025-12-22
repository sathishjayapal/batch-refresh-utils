package me.sathish.aws_refresh.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;

@Slf4j
public class CronValidator {

    public static boolean isValid(String cronExpression) {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return false;
        }
        
        try {
            CronExpression.parse(cronExpression);
            return true;
        } catch (Exception e) {
            log.debug("Invalid cron expression: {}", cronExpression, e);
            return false;
        }
    }

    public static String getValidationMessage(String cronExpression) {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return "Cron expression cannot be empty";
        }
        
        try {
            CronExpression.parse(cronExpression);
            return null;
        } catch (Exception e) {
            return "Invalid cron expression: " + e.getMessage();
        }
    }
}
