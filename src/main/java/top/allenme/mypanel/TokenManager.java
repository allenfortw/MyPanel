package top.allenme.mypanel;

import org.bukkit.entity.Player;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Base64;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

public class TokenManager {
    private final Map<String, TokenInfo> tokenMap;
    private final SecureRandom secureRandom;
    private final MyPanel plugin;
    private static final long TOKEN_VALIDITY_DURATION = 86400; // 24 hours in seconds

    public TokenManager(MyPanel plugin) {
        this.plugin = plugin;
        this.tokenMap = new ConcurrentHashMap<>();
        this.secureRandom = new SecureRandom();
    }

    public String generateToken(Player player) {
        cleanExpiredTokens();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        TokenInfo tokenInfo = new TokenInfo(player.getUniqueId(),
                Instant.now().plusSeconds(TOKEN_VALIDITY_DURATION));
        tokenMap.put(token, tokenInfo);

        plugin.getLogger().info("Generated new token for player: " + player.getName());
        return token;
    }

    public UUID getPlayerIdFromToken(String token) {
        TokenInfo info = tokenMap.get(token);
        if (info == null) {
            plugin.getLogger().warning("Token not found: " + token.substring(0, 10) + "...");
            return null;
        }

        if (info.getExpiration().isBefore(Instant.now())) {
            plugin.getLogger().warning("Token expired for player: " + info.getPlayerId());
            tokenMap.remove(token);
            return null;
        }

        return info.getPlayerId();
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
        private final UUID playerId;
        private final Instant expiration;

        public TokenInfo(UUID playerId, Instant expiration) {
            this.playerId = playerId;
            this.expiration = expiration;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public Instant getExpiration() {
            return expiration;
        }
    }
}