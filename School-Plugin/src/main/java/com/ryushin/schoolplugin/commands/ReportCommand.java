package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.manager.AcademicReportManager;
import com.ryushin.schoolplugin.util.MessageHelper;
import org.bukkit.Bukkit;

import java.util.List;

public class ReportCommand {

    private final RyuPlugin plugin;
    private final AcademicReportManager academicReportManager;

    public ReportCommand(RyuPlugin plugin) {
        this.plugin = plugin;
        this.academicReportManager = new AcademicReportManager(plugin);
    }

    private String uuidOf(String playerName) {
        return Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
    }

    public void register() {
        plugin.commandManager().command("report")
            .permission("server.report", MessageHelper.get("access_denied"))
            .usage("/report <set|view>")
            .tab((sender, args) -> args.length == 1 ? List.of("set", "view") : List.of())
            .handler((sender, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(MessageHelper.get("report.usage"));
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "set" -> {
                        if (args.length < 4) { sender.sendMessage(MessageHelper.get("report.usage")); return; }
                        String playerName = args[1];
                        String subject = args[2];
                        String grade = args[3];
                        String uuid = uuidOf(playerName);
                        academicReportManager.setGrade(uuid, playerName, subject, grade);
                        var report = academicReportManager.getReport(uuid);
                        String average = report != null ? String.valueOf(report.getAverage()) : grade;
                        sender.sendMessage(MessageHelper.get("report.set_success",
                            "subject", subject, "player", playerName, "grade", grade, "average", average));
                    }

                    case "view" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("report.usage")); return; }
                        String uuid = uuidOf(args[1]);
                        var report = academicReportManager.getReport(uuid);
                        if (report == null) {
                            sender.sendMessage(MessageHelper.get("report.not_found", "player", args[1]));
                            return;
                        }
                        sender.sendMessage("§6=== Rapor " + report.getName() + " ===");
                        for (var entry : report.getGrades().entrySet()) {
                            sender.sendMessage("§7- §f" + entry.getKey() + ": §e" + entry.getValue());
                        }
                        sender.sendMessage("§7Rata-rata: §e" + report.getAverage());
                    }

                    default -> sender.sendMessage(MessageHelper.get("report.usage"));
                }
            })
            .register();
    }
}
