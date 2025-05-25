package net.allenpvp.mypanel.core;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private final AuthManager authManager;

    public RegisterCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("§c用法: /register <密碼>");
            return true;
        }

        String password = args[0];

        if (!authManager.isPlayerRegistered(player)) {
            authManager.addPendingRegistration(player, password);

            // 顯示確認按鈕
            player.sendMessage("§6=== 註冊確認 ===");
            player.sendMessage("§7玩家名稱: §a" + player.getName());
            player.sendMessage("§7密碼: §a" + password);

            // 創建可點擊的確認按鈕
            TextComponent confirmButton = new TextComponent("§a[確認密碼]");
            confirmButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/confirm-register"));
            confirmButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("點擊確認註冊").create()));

            player.spigot().sendMessage(confirmButton);
        } else {
            player.sendMessage("§c註冊失敗！您可能已經註冊過了。");
        }

        return true;
    }
}