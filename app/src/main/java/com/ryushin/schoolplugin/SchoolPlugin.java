package com.ryushin.schoolplugin;

import com.ryushin.schoolplugin.commands.AdminCommand;
import org.bukkit.plugin.java.JavaPlugin;
import com.ryushin.ryulib.RyuPlugin;

public class SchoolPlugin extends RyuPlugin {

  @Override
  public void onEnable() {
    new AdminCommand(this).register();
    info("[SchoolPlugin] Berhasil Dijalankan");
  }
  
  @Override
  public void onDisable() {
    info("[SchoolPlugin] Berhasil Dimatikan");
    super.onDisable();
  }
}
