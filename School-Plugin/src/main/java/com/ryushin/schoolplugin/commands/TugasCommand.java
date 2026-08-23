package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.manager.AssignmentManager;
import com.ryushin.schoolplugin.manager.ClassroomManager;
import com.ryushin.schoolplugin.util.MessageHelper;
import org.bukkit.entity.Player;

import java.util.List;

public class TugasCommand {

    private final RyuPlugin plugin;
    private final AssignmentManager assignmentManager;
    private final ClassroomManager classroomManager;

    public TugasCommand(RyuPlugin plugin) {
        this.plugin = plugin;
        this.assignmentManager = new AssignmentManager(plugin);
        this.classroomManager = new ClassroomManager(plugin);
    }

    public void register() {
        plugin.commandManager().command("tugas")
            .permission("server.tugas.use", MessageHelper.get("access_denied"))
            .usage("/tugas [cek <kelas>]")
            .tab((sender, args) -> {
                if (args.length == 1) return List.of("cek");
                if (args.length == 2 && args[0].equalsIgnoreCase("cek")) return classroomManager.getSchoolClasses();
                return List.of();
            })
            .handler((sender, args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MessageHelper.get("player_only"));
                    return;
                }

                if (args.length > 0 && args[0].equalsIgnoreCase("cek")) {
                    if (!player.hasPermission("server.tugas.guru") && !player.isOp()) {
                        player.sendMessage(MessageHelper.get("tugas.no_permission"));
                        return;
                    }
                    if (args.length < 2) {
                        player.sendMessage(MessageHelper.get("tugas.usage"));
                        return;
                    }
                    assignmentManager.openChestForTeacher(player, args[1]);
                    return;
                }

                if (player.hasPermission("server.tugas.guru") && !player.isOp()) {
                    player.sendMessage(MessageHelper.get("tugas.teacher_only"));
                    return;
                }

                var kelasCheck = classroomManager.validateStudentClass(player);
                if (!kelasCheck.isValid()) {
                    player.sendMessage("§c" + kelasCheck.getMessage());
                    return;
                }

                var result = assignmentManager.submitAssignment(player, kelasCheck.getClassName());
                player.sendMessage(result.isSuccess() ? "§a" + result.getMessage() : "§c" + result.getMessage());
            })
            .register();
    }
}
