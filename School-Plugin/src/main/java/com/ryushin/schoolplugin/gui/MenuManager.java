package com.ryushin.schoolplugin.gui;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.ryulib.gui.Gui;
import com.ryushin.ryulib.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.ryushin.schoolplugin.manager.BookGuide;

public class MenuManager {

  private final RyuPlugin plugin;

  public MenuManager(RyuPlugin plugin) {
    this.plugin = plugin;
  }

  public void open(Player player) {

    Gui gui = plugin.guis().create("§8[ MENU ]", 3);

    ItemStack panduanBook = ItemBuilder.of(Material.WRITABLE_BOOK)
        .name("§aPanduan")
        .lore("§7Klik Untuk Menampilkan Panduan")
        .build();

    ItemStack closeItem = ItemBuilder.of(Material.BARRIER)
        .name("§cTutup Menu")
        .build();

    gui.getInventory().setItem(11, panduanBook);
    gui.getInventory().setItem(13, closeItem);

    gui.action(11, (p, inv) -> {
      player.sendMessage("§a[School] Membuka Panduan....");
      player.closeInventory();
      BookGuide.open(player);
    });

    gui.action(13, (p, inv) -> player.closeInventory());
  }
}
