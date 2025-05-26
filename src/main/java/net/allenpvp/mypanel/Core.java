// Core.java
package net.allenpvp.mypanel;

import net.allenpvp.mypanel.backend.WebServer;
import net.allenpvp.mypanel.core.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
    private static Core instance;
    private ConfigManager configManager;
    private AuthManager authManager;
    private PanelManager panelManager;
    private WebServer webServer;

    @Override
    public void onEnable() {
        instance = this;

        // 初始化配置
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        // 初始化認證系統
        authManager = new AuthManager(this);

        // 初始化面板系統
        panelManager = new PanelManager(this);

        // 註冊命令和事件
        getCommand("register").setExecutor(new RegisterCommand(authManager));
        getServer().getPluginManager().registerEvents(new PlayerListener(authManager, panelManager), this);
        getCommand("confirm-register").setExecutor(new ConfirmRegisterCommand(authManager));
        getCommand("paneltest").setExecutor(new PanelTestCommand(this));

        // 啟動Web服務器
        if (configManager.isWebEnabled()) {
            webServer = new WebServer(this);
            webServer.start();
        }

        getLogger().info("MyPanel 插件已啟動!");
    }

    @Override
    public void onDisable() {
        if (webServer != null) {
            webServer.stop();
        }
        getLogger().info("MyPanel 插件已關閉!");
    }

    public static Core getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public PanelManager getPanelManager() {
        return panelManager;
    }
}