package com.ryushin.schoolplugin.listener;

import com.ryushin.schoolplugin.util.MessageHelper;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

public class PlayerListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    event.getPlayer().sendMessage(MessageHelper.get("welcome"));
  }
}
