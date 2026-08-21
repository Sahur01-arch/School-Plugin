package com.ryushin.schoolplugin.gui;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.ryulib.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.ryushin.ryulib.gui.Gui;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class KelasGui {

  private final RyuPlugin plugin;

  public KelasGui(RyuPlugin plugin) {
    this.plugin = plugin;
  }

  private ItemStack buildClockItem() {
    String waktu = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    return ItemBuilder.of(Material.CLOCK)
        .name("§eWaktu Server")
        .lore("7" + waktu)
        .build();
  }

  public void open() {
    Gui gui = plugin.guis().create("§aMenu Kelas", 5);

  }
}
