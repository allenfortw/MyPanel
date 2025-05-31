package top.allenme.mypanel;

import org.bukkit.entity.Player;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.util.Base64;

public class TokenManager {
    private final Map<String, TokenInfo> tokenMap;
    private final SecureRandom secureRandom;

    public TokenManager() {
        this.tokenMap = new ConcurrentHashMap<>();
        this.secureRandom = new SecureRandom();
    }

    public String generateToken(Player player) {
        cleanExpiredTokens();
        byte[] randomBytes = new byte[384];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // 將有效期改為 24 小時，而不是 1 小時
        TokenInfo tokenInfo = new TokenInfo(player.getUniqueId(), Instant.now().plusSeconds(86400));
        tokenMap.put(token, tokenInfo);

        return token;
    }

    public boolean validateToken(String token) {
        TokenInfo info = tokenMap.get(token);
        if (info == null) {
            return false;
        }

        if (info.getExpiration().isBefore(Instant.now())) {
            tokenMap.remove(token);
            return false;
        }

        return true;
    }

    private void cleanExpiredTokens() {
        Instant now = Instant.now();
        tokenMap.entrySet().removeIf(entry -> entry.getValue().getExpiration().isBefore(now));
    }

    private static class TokenInfo {
        private final java.util.UUID playerId;
        private final Instant expiration;

        public TokenInfo(java.util.UUID playerId, Instant expiration) {
            this.playerId = playerId;
            this.expiration = expiration;
        }

        public java.util.UUID getPlayerId() {
            return playerId;
        }

        public Instant getExpiration() {
            return expiration;
        }
    }
    public UUID getPlayerIdFromToken(String token) {
        plugin.getLogger().info("[TokenManager] 驗證 Token: " + token.substring(0, 10) + "...");

        TokenInfo info = tokenMap.get(token);
        if (info == null) {
            plugin.getLogger().warning("[TokenManager] Token 不存在");
            return null;
        }

        if (info.getExpiration().isBefore(Instant.now())) {
            plugin.getLogger().warning("[TokenManager] Token 已過期");
            tokenMap.remove(token);
            return null;
        }

        plugin.getLogger().info("[TokenManager] Token 驗證成功");
        return info.getPlayerId();
    }
}