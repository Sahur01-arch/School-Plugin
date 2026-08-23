package com.ryushin.schoolplugin;

import com.ryushin.schoolplugin.commands.AdminCommand;
import com.ryushin.schoolplugin.commands.GuiCommand;
import com.ryushin.schoolplugin.commands.KelasCommand;
import com.ryushin.schoolplugin.commands.TugasCommand;
import com.ryushin.schoolplugin.database.DatabaseManager;
import com.ryushin.schoolplugin.listener.PlayerListener;
import com.ryushin.schoolplugin.util.MessageHelper;

import com.ryushin.ryulib.RyuPlugin;

public class SchoolPlugin extends RyuPlugin {

  private DatabaseManager databaseManager;

  @Override
  public void onEnable() {
    MessageHelper.init(this);
    databaseManager = new DatabaseManager(this);
    databaseManager.init();

    info("[SchoolPlugin] Registering /test");
    new AdminCommand(this).register();
    info("[SchoolPlugin] Registering /panduan");
    new GuiCommand(this).register();
    info("[SchoolPlugin] Registering /kelas");
    new KelasCommand(this).register();
    info("[SchoolPlugin] Registering /tugas");
    new TugasCommand(this).register();
    info("[SchoolPlugin] Registering PlayerListener");
    listen(new PlayerListener());
    info("[SchoolPlugin] Berhasil Dijalankan");
  }

  @Override
  public void onDisable() {
    if (databaseManager != null) {
      databaseManager.close();
    }
    info("[SchoolPlugin] Berhasil Dimatikan");
    super.onDisable();
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }
}
