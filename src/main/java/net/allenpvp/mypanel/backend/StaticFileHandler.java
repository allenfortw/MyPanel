// backend/StaticFileHandler.java
package net.allenpvp.mypanel.backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.allenpvp.mypanel.Core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StaticFileHandler implements HttpHandler {
    private final Core plugin;

    public StaticFileHandler(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // 只處理靜態資源請求
        if (!isStaticResource(path)) {
            send404(exchange);
            return;
        }

        // 處理靜態資源
        InputStream resourceStream = getClass().getResourceAsStream("/web" + path);

        if (resourceStream == null) {
            // 嘗試從resources目錄載入
            resourceStream = getClass().getResourceAsStream(path);
        }

        if (resourceStream == null) {
            plugin.getLogger().warning("靜態資源未找到: " + path);
            send404(exchange);
            return;
        }

        try {
            // 設置正確的Content-Type
            String contentType = getContentType(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);

            // 設置快取headers
            exchange.getResponseHeaders().set("Cache-Control", "public, max-age=3600");

            // 設置CORS headers
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            exchange.sendResponseHeaders(200, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = resourceStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            plugin.getLogger().fine("提供靜態資源: " + path);

        } finally {
            resourceStream.close();
        }
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico") ||
                path.endsWith(".svg") ||
                path.endsWith(".woff") ||
                path.endsWith(".woff2") ||
                path.endsWith(".ttf") ||
                path.endsWith(".eot") ||
                path.startsWith("/static/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/");
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (path.endsWith(".json")) return "application/json; charset=utf-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        if (path.endsWith(".woff")) return "font/woff";
        if (path.endsWith(".woff2")) return "font/woff2";
        if (path.endsWith(".ttf")) return "font/ttf";
        if (path.endsWith(".eot")) return "application/vnd.ms-fontobject";
        return "text/plain; charset=utf-8";
    }

    private void send404(HttpExchange exchange) throws IOException {
        String response = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-TW\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>404 - 資源未找到</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }\n" +
                "        h1 { color: #e74c3c; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>404</h1>\n" +
                "    <p>請求的資源未找到</p>\n" +
                "    <a href='/'>返回首頁</a>\n" +
                "</body>\n" +
                "</html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(404, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}