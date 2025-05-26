// backend/APIHandler.java
package net.allenpvp.mypanel.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import net.allenpvp.mypanel.Core;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APIHandler {
    private final Core plugin;
    private final Gson gson = new Gson();

    public APIHandler(Core plugin) {
        this.plugin = plugin;
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"方法不允許\"}");
            return;
        }

        try {
            JsonObject request = gson.fromJson(
                    new InputStreamReader(exchange.getRequestBody()), JsonObject.class);

            String playerName = request.get("playerName").getAsString();
            String password = request.get("password").getAsString();

            boolean success = plugin.getAuthManager().authenticatePlayer(playerName, password);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);

            if (success) {
                Player player = plugin.getServer().getPlayer(playerName);
                if (player != null) {
                    String session = plugin.getAuthManager().getSession(player.getUniqueId());
                    response.put("sessionId", session);
                    response.put("message", "登入成功");
                }
            } else {
                response.put("message", "登入失敗：用戶名或密碼錯誤");
            }

            sendResponse(exchange, 200, gson.toJson(response));

        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"請求格式錯誤\"}");
        }
    }

    public void handlePlayerStats(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"方法不允許\"}");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String playerName = extractPlayerName(query);

        if (playerName == null) {
            sendResponse(exchange, 400, "{\"error\":\"缺少玩家名稱參數\"}");
            return;
        }

        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            sendResponse(exchange, 404, "{\"error\":\"玩家未找到\"}");
            return;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("playerName", player.getName());
        stats.put("kills", player.getStatistic(org.bukkit.Statistic.PLAYER_KILLS));
        stats.put("deaths", player.getStatistic(org.bukkit.Statistic.DEATHS));
        stats.put("playTime", player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE) / 20 / 60);

        sendResponse(exchange, 200, gson.toJson(stats));
    }

    public void handlePlayerProfile(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"方法不允許\"}");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String playerName = extractPlayerName(query);

        if (playerName == null) {
            sendResponse(exchange, 400, "{\"error\":\"缺少玩家名稱參數\"}");
            return;
        }

        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            sendResponse(exchange, 404, "{\"error\":\"玩家未找到\"}");
            return;
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("playerName", player.getName());
        profile.put("uuid", player.getUniqueId().toString());
        profile.put("prefix", "&a玩家");
        profile.put("level", 1);
        profile.put("exp", player.getExp());

        sendResponse(exchange, 200, gson.toJson(profile));
    }

    private String extractPlayerName(String query) {
        if (query == null) return null;
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "playerName".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}