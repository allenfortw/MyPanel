// core/PanelTestCommand.java
package net.allenpvp.mypanel.core;

import net.allenpvp.mypanel.Core;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PanelTestCommand implements CommandExecutor {
    private final Core plugin;

    public PanelTestCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "web":
                    testWebServer(player);
                    break;
                case "auth":
                    testAuthSystem(player);
                    break;
                case "panel":
                    testPanelSystem(player);
                    break;
                case "url":
                    showWebUrls(player);
                    break;
                default:
                    showHelp(player);
                    break;
            }
        } else {
            showHelp(player);
        }

        return true;
    }

    private void testWebServer(Player player) {
        player.sendMessage("§6=== Web服務器測試 ===");

        if (plugin.getConfigManager().isWebEnabled()) {
            player.sendMessage("§a✓ Web功能已啟用");

            String host = plugin.getConfigManager().getWebHost();
            int port = plugin.getConfigManager().getWebPort();
            String url = "http://" + host + ":" + port;

            player.sendMessage("§7服務器地址: §e" + url);

            // 創建可點擊的鏈接
            TextComponent linkComponent = new TextComponent("§a[點擊訪問Web面板]");
            linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            linkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("點擊在瀏覽器中打開").create()));

            player.spigot().sendMessage(linkComponent);

        } else {
            player.sendMessage("§c✗ Web功能已停用");
        }
    }

    private void testAuthSystem(Player player) {
        player.sendMessage("§6=== 認證系統測試 ===");

        boolean isRegistered = plugin.getAuthManager().isPlayerRegistered(player);
        boolean isAuthenticated = plugin.getAuthManager().isAuthenticated(player.getUniqueId());

        player.sendMessage("§7註冊狀態: " + (isRegistered ? "§a已註冊" : "§c未註冊"));
        player.sendMessage("§7登入狀態: " + (isAuthenticated ? "§a已登入" : "§c未登入"));

        if (!isRegistered) {
            player.sendMessage("§e使用 /register <密碼> 來註冊帳號");
        }
    }

    private void testPanelSystem(Player player) {
        player.sendMessage("§6=== 面板系統測試 ===");

        try {
            plugin.getPanelManager().openPanel(player);
            player.sendMessage("§a✓ 面板系統運行正常");
        } catch (Exception e) {
            player.sendMessage("§c✗ 面板系統出現錯誤: " + e.getMessage());
        }
    }

    private void showWebUrls(Player player) {
        player.sendMessage("§6=== MyPanel 網址資訊 ===");

        String host = plugin.getConfigManager().getWebHost();
        int port = plugin.getConfigManager().getWebPort();
        String baseUrl = "http://" + host + ":" + port;

        player.sendMessage("§7基礎網址: §e" + baseUrl);
        player.sendMessage("§7登入頁面: §e" + baseUrl + "/");
        player.sendMessage("§7面板頁面: §e" + baseUrl + "/panel");
        player.sendMessage("§7API端點: §e" + baseUrl + "/api/");

        // 創建可點擊的鏈接
        TextComponent[] links = {
                createLink("§a[訪問登入頁面]", baseUrl + "/"),
                new TextComponent(" "),
                createLink("§b[訪問面板]", baseUrl + "/panel")
        };

        player.spigot().sendMessage(links);
    }

    private TextComponent createLink(String text, String url) {
        TextComponent component = new TextComponent(text);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("點擊訪問: " + url).create()));
        return component;
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== MyPanel 測試命令 ===");
        player.sendMessage("§7/paneltest web §f- 測試Web服務器");
        player.sendMessage("§7/paneltest auth §f- 測試認證系統");
        player.sendMessage("§7/paneltest panel §f- 測試面板系統");
        player.sendMessage("§7/paneltest url §f- 顯示所有網址");
    }
}