package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import org.bukkit.entity.Player;
import java.util.List;
import com.ryushin.schoolplugin.gui.MenuManager;

public class GuiCommand {
  private final RyuPlugin plugin;
  private final MenuManager menuManager;

  public GuiCommand(RyuPlugin plugin) {
    this.plugin = plugin;
    this.menuManager = new MenuManager(plugin);
  }

  public void register() {
    plugin.commandManager().command("panduan")
        .handler((sender, args) -> {
          if (!(sender instanceof Player player)) {
            sender.sendMessage("§cHanya Player Yang Bisa Menggunakan nya!!");
            return;
          }

          try {
            menuManager.open((Player) sender);
          } catch (Exception e) {
            sender.sendMessage("§cTerjadi Error: " + e);
            return;
          }
        })
        .register();
  }
}
