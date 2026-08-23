package com.ryushin.schoolplugin.manager;

import com.ryushin.ryulib.RyuPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class AssignmentManager {

    private final RyuPlugin plugin;
    private static final int CHEST_SIZE = 54;
    private final Map<String, ItemStack[]> classInventories = new HashMap<>();

    public AssignmentManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory getAssignmentChest(String className) {
        Inventory inv = Bukkit.createInventory(null, CHEST_SIZE, "§6AssignmentChest-" + className);
        ItemStack[] contents = classInventories.get(className.toLowerCase());
        if (contents != null) {
            inv.setContents(contents);
        }
        return inv;
    }

    public void saveAssignmentChest(String className, Inventory inv) {
        classInventories.put(className.toLowerCase(), inv.getContents());
    }

    public AssignmentSubmitResult submitAssignment(Player player, String className) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            return new AssignmentSubmitResult(false, "You are not holding any book.");
        }

        Material type = itemInHand.getType();
        if (type != Material.WRITTEN_BOOK && type != Material.WRITABLE_BOOK) {
            return new AssignmentSubmitResult(false, "Item in hand is not a Book and Quill or Written Book.");
        }

        if (type == Material.WRITABLE_BOOK) {
            return new AssignmentSubmitResult(false, "Book is not signed yet. Sign it before submitting.");
        }

        Inventory inv = getAssignmentChest(className);
        int emptySlot = inv.firstEmpty();

        if (emptySlot == -1) {
            return new AssignmentSubmitResult(false, "Assignment chest for " + className + " is full. Contact teacher.");
        }

        BookMeta bookMeta = (BookMeta) itemInHand.getItemMeta();
        String assignmentTitle = (bookMeta != null && bookMeta.hasTitle()) ? bookMeta.getTitle() : "(Untitled)";

        if (bookMeta != null) {
            List<String> lore = bookMeta.hasLore() ? bookMeta.getLore() : new ArrayList<>();
            lore.add("§7Submitted by: §f" + player.getName());
            lore.add("§7Time: §f" + new Date().toLocaleString());
            bookMeta.setLore(lore);
            itemInHand.setItemMeta(bookMeta);
        }

        ItemStack itemToSave = itemInHand.clone();
        itemToSave.setAmount(1);

        inv.setItem(emptySlot, itemToSave);
        saveAssignmentChest(className, inv);

        int currentAmount = itemInHand.getAmount();
        if (currentAmount <= 1) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            itemInHand.setAmount(currentAmount - 1);
            player.getInventory().setItemInMainHand(itemInHand);
        }

        return new AssignmentSubmitResult(true, "Assignment " + assignmentTitle + " successfully submitted to " + className + ".");
    }

    public void openChestForTeacher(Player teacher, String className) {
        Inventory inv = getAssignmentChest(className);
        teacher.openInventory(inv);
    }

    public static class AssignmentSubmitResult {
        private final boolean success;
        private final String message;

        public AssignmentSubmitResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
