// core/PanelManager.java
package net.allenpvp.mypanel.core;

import net.allenpvp.mypanel.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PanelManager {
    private final Core plugin;

    public PanelManager(Core plugin) {
        this.plugin = plugin;
    }

    public void openPanel(Player player) {
        if (!plugin.getAuthManager().isAuthenticated(player.getUniqueId())) {
            player.sendMessage("§c請先登入後再使用面板功能！");
            return;
        }

        Inventory panel = Bukkit.createInventory(null, 54, "§6MyPanel - 玩家統計");

        // 設置玩家頭像
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta headMeta = head.getItemMeta();
        headMeta.setDisplayName("§a" + player.getName());
        headMeta.setLore(Arrays.asList("§7玩家資訊", "§7等級: §e1"));
        head.setItemMeta(headMeta);
        panel.setItem(4, head);

        // 設置統計數據
        setupStatistics(panel, player);

        // 設置功能按鈕
        setupFunctionButtons(panel);

        player.openInventory(panel);
    }

    private void setupStatistics(Inventory panel, Player player) {
        List<Map<?, ?>> stats = plugin.getConfigManager().getStatistics();

        for (int i = 0; i < Math.min(stats.size(), 3); i++) {
            Map<?, ?> stat = stats.get(i);
            String name = (String) stat.get("name");
            String emoji = (String) stat.get("emoji");
            String source = (String) stat.get("source");

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + emoji + " " + name);

            // 獲取統計數據
            int value = getStatisticValue(player, source);
            meta.setLore(Arrays.asList("§7數值: §a" + value));

            item.setItemMeta(meta);
            panel.setItem(19 + i * 2, item);
        }
    }

    private void setupFunctionButtons(Inventory panel) {
        // 設置功能按鈕 A-F
        String[] functions = {"統計", "設定", "好友", "成就", "商店", "幫助"};
        Material[] materials = {
                Material.BOOK, Material.REDSTONE, Material.EMERALD,
                Material.DIAMOND, Material.GOLD_INGOT, Material.COMPASS
        };

        for (int i = 0; i < functions.length; i++) {
            ItemStack button = new ItemStack(materials[i]);
            ItemMeta meta = button.getItemMeta();
            meta.setDisplayName("§6" + functions[i]);
            meta.setLore(Arrays.asList("§7點擊查看詳細資訊"));
            button.setItemMeta(meta);
            panel.setItem(45 + i, button);
        }
    }

    private int getStatisticValue(Player player, String source) {
        // 根據source獲取對應的統計數據
        switch (source) {
            case "player_kills":
                return player.getStatistic(org.bukkit.Statistic.PLAYER_KILLS);
            case "player_deaths":
                return player.getStatistic(org.bukkit.Statistic.DEATHS);
            case "play_time":
                return player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE) / 20 / 60; // 轉換為分鐘
            default:
                return 0;
        }
    }
}