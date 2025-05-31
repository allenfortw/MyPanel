package top.allenme.mypanel;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import org.json.simple.JSONObject;

public class PlayerManager {
    private final MyPanel plugin;
    private final Map<UUID, JSONObject> playerDataMap;
    private BukkitTask updateTask;
    private static final long UPDATE_INTERVAL = 20L; // 1 second in ticks

    public PlayerManager(MyPanel plugin) {
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<>();
        startAutoUpdate();
    }

    private void startAutoUpdate() {
        // Cancel any existing task
        if (updateTask != null) {
            updateTask.cancel();
        }

        // Start new update task
        updateTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                updatePlayerData(player);
            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }

    public void updatePlayerData(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        try {
            JSONObject playerData = new JSONObject();

            // Basic info
            playerData.put("name", player.getName());
            playerData.put("health", player.getHealth());
            playerData.put("maxHealth", player.getMaxHealth());
            playerData.put("foodLevel", player.getFoodLevel());
            playerData.put("level", player.getLevel());
            playerData.put("exp", player.getExp());

            // Location
            Location loc = player.getLocation();
            JSONObject location = new JSONObject();
            location.put("world", loc.getWorld().getName());
            location.put("x", loc.getX());
            location.put("y", loc.getY());
            location.put("z", loc.getZ());
            location.put("yaw", loc.getYaw());
            location.put("pitch", loc.getPitch());
            playerData.put("location", location);

            // Inventory
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

            // Effects
            JSONObject effects = new JSONObject();
            player.getActivePotionEffects().forEach(effect -> {
                JSONObject potionEffect = new JSONObject();
                potionEffect.put("type", effect.getType().getName());
                potionEffect.put("duration", effect.getDuration());
                potionEffect.put("amplifier", effect.getAmplifier());
                effects.put(effect.getType().getName(), potionEffect);
            });
            playerData.put("effects", effects);

            // Update the map
            playerDataMap.put(player.getUniqueId(), playerData);

        } catch (Exception e) {
            plugin.getLogger().severe("Error updating player data for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public JSONObject getPlayerData(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    public JSONObject getServerStatus() {
        JSONObject status = new JSONObject();

        // Server info
        status.put("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        status.put("maxPlayers", plugin.getServer().getMaxPlayers());
        status.put("tps", estimateServerTPS());

        // Server details
        JSONObject serverInfo = new JSONObject();
        serverInfo.put("version", plugin.getServer().getVersion());
        serverInfo.put("bukkitVersion", plugin.getServer().getBukkitVersion());
        serverInfo.put("name", plugin.getServer().getName());
        status.put("serverInfo", serverInfo);

        // Memory info
        JSONObject memoryInfo = new JSONObject();
        Runtime runtime = Runtime.getRuntime();
        memoryInfo.put("max", runtime.maxMemory() / 1024 / 1024);
        memoryInfo.put("allocated", runtime.totalMemory() / 1024 / 1024);
        memoryInfo.put("free", runtime.freeMemory() / 1024 / 1024);
        status.put("memory", memoryInfo);

        // Online players
        JSONObject players = new JSONObject();
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            JSONObject playerInfo = new JSONObject();
            playerInfo.put("name", player.getName());
            playerInfo.put("world", player.getWorld().getName());
            players.put(player.getUniqueId().toString(), playerInfo);
        });
        status.put("players", players);

        return status;
    }

    private double estimateServerTPS() {
        return Math.min(20.0, 20.0); // 簡單實現，實際可能需要更複雜的計算
    }

    public void cleanup() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        playerDataMap.clear();
    }
}