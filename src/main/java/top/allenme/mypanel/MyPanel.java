package top.allenme.mypanel;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MyPanel extends JavaPlugin {
    private WebServer webServer;
    private PlayerManager playerManager;
    private TokenManager tokenManager;
    private static Logger logger;

    // 內部類別：事件監聽器
    private class EventListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            playerManager.updatePlayerData(event.getPlayer());
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            playerManager.cleanupPlayerData(event.getPlayer().getUniqueId());
        }

        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            if (event.getFrom().getWorld() != event.getTo().getWorld() ||
                    event.getFrom().distanceSquared(event.getTo()) > 1) {
                playerManager.updatePlayerData(player);
            }
        }

        @EventHandler
        public void onPlayerDamage(EntityDamageEvent event) {
            if (event.getEntity() instanceof Player) {
                playerManager.updatePlayerData((Player) event.getEntity());
            }
        }

        @EventHandler
        public void onFoodLevelChange(FoodLevelChangeEvent event) {
            if (event.getEntity() instanceof Player) {
                playerManager.updatePlayerData((Player) event.getEntity());
            }
        }
    }

    @Override
    public void onEnable() {
        // 創建必要的目錄
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File logsDir = new File(getDataFolder(), "logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }

        // 載入配置文件
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // 初始化組件
        logger = getLogger();
        playerManager = new PlayerManager(this);
        tokenManager = new TokenManager(this);  // 修改這裡，傳入 this

        // 啟動網頁服務器
        String bindIp = config.getString("web.bind-ip", "localhost");
        int port = config.getInt("web.port", 8080);

        try {
            webServer = new WebServer(this, bindIp, port);
            webServer.start();
            logger.info("Web server started successfully on " + bindIp + ":" + port);
        } catch (IOException e) {
            logger.severe("Failed to start web server: " + e.getMessage());
            logger.severe("Make sure port " + port + " is available and not in use");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } catch (Exception e) {
            logger.severe("Unexpected error while starting web server: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 註冊事件監聽器
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                playerManager.updatePlayerData(player);
            }
        }, 20L, 20L);

        logger.info("MyPanel has been enabled!");
    }

    @Override
    public void onDisable() {
        if (webServer != null) {
            try {
                webServer.stop();
                logger.info("Web server stopped successfully");
            } catch (Exception e) {
                logger.warning("Error while stopping web server: " + e.getMessage());
            }
        }

        // 清理玩家數據
        if (playerManager != null) {
            playerManager.cleanupAllPlayerData();
        }

        logger.info("MyPanel has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c此指令只能由玩家執行！");
            return true;
        }

        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "server":
                try {
                    String serverUrl = "http://" + webServer.getBindAddress() + ":" + webServer.getPort() + "/";
                    // 使用 JSON 格式發送可點擊的連結
                    player.spigot().sendMessage(
                            net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                                    "§a伺服器狀態頁面: §n" + serverUrl
                            )
                    );
                } catch (Exception e) {
                    player.sendMessage("§c無法取得伺服器網址，請聯繫管理員！");
                    logger.warning("Error while getting server URL: " + e.getMessage());
                }
                break;

            case "player":
                try {
                    String token = tokenManager.generateToken(player);
                    String playerUrl = "http://" + webServer.getBindAddress() + ":" +
                            webServer.getPort() + "/player?token=" + token;
                    // 使用 JSON 格式發送可點擊的連結
                    net.md_5.bungee.api.chat.TextComponent message = new net.md_5.bungee.api.chat.TextComponent(
                            "§a您的個人頁面: §n" + playerUrl
                    );
                    message.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(
                            net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL,
                            playerUrl
                    ));
                    player.spigot().sendMessage(message);

                    // 發送過期提醒（純文字）
                    player.sendMessage("§e此連結將在1小時後失效");
                } catch (Exception e) {
                    player.sendMessage("§c無法生成個人頁面，請聯繫管理員！");
                    logger.warning("Error while generating player page: " + e.getMessage());
                }
                break;

            default:
                return false;
        }

        return true;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public static void log(String message) {
        if (logger != null) {
            logger.info(message);
        }
    }
}