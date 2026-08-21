package com.ryushin.schoolplugin.listener;

import com.ryushin.ryulib.RyuPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventHandler;

public class PlayerListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    event.getPlayer().sendMessage("Selamat Datang Di Server");
  }
}
