package com.ryushin.schoolplugin.commands;

import com.ryushin.ryulib.RyuPlugin;
import com.ryushin.schoolplugin.manager.CanteenManager;
import com.ryushin.schoolplugin.util.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KoperasiCommand {

    private final RyuPlugin plugin;
    private final CanteenManager canteenManager;

    public KoperasiCommand(RyuPlugin plugin) {
        this.plugin = plugin;
        this.canteenManager = new CanteenManager(plugin);
    }

    private int parse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    public void register() {
        plugin.commandManager().command("koperasi")
            .permission("server.koperasi", MessageHelper.get("access_denied"))
            .usage("/koperasi <saldo|deposit|tarik|beli|menu|tambahmenu|hapusmenu|riwayat>")
            .tab((sender, args) -> args.length == 1
                ? List.of("saldo", "deposit", "tarik", "beli", "menu", "tambahmenu", "hapusmenu", "riwayat")
                : List.of())
            .handler((sender, args) -> {
                if (args.length == 0) {
                    sender.sendMessage(MessageHelper.get("koperasi.usage"));
                    return;
                }

                String uuid = sender instanceof Player p ? p.getUniqueId().toString() : "console";
                String name = sender instanceof Player p ? p.getName() : "Console";

                switch (args[0].toLowerCase()) {
                    case "saldo" -> sender.sendMessage(
                        MessageHelper.get("koperasi.balance", "balance", String.valueOf(canteenManager.getBalance(uuid))));

                    case "deposit" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("koperasi.usage")); return; }
                        int amount = parse(args[1]);
                        if (amount <= 0) { sender.sendMessage(MessageHelper.get("koperasi.invalid_amount")); return; }
                        canteenManager.deposit(uuid, name, amount);
                        sender.sendMessage(MessageHelper.get("koperasi.deposit_success",
                            "amount", args[1], "balance", String.valueOf(canteenManager.getBalance(uuid))));
                    }

                    case "tarik" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("koperasi.usage")); return; }
                        int amount = parse(args[1]);
                        if (amount <= 0) { sender.sendMessage(MessageHelper.get("koperasi.invalid_amount")); return; }
                        var w = canteenManager.withdraw(uuid, name, amount);
                        if (w.isSuccess()) {
                            sender.sendMessage(MessageHelper.get("koperasi.withdraw_success",
                                "amount", args[1], "balance", String.valueOf(canteenManager.getBalance(uuid))));
                        } else {
                            sender.sendMessage(MessageHelper.get("koperasi.insufficient_funds",
                                "balance", String.valueOf(canteenManager.getBalance(uuid))));
                        }
                    }

                    case "beli" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("koperasi.usage")); return; }
                        String item = args[1];
                        var buy = canteenManager.buyItem(uuid, name, item);
                        if (buy.isSuccess()) {
                            var itemObj = canteenManager.getMenu().get(item.toLowerCase());
                            String price = itemObj != null ? String.valueOf(itemObj.getPrice()) : "?";
                            sender.sendMessage(MessageHelper.get("koperasi.buy_success",
                                "item", item, "price", price, "balance", String.valueOf(canteenManager.getBalance(uuid))));
                        } else {
                            sender.sendMessage(MessageHelper.get("koperasi.item_not_found", "item", item));
                        }
                    }

                    case "menu" -> {
                        sender.sendMessage("§6=== Menu Koperasi ===");
                        for (var entry : canteenManager.getMenu().entrySet()) {
                            var item = entry.getValue();
                            sender.sendMessage("§7- §f" + item.getName() + " §7(§e" + item.getPrice() + "§7)");
                        }
                    }

                    case "tambahmenu" -> {
                        if (args.length < 3) { sender.sendMessage(MessageHelper.get("koperasi.usage")); return; }
                        int price = parse(args[2]);
                        if (price <= 0) { sender.sendMessage(MessageHelper.get("koperasi.invalid_amount")); return; }
                        String desc = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : "";
                        var r = canteenManager.addItemMenu(args[1], desc, price);
                        sender.sendMessage(r.isSuccess() ? "§a" + r.getMessage() : "§c" + r.getMessage());
                    }

                    case "hapusmenu" -> {
                        if (args.length < 2) { sender.sendMessage(MessageHelper.get("koperasi.usage")); return; }
                        var r = canteenManager.removeItemMenu(args[1]);
                        sender.sendMessage(r.isSuccess() ? "§a" + r.getMessage() : "§c" + r.getMessage());
                    }

                    case "riwayat" -> {
                        int count = args.length > 1 ? parse(args[1]) : 5;
                        if (count <= 0) count = 5;
                        sender.sendMessage("§6=== Riwayat Transaksi ===");
                        for (var t : canteenManager.getTransactionLog(count)) {
                            sender.sendMessage("§7[" + t.getType() + "] §f" + t.getPlayer()
                                + " §e" + t.getAmount() + " §7(" + t.getDate() + ")");
                        }
                    }

                    default -> sender.sendMessage(MessageHelper.get("koperasi.usage"));
                }
            })
            .register();
    }
}
