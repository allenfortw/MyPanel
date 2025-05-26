// backend/WebRouteHandler.java
package net.allenpvp.mypanel.backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.allenpvp.mypanel.Core;
import net.allenpvp.mypanel.frontend.HTMLGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class WebRouteHandler implements HttpHandler {
    private final Core plugin;

    public WebRouteHandler(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // 設置CORS headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        // 處理OPTIONS預檢請求
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            switch (path) {
                case "/":
                case "/index.html":
                case "/login":
                    handleLoginPage(exchange);
                    break;
                case "/panel":
                case "/panel.html":
                case "/dashboard":
                    handlePanelPage(exchange);
                    break;
                case "/logout":
                    handleLogout(exchange);
                    break;
                default:
                    // 如果不是特定路由，返回404
                    handle404(exchange);
                    break;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("處理Web請求時發生錯誤: " + e.getMessage());
            handle500(exchange, e);
        }
    }

    private void handleLoginPage(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String html = HTMLGenerator.generateLoginPage();
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        sendResponse(exchange, 200, html);

        plugin.getLogger().info("提供登入頁面給 " + exchange.getRemoteAddress().getAddress().getHostAddress());
    }

    private void handlePanelPage(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String html = HTMLGenerator.generatePanelPage();
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        sendResponse(exchange, 200, html);

        plugin.getLogger().info("提供面板頁面給 " + exchange.getRemoteAddress().getAddress().getHostAddress());
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        // 清除session並重定向到登入頁面
        String query = exchange.getRequestURI().getQuery();
        String playerName = extractPlayerName(query);

        if (playerName != null) {
            org.bukkit.entity.Player player = plugin.getServer().getPlayer(playerName);
            if (player != null) {
                plugin.getAuthManager().logout(player.getUniqueId());
                plugin.getLogger().info("玩家 " + playerName + " 已登出");
            }
        }

        // 重定向到登入頁面
        exchange.getResponseHeaders().set("Location", "/");
        exchange.sendResponseHeaders(302, -1);
    }

    private void handle404(HttpExchange exchange) throws IOException {
        String html = generateErrorPage(404, "頁面未找到",
                "抱歉，您訪問的頁面不存在。",
                "<a href='/'>返回首頁</a>");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        sendResponse(exchange, 404, html);
    }

    private void handle500(HttpExchange exchange, Exception e) throws IOException {
        String html = generateErrorPage(500, "伺服器錯誤",
                "伺服器處理請求時發生錯誤。",
                "<a href='/'>返回首頁</a>");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        sendResponse(exchange, 500, html);
    }

    private String generateErrorPage(int code, String title, String message, String action) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-TW\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>MyPanel - " + title + "</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Microsoft JhengHei', Arial, sans-serif;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        .error-container {\n" +
                "            background: white;\n" +
                "            padding: 60px 40px;\n" +
                "            border-radius: 15px;\n" +
                "            box-shadow: 0 15px 35px rgba(0,0,0,0.1);\n" +
                "            text-align: center;\n" +
                "            max-width: 500px;\n" +
                "        }\n" +
                "        .error-code {\n" +
                "            font-size: 72px;\n" +
                "            font-weight: bold;\n" +
                "            color: #e74c3c;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .error-title {\n" +
                "            font-size: 24px;\n" +
                "            color: #333;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "        .error-message {\n" +
                "            color: #666;\n" +
                "            margin-bottom: 30px;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        .error-action a {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 24px;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            color: white;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 8px;\n" +
                "            transition: transform 0.2s;\n" +
                "        }\n" +
                "        .error-action a:hover {\n" +
                "            transform: translateY(-2px);\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"error-container\">\n" +
                "        <div class=\"error-code\">" + code + "</div>\n" +
                "        <div class=\"error-title\">" + title + "</div>\n" +
                "        <div class=\"error-message\">" + message + "</div>\n" +
                "        <div class=\"error-action\">" + action + "</div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
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
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
