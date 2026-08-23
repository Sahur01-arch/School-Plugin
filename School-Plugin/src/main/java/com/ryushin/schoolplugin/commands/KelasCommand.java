package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.manager.ClassroomManager;
import com.ryushin.schoolplugin.util.MessageHelper;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class KelasCommand {

    private final RyuPlugin plugin;
    private final ClassroomManager classroomManager;

    public KelasCommand(RyuPlugin plugin) {
        this.plugin = plugin;
        this.classroomManager = new ClassroomManager(plugin);
    }

    public void register() {
        plugin.commandManager().command("kelas")
            .permission("server.kelas", MessageHelper.get("access_denied"))
            .usage("/kelas <tambah|hapus|list|info|siswa|masukkan|keluarkan|setweight>")
            .tab((sender, args) -> {
                if (args.length == 1) {
                    return List.of("tambah", "hapus", "list", "info", "siswa", "masukkan", "keluarkan", "setweight");
                }
                if (args.length == 2) {
                    return switch (args[0].toLowerCase()) {
                        case "hapus", "info", "siswa", "setweight" -> classroomManager.getSchoolClasses();
                        case "masukkan", "keluarkan" -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                        default -> List.of();
                    };
                }
                if (args.length == 3) {
                    return switch (args[0].toLowerCase()) {
                        case "masukkan", "keluarkan" -> classroomManager.getSchoolClasses();
                        case "setweight" -> List.of("0", "100", "200", "500", "1000");
                        default -> List.of();
                    };
                }
                return List.of();
            })
            .handler((sender, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(MessageHelper.get("kelas.usage"));
                    return;
                }

                String aksi = args[0].toLowerCase();

                switch (aksi) {
                    case "tambah" -> {
                        if (args.length < 2) {
                            sender.sendMessage(MessageHelper.get("kelas.usage"));
                            return;
                        }
                        int weight = 0;
                        if (args.length > 2) {
                            try {
                                weight = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage("§cWeight harus berupa angka.");
                                return;
                            }
                        }
                        classroomManager.addGroup(args[1], weight);
                        sender.sendMessage(MessageHelper.get("kelas.created", "kelas", args[1]));
                    }
                    case "hapus" -> {
                        if (args.length < 2) {
                            sender.sendMessage(MessageHelper.get("kelas.usage"));
                            return;
                        }
                        Player p = sender instanceof Player ? (Player) sender : null;
                        classroomManager.removeGroup(args[1], p);
                    }
                    case "list" -> {
                        if (!isLuckPermsAvailable(sender)) return;
                        var lp = LuckPermsProvider.get();
                        sender.sendMessage("§6=== Daftar Kelas ===");
                        for (String kelasName : classroomManager.getSchoolClasses()) {
                            Group group = lp.getGroupManager().getGroup(kelasName);
                            if (group != null) {
                                sender.sendMessage("§7- §f" + kelasName.toUpperCase() + " §7(Weight: §e" + group.getWeight() + "§7)");
                            } else {
                                sender.sendMessage("§7- §f" + kelasName.toUpperCase() + " §c(tidak ada)");
                            }
                        }
                    }
                    case "info" -> {
                        if (args.length < 2) {
                            sender.sendMessage(MessageHelper.get("kelas.usage"));
                            return;
                        }
                        if (!isLuckPermsAvailable(sender)) return;
                        var lp = LuckPermsProvider.get();
                        Group group = lp.getGroupManager().getGroup(args[1]);
                        if (group == null) {
                            sender.sendMessage(MessageHelper.get("kelas.not_found", "kelas", args[1]));
                            return;
                        }
                        sender.sendMessage("§6=== Info Kelas " + group.getName().toUpperCase() + " ===");
                        sender.sendMessage("§fWeight: §e" + group.getWeight());
                    }
                    case "siswa" -> {
                        if (args.length < 2) {
                            sender.sendMessage(MessageHelper.get("kelas.usage"));
                            return;
                        }
                        if (!isLuckPermsAvailable(sender)) return;
                        var lp = LuckPermsProvider.get();
                        String kelas = args[1];
                        if (lp.getGroupManager().getGroup(kelas) == null) {
                            sender.sendMessage(MessageHelper.get("kelas.not_found", "kelas", kelas));
                            return;
                        }
                        sender.sendMessage("§6=== Siswa Kelas " + kelas.toUpperCase() + " ===");
                        int count = 0;
                        for (User user : lp.getUserManager().getLoadedUsers()) {
                            boolean inClass = user.getNodes(NodeType.INHERITANCE).stream()
                                    .anyMatch(node -> node.getGroupName().equalsIgnoreCase(kelas));
                            if (inClass) {
                                sender.sendMessage("§7- §f" + user.getUsername());
                                count++;
                            }
                        }
                        if (count == 0) {
                            sender.sendMessage("§7Belum ada siswa yang terdaftar di kelas ini.");
                        }
                    }
                    case "masukkan" -> {
                        if (args.length < 3) {
                            sender.sendMessage("§cGunakan: /kelas masukkan <player> <kelas>");
                            return;
                        }
                        if (!classroomManager.getSchoolClasses().contains(args[2].toLowerCase())) {
                            sender.sendMessage(MessageHelper.get("kelas.invalid_class", "kelas", args[2]));
                            return;
                        }
                        classroomManager.addStudentToClass(args[1], args[2].toLowerCase(), sender);
                    }
                    case "keluarkan" -> {
                        if (args.length < 3) {
                            sender.sendMessage("§cGunakan: /kelas keluarkan <player> <kelas>");
                            return;
                        }
                        classroomManager.removeStudentFromClass(args[1], args[2].toLowerCase(), sender);
                    }
                    case "setweight" -> {
                        if (args.length < 3) {
                            sender.sendMessage("§cGunakan: /kelas setweight <kelas> <weight>");
                            return;
                        }
                        try {
                            int weight = Integer.parseInt(args[2]);
                            classroomManager.setClassWeight(args[1], weight);
                            sender.sendMessage("§aWeight kelas " + args[1] + " diatur menjadi " + weight + ".");
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§cWeight harus berupa angka.");
                        }
                    }
                    default -> sender.sendMessage(MessageHelper.get("kelas.usage"));
                }
            })
            .register();
    }

    private boolean isLuckPermsAvailable(org.bukkit.command.CommandSender sender) {
        if (org.bukkit.Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            sender.sendMessage("§cLuckPerms tidak terpasang! Fitur kelas membutuhkan LuckPerms.");
            return false;
        }
        return true;
    }
}
