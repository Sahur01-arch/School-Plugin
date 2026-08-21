package com.ryushin.schoolplugin;

import com.ryushin.schoolplugin.commands.AdminCommand;
import com.ryushin.schoolplugin.commands.GuiCommand;
import com.ryushin.schoolplugin.listener.PlayerListener;

import org.bukkit.entity.Player;

import com.ryushin.ryulib.RyuPlugin;

public class SchoolPlugin extends RyuPlugin {

  @Override
  public void onEnable() {
    info("[SchoolPlugin] Registering /test");
    new AdminCommand(this).register();
    info("[SchoolPlugin] Registering /panduan");
    new GuiCommand(this).register();
    info("[SchoolPlugin] Registering PlayerListener");
    listen(new PlayerListener());
    info("[SchoolPlugin] Berhasil Dijalankan");
  }

  @Override
  public void onDisable() {
    info("[SchoolPlugin] Berhasil Dimatikan");
    super.onDisable();
  }
}
