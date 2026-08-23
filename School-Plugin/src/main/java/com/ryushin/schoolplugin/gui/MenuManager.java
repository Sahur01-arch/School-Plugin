package com.ryushin.schoolplugin.gui;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.ryulib.gui.Gui;
import com.ryushin.ryulib.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MenuManager {

    private final RyuPlugin plugin;

    public MenuManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Gui gui = plugin.guis().create("§8[ Konfigurasi School ]", 3);

        var kelas = ItemBuilder.of(Material.BOOK)
            .name("§aManajemen Kelas")
            .lore("§7Klik untuk membuka /kelas list")
            .build();

        var tugas = ItemBuilder.of(Material.WRITTEN_BOOK)
            .name("§aTugas")
            .lore("§7Klik untuk membuka /tugas")
            .build();

        var koperasi = ItemBuilder.of(Material.CHEST)
            .name("§aKoperasi")
            .lore("§7Klik untuk membuka /koperasi menu")
            .build();

        var organisasi = ItemBuilder.of(Material.EMERALD)
            .name("§aOrganisasi")
            .lore("§7Klik untuk membuka /organisasi daftar")
            .build();

        var report = ItemBuilder.of(Material.PAPER)
            .name("§aRapor / Nilai")
            .lore("§7Klik untuk melihat rapor kamu")
            .build();

        var reload = ItemBuilder.of(Material.ANVIL)
            .name("§eReload Config")
            .lore("§7Klik untuk reload config.yml")
            .build();

        var close = ItemBuilder.of(Material.BARRIER)
            .name("§cTutup")
            .build();

        gui.getInventory().setItem(10, kelas);
        gui.getInventory().setItem(12, tugas);
        gui.getInventory().setItem(14, koperasi);
        gui.getInventory().setItem(16, organisasi);
        gui.getInventory().setItem(28, report);
        gui.getInventory().setItem(22, reload);
        gui.getInventory().setItem(26, close);

        gui.action(10, (p, inv) -> { p.closeInventory(); p.performCommand("kelas list"); });
        gui.action(12, (p, inv) -> { p.closeInventory(); p.performCommand("tugas"); });
        gui.action(14, (p, inv) -> { p.closeInventory(); p.performCommand("koperasi menu"); });
        gui.action(16, (p, inv) -> { p.closeInventory(); p.performCommand("organisasi daftar"); });
        gui.action(28, (p, inv) -> { p.closeInventory(); p.performCommand("report view " + p.getName()); });
        gui.action(22, (p, inv) -> {
            p.closeInventory();
            if (!p.hasPermission("server.menu.reload") && !p.isOp()) {
                p.sendMessage("§cKamu tidak punya izin untuk me-reload config.");
                return;
            }
            plugin.configs().reloadAll();
            p.sendMessage("§eConfig berhasil di-reload!");
        });
        gui.action(26, (p, inv) -> p.closeInventory());

        gui.open(player);
    }
}
