package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.gui.MenuManager;
import org.bukkit.entity.Player;

public class MenuCommand {

    private final RyuPlugin plugin;
    private final MenuManager menuManager;

    public MenuCommand(RyuPlugin plugin) {
        this.plugin = plugin;
        this.menuManager = new MenuManager(plugin);
    }

    public void register() {
        plugin.commandManager().command("menu")
            .permission("server.menu", "§cAkses Ditolak")
            .usage("/menu")
            .handler((sender, args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cHanya Player yang bisa membuka menu.");
                    return;
                }
                menuManager.open(player);
            })
            .register();
    }
}
