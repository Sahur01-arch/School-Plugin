package com.ryushin.schoolplugin.manager;

import com.ryushin.ryulib.RyuPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClassroomManager {

    private final RyuPlugin plugin;
    private final List<String> schoolClasses = Arrays.asList("jurusan_build", "jurusan_redstone", "jurusan_pertanian", "dkv", "kelasa", "kelasb", "kelasc", "kelasd");

    public ClassroomManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public List<String> getSchoolClasses() {
        return schoolClasses;
    }

    public void addGroup(String name, int weight) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                LuckPerms lp = LuckPermsProvider.get();
                Group group = lp.getGroupManager().createAndLoadGroup(name).join();
                if (group != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp creategroup " + name);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp group " + name + " setweight " + weight);
                    });
                    plugin.info("Group " + name + " created with weight " + weight);
                }
            } catch (Exception e) {
                plugin.error("Failed to create LuckPerms group " + name + ": " + e.getMessage());
            }
        });
    }

    public void removeGroup(String name, Player sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                LuckPerms lp = LuckPermsProvider.get();
                Group group = lp.getGroupManager().getGroup(name);
                if (group == null) {
                    if (sender != null) {
                        Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage("§cGroup " + name + " Not Found"));
                    }
                    return;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp deletegroup " + name);
                    if (sender != null) {
                        sender.sendMessage("§aGroup " + name + " successfully removed.");
                    }
                });
            } catch (Exception e) {
                if (sender != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage("§cFailed to remove group: " + e.getMessage()));
                }
            }
        });
    }

    public String getStudentClass(String uuidString) {
        try {
            LuckPerms lp = LuckPermsProvider.get();
            UUID uuid = UUID.fromString(uuidString);
            User user = lp.getUserManager().getUser(uuid);
            if (user == null) {
                CompletableFuture<User> future = lp.getUserManager().loadUser(uuid);
                user = future.join();
            }
            if (user != null) {
                for (var node : user.getNodes()) {
                    if (node.getType() == NodeType.INHERITANCE) {
                        InheritanceNode in = (InheritanceNode) node;
                        String groupName = in.getGroupName();
                        if (schoolClasses.contains(groupName.toLowerCase())) {
                            return groupName;
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.error("Failed to get class for UUID: " + uuidString + " - " + e.getMessage());
        }
        return null;
    }

    public ClassCheckResult validateStudentClass(Player player) {
        if (player.isOp() || player.hasPermission("under.tugas.bypass")) {
            return new ClassCheckResult(true, "STAFF", "");
        }

        String uuidString = player.getUniqueId().toString();
        String groupName = getStudentClass(uuidString);

        if (groupName == null || groupName.equalsIgnoreCase("default")) {
            return new ClassCheckResult(false, null, "You are not enrolled in any class.");
        }

        if (!schoolClasses.contains(groupName.toLowerCase())) {
            return new ClassCheckResult(false, null, "Your group ('" + groupName + "') is not a valid class.");
        }

        return new ClassCheckResult(true, groupName, "");
    }

    public static class ClassCheckResult {
        private final boolean valid;
        private final String className;
        private final String message;

        public ClassCheckResult(boolean valid, String className, String message) {
            this.valid = valid;
            this.className = className;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getClassName() { return className; }
        public String getMessage() { return message; }
    }
}
