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
        .tab((sender, args) -> args.length == 1 ? List.of("ping", "reload") : List.of())
        .handler((sender, args) -> {
          if (args.length == 0) {
            sender.sendMessage("gunakan /test ping|reload");
            return;
          }

          switch (args[0].toLowerCase()) {
            case "ping" -> {
              sender.sendMessage("pong");
            }
            case "reload" -> {
              plugin.configs().reloadAll();
              sender.sendMessage("§eConfig di reload");
            }
            default -> sender.sendMessage("§cSubCommand tidak dikenal " + args[0]);
          }
        })
        .register();
  }
}
