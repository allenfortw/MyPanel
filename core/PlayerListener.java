// core/PlayerListener.java
package net.allenpvp.mypanel.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final AuthManager authManager;
    private final PanelManager panelManager;

    public PlayerListener(AuthManager authManager, PanelManager panelManager) {
        this.authManager = authManager;
        this.panelManager = panelManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家加入時的處理
        event.getPlayer().sendMessage("§6歡迎來到伺服器！使用 /register <密碼> 來註冊帳號。");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 右鍵打開面板的處理
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("COMPASS")) {
                panelManager.openPanel(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("MyPanel")) {
            event.setCancelled(true);
            // 處理面板點擊事件
        }
    }
}