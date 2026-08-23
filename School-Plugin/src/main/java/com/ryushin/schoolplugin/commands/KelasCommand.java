package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.manager.ClassroomManager;
import com.ryushin.schoolplugin.util.MessageHelper;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
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
                if (args.length == 2 && !args[0].equalsIgnoreCase("list")) {
                    return classroomManager.getSchoolClasses();
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
                        int weight = args.length > 2 ? Integer.parseInt(args[2]) : 0;
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
