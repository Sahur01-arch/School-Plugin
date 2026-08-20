package com.ryushin.schoolplugin;

import org.bukkit.plugin.java.JavaPlugin;
import com.ryushin.ryulib.RyuPlugin;

public class SchoolPlugin extends JavaPlugin {

  @Override
  public onEnable() {
    getLogger.("[SchoolPlugin] Berhasil Dijalankan");
  }
  
  @Override
  public onDisable() {
    getLogger.info("[SchoolPlugin] Berhasil Dimatikan");
  }
}
