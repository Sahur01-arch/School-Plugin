package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import org.bukkit.entity.Player;
import java.util.List;

public class AdminCommand {

  private final RyuPlugin plugin;

  public AdminCommand(RyuPlugin plugin) {
    this.plugin = plugin;
  }

  public void register() {
    plugin.commandManager().command("test")
      .permission("server.admin", "§cAkses Ditolak")
      .usage("/test")
      .handler((sender, args) -> {
        sender.sendMessage("Pong!");
      })
      .register();
  }
}
