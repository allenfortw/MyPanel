package top.allenme.mypanel;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.json.simple.JSONObject;
import java.util.Map;
import java.util.UUID;

public class WebServer {
    private final MyPanel plugin;
    private final HttpServer server;
    private final String bindAddress;
    private final int port;

    public WebServer(MyPanel plugin, String bindAddress, int port) throws IOException {
        this.plugin = plugin;
        this.bindAddress = bindAddress;
        this.port = port;

        server = HttpServer.create(new InetSocketAddress(bindAddress, port), 0);

        // 註冊處理器
        server.createContext("/", exchange -> {
            String response = getMainPage();
            sendResponse(exchange, response, "text/html");
        });

        server.createContext("/player", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.startsWith("token=")) {
                String error = "Invalid request: missing token";
                sendResponse(exchange, error, "text/plain", 400);
                return;
            }

            String token = query.substring(6); // 去掉 "token="
            if (!plugin.getTokenManager().validateToken(token)) {
                String error = "Invalid or expired token";
                sendResponse(exchange, error, "text/plain", 403);
                return;
            }

            String response = getPlayerPage();
            sendResponse(exchange, response, "text/html");
        });

        server.createContext("/api/server-status", exchange -> {
            JSONObject status = plugin.getPlayerManager().getServerStatus();
            String response = status.toJSONString();
            sendResponse(exchange, response, "application/json");
        });

        server.createContext("/api/player-data", exchange -> {
            try {
                String query = exchange.getRequestURI().getQuery();
                plugin.getLogger().info("[PlayerData API] 收到請求: " + exchange.getRequestURI());

                if (query == null || !query.startsWith("token=")) {
                    plugin.getLogger().warning("[PlayerData API] 無效請求: 缺少token參數");
                    String error = "Invalid request: missing token";
                    sendResponse(exchange, error, "text/plain", 400);
                    return;
                }

                String token = query.substring(6);
                plugin.getLogger().info("[PlayerData API] 正在驗證 Token: " + token.substring(0, 10) + "...");

                UUID playerId = plugin.getTokenManager().getPlayerIdFromToken(token);
                if (playerId == null) {
                    plugin.getLogger().warning("[PlayerData API] Token 無效或已過期");
                    String error = "Invalid or expired token";
                    sendResponse(exchange, error, "text/plain", 403);
                    return;
                }

                plugin.getLogger().info("[PlayerData API] Token 驗證成功，玩家 UUID: " + playerId);

                JSONObject playerData = plugin.getPlayerManager().getPlayerData(playerId);
                if (playerData == null) {
                    plugin.getLogger().warning("[PlayerData API] 找不到玩家數據，UUID: " + playerId);
                    String error = "Player data not found";
                    sendResponse(exchange, error, "text/plain", 404);
                    return;
                }

                plugin.getLogger().info("[PlayerData API] 成功取得玩家數據，大小: " + playerData.toString().length() + " bytes");

                String response = playerData.toJSONString();
                sendResponse(exchange, response, "application/json");

            } catch (Exception e) {
                plugin.getLogger().severe("[PlayerData API] 處理請求時發生錯誤: " + e.getMessage());
                e.printStackTrace();
                String error = "Internal server error";
                try {
                    sendResponse(exchange, error, "text/plain", 500);
                } catch (IOException ioe) {
                    plugin.getLogger().severe("[PlayerData API] 無法發送錯誤回應: " + ioe.getMessage());
                }
            }
        });
        server.setExecutor(null);
    }

    private void sendResponse(HttpExchange exchange, String response, String contentType) throws IOException {
        sendResponse(exchange, response, contentType, 200);
    }

    private void sendResponse(HttpExchange exchange, String response, String contentType, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.getResponseHeaders().add("Cache-Control", "no-cache");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    public void start() {
        server.start();
        MyPanel.log("Web server started on http://" + bindAddress + ":" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            MyPanel.log("Web server stopped");
        }
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public int getPort() {
        return port;
    }

    private String getMainPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Minecraft Server Status</title>\n");
        html.append("    <link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\">\n");
        html.append("    <link href=\"https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@6.0.0/css/all.min.css\" rel=\"stylesheet\">\n");
        html.append("    <style>\n");
        html.append("        .gradient-bg { background: linear-gradient(135deg, #1a365d 0%, #2d3748 100%); }\n");
        html.append("        .glass-effect { backdrop-filter: blur(10px); background-color: rgba(255, 255, 255, 0.1); }\n");
        html.append("        .hover-scale { transition: transform 0.2s; }\n");
        html.append("        .hover-scale:hover { transform: scale(1.02); }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body class=\"gradient-bg min-h-screen text-white\">\n");
        html.append("    <div class=\"container mx-auto px-4 py-8\">\n");
        html.append("        <div class=\"text-center mb-12\">\n");
        html.append("            <h1 class=\"text-5xl font-bold mb-4\">Minecraft Server Status</h1>\n");
        html.append("            <p class=\"text-xl text-gray-300\" id=\"server-time\"></p>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"grid grid-cols-1 md:grid-cols-2 gap-8 mb-8\">\n");
        html.append("            <div class=\"glass-effect rounded-xl p-6 hover-scale\">\n");
        html.append("                <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-server mr-2\"></i>Server Info</h2>\n");
        html.append("                <div id=\"server-info\" class=\"space-y-4\"></div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"glass-effect rounded-xl p-6 hover-scale\">\n");
        html.append("                <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-memory mr-2\"></i>Performance</h2>\n");
        html.append("                <div id=\"performance-info\" class=\"space-y-4\"></div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        html.append("        <div class=\"glass-effect rounded-xl p-6 hover-scale\">\n");
        html.append("            <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-users mr-2\"></i>Online Players</h2>\n");
        html.append("            <div id=\"players-list\" class=\"grid grid-cols-1 md:grid-cols-3 gap-4\"></div>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("    <script>\n");
        html.append("        function updateServerTime() {\n");
        html.append("            const now = new Date();\n");
        html.append("            document.getElementById('server-time').textContent = \n");
        html.append("                now.toISOString().replace('T', ' ').substr(0, 19) + ' UTC';\n");
        html.append("        }\n");
        html.append("        \n");
        html.append("        function updateStatus() {\n");
        html.append("            fetch('/api/server-status')\n");
        html.append("                .then(response => response.json())\n");
        html.append("                .then(data => {\n");
        html.append("                    // Server Info\n");
        html.append("                    document.getElementById('server-info').innerHTML = `\n");
        html.append("                        <div class='grid grid-cols-2 gap-4'>\n");
        html.append("                            <div class='bg-white bg-opacity-10 p-4 rounded-lg'>\n");
        html.append("                                <div class='text-gray-300'>Players</div>\n");
        html.append("                                <div class='text-2xl'>${data.onlinePlayers}/${data.maxPlayers}</div>\n");
        html.append("                            </div>\n");
        html.append("                            <div class='bg-white bg-opacity-10 p-4 rounded-lg'>\n");
        html.append("                                <div class='text-gray-300'>Version</div>\n");
        html.append("                                <div class='text-2xl'>${data.serverInfo.version}</div>\n");
        html.append("                            </div>\n");
        html.append("                        </div>\n");
        html.append("                    `;\n");
        html.append("                    \n");
        html.append("                    // Performance Info\n");
        html.append("                    document.getElementById('performance-info').innerHTML = `\n");
        html.append("                        <div class='space-y-4'>\n");
        html.append("                            <div class='relative pt-1'>\n");
        html.append("                                <div class='text-gray-300 mb-2'>TPS</div>\n");
        html.append("                                <div class='flex h-2 mb-4 overflow-hidden rounded bg-gray-700'>\n");
        html.append("                                    <div class='flex flex-col justify-center rounded bg-green-500'\n");
        html.append("                                         style='width: ${(data.tps/20)*100}%'></div>\n");
        html.append("                                </div>\n");
        html.append("                                <div class='text-xl'>${data.tps.toFixed(2)}</div>\n");
        html.append("                            </div>\n");
        html.append("                            <div class='relative pt-1'>\n");
        html.append("                                <div class='text-gray-300 mb-2'>Memory Usage</div>\n");
        html.append("                                <div class='flex h-2 mb-4 overflow-hidden rounded bg-gray-700'>\n");
        html.append("                                    <div class='flex flex-col justify-center rounded bg-blue-500'\n");
        html.append("                                         style='width: ${(data.memory.allocated/data.memory.max)*100}%'></div>\n");
        html.append("                                </div>\n");
        html.append("                                <div class='text-xl'>${data.memory.allocated}MB / ${data.memory.max}MB</div>\n");
        html.append("                            </div>\n");
        html.append("                        </div>\n");
        html.append("                    `;\n");
        html.append("                    \n");
        html.append("                    // Players List\n");
        html.append("                    const playersList = document.getElementById('players-list');\n");
        html.append("                    playersList.innerHTML = '';\n");
        html.append("                    Object.values(data.players).forEach(player => {\n");
        html.append("                        const playerCard = document.createElement('div');\n");
        html.append("                        playerCard.className = 'bg-white bg-opacity-10 p-4 rounded-lg flex items-center space-x-4';\n");
        html.append("                        playerCard.innerHTML = `\n");
        html.append("                            <img src='https://mc-heads.net/avatar/${player.name}' alt='${player.name}' class='w-10 h-10 rounded'>\n");
        html.append("                            <div>\n");
        html.append("                                <div class='font-bold'>${player.name}</div>\n");
        html.append("                                <div class='text-sm text-gray-300'>${player.world}</div>\n");
        html.append("                            </div>\n");
        html.append("                        `;\n");
        html.append("                        playersList.appendChild(playerCard);\n");
        html.append("                    });\n");
        html.append("                });\n");
        html.append("        }\n");
        html.append("        \n");
        html.append("        updateServerTime();\n");
        html.append("        setInterval(updateServerTime, 1000);\n");
        html.append("        updateStatus();\n");
        html.append("        setInterval(updateStatus, 5000);\n");
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private String getPlayerPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Player Status</title>\n");
        html.append("    <link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\">\n");
        html.append("    <link href=\"https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@6.0.0/css/all.min.css\" rel=\"stylesheet\">\n");
        html.append("    <style>\n");
        html.append("        .gradient-bg { background: linear-gradient(135deg, #1a365d 0%, #2d3748 100%); }\n");
        html.append("        .glass-effect { backdrop-filter: blur(10px); background-color: rgba(255, 255, 255, 0.1); }\n");
        html.append("        .hover-scale { transition: transform 0.2s; }\n");
        html.append("        .hover-scale:hover { transform: scale(1.02); }\n");
        html.append("        .inventory-slot { width: 64px; height: 64px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body class=\"gradient-bg min-h-screen text-white\">\n");
        html.append("    <div class=\"container mx-auto px-4 py-8\">\n");
        html.append("        <div class=\"text-center mb-12\">\n");
        html.append("            <h1 class=\"text-5xl font-bold mb-4\">Player Status</h1>\n");
        html.append("            <div id=\"player-name\" class=\"text-2xl text-gray-300\"></div>\n");
        html.append("        </div>\n");
        html.append("        \n");
        html.append("        <div class=\"grid grid-cols-1 md:grid-cols-2 gap-8 mb-8\">\n");
        html.append("            <div class=\"glass-effect rounded-xl p-6 hover-scale\">\n");
        html.append("                <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-heart mr-2\"></i>Health & Hunger</h2>\n");
        html.append("                <div class=\"space-y-6\">\n");
        html.append("                    <div class=\"relative pt-1\">\n");
        html.append("                        <div class=\"text-gray-300 mb-2\">Health</div>\n");
        html.append("                        <div class=\"flex h-4 mb-4 overflow-hidden rounded bg-gray-700\">\n");
        html.append("                            <div id=\"health-bar\" class=\"flex flex-col justify-center rounded bg-red-500 transition-all duration-500\"></div>\n");
        html.append("                        </div>\n");
        html.append("                        <div id=\"health-text\" class=\"text-xl\"></div>\n");
        html.append("                    </div>\n");
        html.append("                    <div class=\"relative pt-1\">\n");
        html.append("                        <div class=\"text-gray-300 mb-2\">Food Level</div>\n");
        html.append("                        <div class=\"flex h-4 mb-4 overflow-hidden rounded bg-gray-700\">\n");
        html.append("                            <div id=\"food-bar\" class=\"flex flex-col justify-center rounded bg-green-500 transition-all duration-500\"></div>\n");
        html.append("                        </div>\n");
        html.append("                        <div id=\"food-text\" class=\"text-xl\"></div>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("            \n");
        html.append("            <div class=\"glass-effect rounded-xl p-6 hover-scale\">\n");
        html.append("                <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-map-marker-alt mr-2\"></i>Location</h2>\n");
        html.append("                <div id=\"location-info\" class=\"space-y-4\"></div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        html.append("        \n");
        html.append("        <div class=\"glass-effect rounded-xl p-6 hover-scale\">\n");
        html.append("            <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-box mr-2\"></i>Inventory</h2>\n");
        html.append("            <div class=\"grid grid-cols-9 gap-2\" id=\"inventory-grid\">\n");
        html.append("                <!-- Inventory slots will be filled by JavaScript -->\n");
        html.append("            </div>\n");
        html.append("        </div>\n");

        html.append("        <div class=\"glass-effect rounded-xl p-6 hover-scale mt-8\">\n");
        html.append("            <h2 class=\"text-2xl font-bold mb-4\"><i class=\"fas fa-magic mr-2\"></i>Effects</h2>\n");
        html.append("            <div id=\"effects-list\" class=\"grid grid-cols-1 md:grid-cols-2 gap-4\">\n");
        html.append("                <!-- Effects will be filled by JavaScript -->\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("    \n");
        html.append("    <script>\n");
        html.append("        let updateInterval;\n");
        html.append("        let failedAttempts = 0;\n");
        html.append("        const MAX_RETRY_ATTEMPTS = 3;\n");
        html.append("        \n");
        html.append("        function updatePlayerData() {\n");
        html.append("            const urlParams = new URLSearchParams(window.location.search);\n");
        html.append("            const token = urlParams.get('token');\n");
        html.append("            \n");
        html.append("            if (!token) {\n");
        html.append("                console.error('No token provided');\n");
        html.append("                return;\n");
        html.append("            }\n");
        html.append("            \n");
        html.append("            fetch('/api/player-data?token=' + token)\n");
        html.append("                .then(response => {\n");
        html.append("                    if (!response.ok) {\n");
        html.append("                        throw new Error(`HTTP error! status: ${response.status}`);\n");
        html.append("                    }\n");
        html.append("                    failedAttempts = 0; // 重置失敗計數\n");
        html.append("                    return response.json();\n");
        html.append("                })\n");
        html.append("                .then(data => {\n");
        html.append("                    // [保留原有的數據更新邏輯]\n");
        html.append("                })\n");
        html.append("                .catch(error => {\n");
        html.append("                    console.error('Error:', error);\n");
        html.append("                    failedAttempts++;\n");
        html.append("                    \n");
        html.append("                    if (failedAttempts >= MAX_RETRY_ATTEMPTS) {\n");
        html.append("                        clearInterval(updateInterval);\n");
        html.append("                        document.body.innerHTML += `\n");
        html.append("                            <div class='fixed top-0 left-0 w-full p-4 bg-red-500 text-white text-center'>\n");
        html.append("                                連線已中斷，請重新載入頁面\n");
        html.append("                            </div>\n");
        html.append("                        `;\n");
        html.append("                    }\n");
        html.append("                });\n");
        html.append("        }\n");
        html.append("        \n");
        html.append("        // 初始更新\n");
        html.append("        updatePlayerData();\n");
        html.append("        \n");
        html.append("        // 設定定期更新（每秒）\n");
        html.append("        updateInterval = setInterval(updatePlayerData, 1000);\n");
        html.append("        \n");
        html.append("        // 頁面隱藏時暫停更新\n");
        html.append("        document.addEventListener('visibilitychange', () => {\n");
        html.append("            if (document.hidden) {\n");
        html.append("                clearInterval(updateInterval);\n");
        html.append("            } else {\n");
        html.append("                updatePlayerData(); // 立即更新一次\n");
        html.append("                updateInterval = setInterval(updatePlayerData, 1000);\n");
        html.append("            }\n");
        html.append("        });\n");
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }
}