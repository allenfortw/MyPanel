// core/ConfigManager.java
package net.allenpvp.mypanel.core;

import net.allenpvp.mypanel.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final Core plugin;
    private FileConfiguration config;
    private FileConfiguration statistics;

    public ConfigManager(Core plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // 加載主配置
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        // 加載統計配置
        File statsFile = new File(plugin.getDataFolder(), "statistics.yml");
        if (!statsFile.exists()) {
            plugin.saveResource("statistics.yml", false);
        }
        statistics = YamlConfiguration.loadConfiguration(statsFile);
    }

    public boolean isWebEnabled() {
        return config.getBoolean("web.enabled", true);
    }

    public int getWebPort() {
        return config.getInt("web.port", 8080);
    }

    public String getWebHost() {
        return config.getString("web.host", "0.0.0.0");
    }

    public int getSessionTimeout() {
        return config.getInt("security.session_timeout", 3600);
    }

    public int getMaxLoginAttempts() {
        return config.getInt("security.max_login_attempts", 5);
    }

    public List<Map<?, ?>> getStatistics() {
        return statistics.getMapList("statistics");
    }
}