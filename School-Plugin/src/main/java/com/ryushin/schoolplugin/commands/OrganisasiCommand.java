package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.manager.SchoolClubManager;
import com.ryushin.schoolplugin.util.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class OrganisasiCommand {

    private final RyuPlugin plugin;
    private final SchoolClubManager schoolClubManager;

    public OrganisasiCommand(RyuPlugin plugin) {
        this.plugin = plugin;
        this.schoolClubManager = new SchoolClubManager(plugin);
    }

    private String uuidOf(String playerName) {
        return Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
    }

    public void register() {
        plugin.commandManager().command("organisasi")
            .permission("server.organisasi", MessageHelper.get("access_denied"))
            .usage("/organisasi <buat|hapus|tambah|keluar|jabatan|lihat|daftar|saya>")
            .tab((sender, args) -> args.length == 1
                ? List.of("buat", "hapus", "tambah", "keluar", "jabatan", "lihat", "daftar", "saya")
                : List.of())
            .handler((sender, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(MessageHelper.get("organisasi.usage"));
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "buat" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("organisasi.usage")); return; }
                        String desc = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "";
                        var r = schoolClubManager.createClub(args[1], desc);
                        sender.sendMessage(r.isSuccess()
                            ? MessageHelper.get("organisasi.created", "org", args[1])
                            : MessageHelper.get("organisasi.already_exists", "org", args[1]));
                    }

                    case "hapus" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("organisasi.usage")); return; }
                        var r = schoolClubManager.deleteClub(args[1]);
                        sender.sendMessage(r.isSuccess()
                            ? MessageHelper.get("organisasi.deleted", "org", args[1])
                            : MessageHelper.get("organisasi.not_found", "org", args[1]));
                    }

                    case "tambah" -> {
                        if (args.length < 3) { sender.sendMessage(MessageHelper.get("organisasi.usage")); return; }
                        String role = args.length > 3 ? args[3] : "Member";
                        String uuid = uuidOf(args[2]);
                        var r = schoolClubManager.addMember(args[1], args[2], uuid, role);
                        sender.sendMessage(r.isSuccess() ? "§a" + r.getMessage() : "§c" + r.getMessage());
                    }

                    case "keluar" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("organisasi.usage")); return; }
                        String pname;
                        if (args.length > 2) {
                            pname = args[2];
                        } else if (sender instanceof Player p) {
                            pname = p.getName();
                        } else {
                            sender.sendMessage(MessageHelper.get("organisasi.usage"));
                            return;
                        }
                        String uuid = uuidOf(pname);
                        var r = schoolClubManager.removeMember(args[1], pname, uuid);
                        sender.sendMessage(r.isSuccess() ? "§a" + r.getMessage() : "§c" + r.getMessage());
                    }

                    case "jabatan" -> {
                        if (args.length < 4) { sender.sendMessage(MessageHelper.get("organisasi.usage")); return; }
                        String uuid = uuidOf(args[2]);
                        schoolClubManager.removeMember(args[1], args[2], uuid);
                        var r = schoolClubManager.addMember(args[1], args[2], uuid, args[3]);
                        sender.sendMessage(r.isSuccess() ? "§a" + r.getMessage() : "§c" + r.getMessage());
                    }

                    case "lihat" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("organisasi.usage")); return; }
                        var club = schoolClubManager.getClub(args[1]);
                        if (club == null) {
                            sender.sendMessage(MessageHelper.get("organisasi.not_found", "org", args[1]));
                            return;
                        }
                        sender.sendMessage("§6=== " + club.getName() + " ===");
                        sender.sendMessage("§7" + club.getDescription());
                        for (var m : club.getMembers().values()) {
                            sender.sendMessage("§7- §f" + m.getName() + " §7(" + m.getRole() + ")");
                        }
                    }

                    case "daftar" -> {
                        sender.sendMessage("§6=== Daftar Organisasi ===");
                        for (var c : schoolClubManager.listClubs()) {
                            sender.sendMessage("§7- §f" + c.getName());
                        }
                    }

                    case "saya" -> {
                        if (!(sender instanceof Player p)) {
                            sender.sendMessage("§cHanya player yang bisa melihat ini.");
                            return;
                        }
                        String uuid = p.getUniqueId().toString();
                        sender.sendMessage("§6=== Organisasi Kamu ===");
                        boolean found = false;
                        for (var c : schoolClubManager.listClubs()) {
                            if (c.getMembers().containsKey(uuid)) {
                                sender.sendMessage("§7- §f" + c.getName() + " §7(" + c.getMembers().get(uuid).getRole() + ")");
                                found = true;
                            }
                        }
                        if (!found) sender.sendMessage("§7Kamu belum bergabung di organisasi manapun.");
                    }

                    default -> sender.sendMessage(MessageHelper.get("organisasi.usage"));
                }
            })
            .register();
    }
}
