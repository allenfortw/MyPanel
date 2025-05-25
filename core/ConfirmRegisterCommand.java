package net.allenpvp.mypanel.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfirmRegisterCommand implements CommandExecutor {
    private final AuthManager authManager;

    public ConfirmRegisterCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (authManager.confirmRegistration(player)) {
            player.sendMessage("§a註冊成功！請使用您的密碼登入。");
        } else {
            player.sendMessage("§c註冊失敗！請重新執行註冊命令。");
        }

        return true;
    }
}