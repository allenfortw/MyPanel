package top.allenme.mypanel;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import org.json.simple.JSONObject;

public class PlayerManager {
    private final MyPanel plugin;
    private final Map<UUID, JSONObject> playerDataMap;
    private double tps = 20.0; // 預設 TPS
    private long lastTPSUpdate = System.currentTimeMillis();

    public PlayerManager(MyPanel plugin) {
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<>();
        startTPSCalculation();
    }

    private void startTPSCalculation() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            long diff = now - lastTPSUpdate;

            if (diff > 0) {
                // 計算預估的 TPS (每秒觸發次數)
                tps = Math.min(20.0, 1000.0 / diff * 20);
            }

            lastTPSUpdate = now;
        }, 1L, 1L);
    }

    public void updatePlayerData(Player player) {
        plugin.getLogger().info("[PlayerManager] 更新玩家數據: " + player.getName());

        try {
            JSONObject playerData = new JSONObject();
        // 基本資料
        playerData.put("name", player.getName());
        playerData.put("health", player.getHealth());
        playerData.put("maxHealth", player.getMaxHealth());
        playerData.put("foodLevel", player.getFoodLevel());
        playerData.put("level", player.getLevel());
        playerData.put("exp", player.getExp());

        // 位置資訊
        Location loc = player.getLocation();
        JSONObject location = new JSONObject();
        location.put("world", loc.getWorld().getName());
        location.put("x", loc.getX());
        location.put("y", loc.getY());
        location.put("z", loc.getZ());
        playerData.put("location", location);

        // 背包資訊
        JSONObject inventory = new JSONObject();
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                JSONObject item = new JSONObject();
                item.put("type", contents[i].getType().toString());
                item.put("amount", contents[i].getAmount());
                inventory.put(String.valueOf(i), item);
            }
        }
        playerData.put("inventory", inventory);

        // 狀態效果
        JSONObject effects = new JSONObject();
        player.getActivePotionEffects().forEach(effect -> {
            JSONObject potionEffect = new JSONObject();
            potionEffect.put("type", effect.getType().getName());
            potionEffect.put("duration", effect.getDuration());
            potionEffect.put("amplifier", effect.getAmplifier());
            effects.put(effect.getType().getName(), potionEffect);
        });
        playerData.put("effects", effects);

            playerDataMap.put(player.getUniqueId(), playerData);
            plugin.getLogger().info("[PlayerManager] 玩家數據更新成功: " + player.getName());
        } catch (Exception e) {
            plugin.getLogger().severe("[PlayerManager] 更新玩家數據時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public JSONObject getPlayerData(UUID playerId) {
        plugin.getLogger().info("[PlayerManager] 嘗試獲取玩家數據，UUID: " + playerId);

        JSONObject playerData = playerDataMap.get(playerId);
        if (playerData == null) {
            plugin.getLogger().warning("[PlayerManager] 找不到玩家數據，UUID: " + playerId);
            return null;
        }

        plugin.getLogger().info("[PlayerManager] 成功獲取玩家數據");
        return playerData;
    }

    public JSONObject getServerStatus() {
        JSONObject status = new JSONObject();

        // 伺服器基本資訊
        status.put("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        status.put("maxPlayers", plugin.getServer().getMaxPlayers());
        status.put("tps", tps);

        // 伺服器詳細資訊
        JSONObject serverInfo = new JSONObject();
        serverInfo.put("version", plugin.getServer().getVersion());
        serverInfo.put("bukkitVersion", plugin.getServer().getBukkitVersion());
        serverInfo.put("serverName", plugin.getServer().getName());
        status.put("serverInfo", serverInfo);

        // 記憶體使用情況
        JSONObject memoryInfo = new JSONObject();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long allocatedMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        memoryInfo.put("max", maxMemory);
        memoryInfo.put("allocated", allocatedMemory);
        memoryInfo.put("free", freeMemory);
        status.put("memory", memoryInfo);

        // 線上玩家列表
        JSONObject onlinePlayers = new JSONObject();
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            JSONObject playerInfo = new JSONObject();
            playerInfo.put("name", player.getName());
            playerInfo.put("world", player.getWorld().getName());
            onlinePlayers.put(player.getUniqueId().toString(), playerInfo);
        });
        status.put("players", onlinePlayers);

        return status;
    }

    public void cleanupPlayerData(UUID playerId) {
        playerDataMap.remove(playerId);
    }

    public void cleanupAllPlayerData() {
        playerDataMap.clear();
    }
}