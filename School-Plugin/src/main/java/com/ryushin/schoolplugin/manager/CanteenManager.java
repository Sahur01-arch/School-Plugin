package com.ryushin.schoolplugin.manager;

import com.ryushin.ryulib.RyuPlugin;
import java.util.*;

public class CanteenManager {

    private final RyuPlugin plugin;
    private final Map<String, Integer> balances = new HashMap<>();
    private final Map<String, CanteenItem> menu = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public CanteenManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public int getBalance(String uuid) {
        return balances.getOrDefault(uuid, 0);
    }

    public void setBalance(String uuid, int amount) {
        balances.put(uuid, amount);
    }

    public Result deposit(String uuid, String playerName, int amount) {
        if (amount <= 0) return new Result(false, "Deposit amount must be greater than 0.");
        int current = getBalance(uuid);
        int newBalance = current + amount;
        balances.put(uuid, newBalance);

        transactions.add(new Transaction("DEPOSIT", playerName, uuid, "", amount, newBalance, new Date().toString()));
        return new Result(true, "Successfully deposited " + amount + ". Current balance: " + newBalance);
    }

    public Result withdraw(String uuid, String playerName, int amount) {
        if (amount <= 0) return new Result(false, "Withdrawal amount must be greater than 0.");
        int current = getBalance(uuid);
        if (current < amount) {
            return new Result(false, "Insufficient balance. Your balance: " + current);
        }
        int newBalance = current - amount;
        balances.put(uuid, newBalance);

        transactions.add(new Transaction("WITHDRAW", playerName, uuid, "", amount, newBalance, new Date().toString()));
        return new Result(true, "Successfully withdrew " + amount + ". Current balance: " + newBalance);
    }

    public Result buyItem(String uuid, String playerName, String itemName) {
        if (itemName == null || itemName.isEmpty()) return new Result(false, "Item name cannot be empty.");
        String key = itemName.toLowerCase();
        CanteenItem item = menu.get(key);
        if (item == null) return new Result(false, "Item '" + itemName + "' is not available in canteen.");

        int current = getBalance(uuid);
        if (current < item.price) {
            return new Result(false, "Insufficient balance. Price: " + item.price + ", Balance: " + current);
        }

        int newBalance = current - item.price;
        balances.put(uuid, newBalance);

        transactions.add(new Transaction("BUY", playerName, uuid, item.name, item.price, newBalance, new Date().toString()));
        return new Result(true, "Successfully bought " + item.name + " for " + item.price + ". Balance: " + newBalance);
    }

    public Result addItemMenu(String name, String description, int price) {
        if (name == null || price <= 0) return new Result(false, "Invalid item name or price.");
        String key = name.toLowerCase();
        if (menu.containsKey(key)) return new Result(false, "Item '" + name + "' already exists in menu.");

        menu.put(key, new CanteenItem(name, description != null ? description : "", price));
        return new Result(true, "Item '" + name + "' successfully added to canteen menu.");
    }

    public Result removeItemMenu(String name) {
        if (name == null) return new Result(false, "Item name cannot be empty.");
        String key = name.toLowerCase();
        if (!menu.containsKey(key)) return new Result(false, "Item '" + name + "' not found in menu.");
        menu.remove(key);
        return new Result(true, "Item '" + name + "' successfully removed from menu.");
    }

    public Map<String, CanteenItem> getMenu() {
        return menu;
    }

    public List<Transaction> getTransactionLog(int count) {
        int limit = Math.min(count, transactions.size());
        List<Transaction> result = new ArrayList<>();
        for (int i = transactions.size() - 1; i >= Math.max(0, transactions.size() - limit); i--) {
            result.add(transactions.get(i));
        }
        return result;
    }

    public static class CanteenItem {
        private final String name;
        private final String description;
        private final int price;

        public CanteenItem(String name, String description, int price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getPrice() { return price; }
    }

    public static class Transaction {
        private final String type;
        private final String player;
        private final String uuid;
        private final String item;
        private final int amount;
        private final int balanceAfter;
        private final String date;

        public Transaction(String type, String player, String uuid, String item, int amount, int balanceAfter, String date) {
            this.type = type;
            this.player = player;
            this.uuid = uuid;
            this.item = item;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.date = date;
        }

        public String getType() { return type; }
        public String getPlayer() { return player; }
        public String getItem() { return item; }
        public int getAmount() { return amount; }
        public int getBalanceAfter() { return balanceAfter; }
        public String getDate() { return date; }
    }

    public static class Result {
        private final boolean success;
        private final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
