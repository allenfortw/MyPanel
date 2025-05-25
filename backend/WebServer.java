// backend/WebServer.java
package net.allenpvp.mypanel.backend;

import com.sun.net.httpserver.HttpServer;
import net.allenpvp.mypanel.Core;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {
    private final Core plugin;
    private HttpServer server;
    private APIHandler apiHandler;

    public WebServer(Core plugin) {
        this.plugin = plugin;
        this.apiHandler = new APIHandler(plugin);
    }

    public void start() {
        try {
            int port = plugin.getConfigManager().getWebPort();
            String host = plugin.getConfigManager().getWebHost();

            server = HttpServer.create(new InetSocketAddress(host, port), 0);

            // 註冊API路由
            server.createContext("/api/auth/login", apiHandler::handleLogin);
            server.createContext("/api/player/stats", apiHandler::handlePlayerStats);
            server.createContext("/api/player/profile", apiHandler::handlePlayerProfile);

            // 註冊靜態文件路由
            server.createContext("/", new StaticFileHandler(plugin));

            server.setExecutor(null);
            server.start();

            plugin.getLogger().info("Web服務器已在 " + host + ":" + port + " 啟動");

        } catch (IOException e) {
            plugin.getLogger().severe("無法啟動Web服務器: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("Web服務器已關閉");
        }
    }
}