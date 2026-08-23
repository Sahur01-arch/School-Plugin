package com.ryushin.schoolplugin.util;

import com.ryushin.ryulib.RyuPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageHelper {

    private static RyuPlugin pluginInstance;

    public static void init(RyuPlugin plugin) {
        pluginInstance = plugin;
        plugin.saveDefaultConfig();
    }

    public static String get(String path, String... replacements) {
        if (pluginInstance == null) return path;
        FileConfiguration config = pluginInstance.getConfig();
        String prefix = config.getString("prefix", "§8[§bSchool§8] §r");
        String message = config.getString("messages." + path, path);

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public static String getRaw(String path, String... replacements) {
        if (pluginInstance == null) return path;
        FileConfiguration config = pluginInstance.getConfig();
        String message = config.getString("messages." + path, path);

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
