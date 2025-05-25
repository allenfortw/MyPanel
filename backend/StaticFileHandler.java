// backend/StaticFileHandler.java
package net.allenpvp.mypanel.backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.allenpvp.mypanel.Core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticFileHandler implements HttpHandler {
    private final Core plugin;

    public StaticFileHandler(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if ("/".equals(path)) {
            path = "/index.html";
        }

        InputStream resourceStream = getClass().getResourceAsStream("/web" + path);

        if (resourceStream == null) {
            // 404 Not Found
            String response = "404 - 頁面未找到";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        // 設置正確的Content-Type
        String contentType = getContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);

        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }

        resourceStream.close();
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        return "text/plain";
    }
}