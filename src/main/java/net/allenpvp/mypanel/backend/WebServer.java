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
    private WebRouteHandler routeHandler;
    private StaticFileHandler staticFileHandler;

    public WebServer(Core plugin) {
        this.plugin = plugin;
        this.apiHandler = new APIHandler(plugin);
        this.routeHandler = new WebRouteHandler(plugin);
        this.staticFileHandler = new StaticFileHandler(plugin);
    }

    public void start() {
        try {
            int port = plugin.getConfigManager().getWebPort();
            String host = plugin.getConfigManager().getWebHost();

            server = HttpServer.create(new InetSocketAddress(host, port), 0);

            // 註冊API路由 (優先級最高)
            server.createContext("/api/auth/login", apiHandler::handleLogin);
            server.createContext("/api/player/stats", apiHandler::handlePlayerStats);
            server.createContext("/api/player/profile", apiHandler::handlePlayerProfile);

            // 註冊前端頁面路由
            server.createContext("/", routeHandler);

            // 註冊靜態資源路由 (用於CSS、JS、圖片等)
            server.createContext("/static", staticFileHandler);
            server.createContext("/assets", staticFileHandler);
            server.createContext("/css", staticFileHandler);
            server.createContext("/js", staticFileHandler);
            server.createContext("/images", staticFileHandler);

            server.setExecutor(null);
            server.start();

            plugin.getLogger().info("=================================");
            plugin.getLogger().info("MyPanel Web服務器已啟動！");
            plugin.getLogger().info("訪問地址: http://" + host + ":" + port);
            plugin.getLogger().info("登入頁面: http://" + host + ":" + port + "/");
            plugin.getLogger().info("面板頁面: http://" + host + ":" + port + "/panel");
            plugin.getLogger().info("API端點: http://" + host + ":" + port + "/api/");
            plugin.getLogger().info("=================================");

        } catch (IOException e) {
            plugin.getLogger().severe("無法啟動Web服務器: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("MyPanel Web服務器已關閉");
        }
    }

    public boolean isRunning() {
        return server != null;
    }

    public String getServerUrl() {
        if (server != null) {
            InetSocketAddress address = server.getAddress();
            return "http://" + address.getHostString() + ":" + address.getPort();
        }
        return null;
    }
}