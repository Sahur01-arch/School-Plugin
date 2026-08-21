package com.ryushin.schoolplugin.manager;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.ryulib.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookGuide {

  public static void open(Player player) {
    ItemStack book = ItemBuilder.of(Material.WRITABLE_BOOK).build();
    BookMeta meta = (BookMeta) book.getItemMeta();

    if (meta != null) {
      meta.setTitle("§bPanduan School");
      meta.setAuthor("§cOwner");

      meta.addPage(
          "§a§lBUKU PANDUAN\n" +
              "§7================\n\n" +
              "§rGeser ke Halaman Selanjutnya\n" +
              "Untuk Cek Berbagai Panduan\n" +
              ":v");

      meta.addPage(
          "Test");

      book.setItemMeta(meta);
    }

    player.openBook(book);
  }
}
