// core/AuthManager.java
package net.allenpvp.mypanel.core;

import net.allenpvp.mypanel.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthManager {
    private final Core plugin;
    private final File dataFile;
    private FileConfiguration data;
    private final Map<UUID, String> sessions = new HashMap<>();
    private final Map<UUID, Integer> loginAttempts = new HashMap<>();
    private final Map<UUID, String> pendingRegistrations = new HashMap<>();

    public AuthManager(Core plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "userdata.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addPendingRegistration(Player player, String password) {
        pendingRegistrations.put(player.getUniqueId(), password);
    }
    public boolean confirmRegistration(Player player) {
        String password = pendingRegistrations.get(player.getUniqueId());
        if (password == null) {
            return false;
        }

        boolean result = registerPlayer(player, password);
        if (result) {
            pendingRegistrations.remove(player.getUniqueId());
        }
        return result;
    }
    public boolean registerPlayer(Player player, String password) {
        UUID uuid = player.getUniqueId();

        if (data.contains(uuid.toString())) {
            return false; // 已註冊
        }

        String salt = generateSalt();
        String hashedPassword = hashPassword(salt + password);

        data.set(uuid.toString() + ".salt", salt);
        data.set(uuid.toString() + ".password", hashedPassword);
        saveData();

        return true;
    }

    public boolean authenticatePlayer(String playerName, String password) {
        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) return false;

        UUID uuid = player.getUniqueId();

        // 檢查登入嘗試次數
        int attempts = loginAttempts.getOrDefault(uuid, 0);
        if (attempts >= plugin.getConfigManager().getMaxLoginAttempts()) {
            return false;
        }

        if (!data.contains(uuid.toString())) {
            return false; // 未註冊
        }

        String salt = data.getString(uuid.toString() + ".salt");
        String storedPassword = data.getString(uuid.toString() + ".password");
        String hashedInput = hashPassword(salt + password);

        if (hashedInput.equals(storedPassword)) {
            loginAttempts.remove(uuid);
            createSession(uuid);
            return true;
        } else {
            loginAttempts.put(uuid, attempts + 1);
            return false;
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private void createSession(UUID uuid) {
        String sessionId = generateSalt();
        sessions.put(uuid, sessionId);
    }

    public boolean isAuthenticated(UUID uuid) {
        return sessions.containsKey(uuid);
    }
    public boolean isPlayerRegistered(Player player) {
        return data.contains(player.getUniqueId().toString());
    }
    public String getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public void logout(UUID uuid) {
        sessions.remove(uuid);
    }
}
