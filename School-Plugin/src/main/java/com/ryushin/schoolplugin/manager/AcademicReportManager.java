package com.ryushin.schoolplugin.manager;

import com.ryushin.ryulib.RyuPlugin;
import java.util.*;

public class AcademicReportManager {

    private final RyuPlugin plugin;
    private final Map<String, StudentReport> reports = new HashMap<>();

    public AcademicReportManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public Result setGrade(String uuid, String playerName, String subject, String grade) {
        if (uuid == null || playerName == null || subject == null || grade == null) {
            return new Result(false, "Incomplete grade data.");
        }

        StudentReport report = reports.computeIfAbsent(uuid, k -> new StudentReport(uuid, playerName));
        report.setName(playerName);
        report.getGrades().put(subject, grade);
        report.calculateAverage();

        return new Result(true, "Grade for " + subject + " for " + playerName + " set to " + grade + ". Average: " + report.getAverage());
    }

    public StudentReport getReport(String uuid) {
        if (uuid == null) return null;
        return reports.get(uuid);
    }

    public static class StudentReport {
        private final String uuid;
        private String name;
        private final Map<String, String> grades = new HashMap<>();
        private double average = 0.0;

        public StudentReport(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public void calculateAverage() {
            if (grades.isEmpty()) {
                average = 0.0;
                return;
            }
            double sum = 0;
            int count = 0;
            for (String gradeStr : grades.values()) {
                try {
                    sum += Double.parseDouble(gradeStr);
                    count++;
                } catch (NumberFormatException ignored) {}
            }
            average = count == 0 ? 0.0 : Math.round((sum / count) * 100.0) / 100.0;
        }

        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Map<String, String> getGrades() { return grades; }
        public double getAverage() { return average; }
    }

    public static class Result {
        private final boolean success;
        private final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
