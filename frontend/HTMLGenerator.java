// frontend/HTMLGenerator.java
package net.allenpvp.mypanel.frontend;

public class HTMLGenerator {

    public static String generateLoginPage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-TW\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>MyPanel - 登入</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'Microsoft JhengHei', Arial, sans-serif;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "        }\n" +
                "        .login-container {\n" +
                "            background: white;\n" +
                "            padding: 40px;\n" +
                "            border-radius: 15px;\n" +
                "            box-shadow: 0 15px 35px rgba(0,0,0,0.1);\n" +
                "            width: 100%;\n" +
                "            max-width: 400px;\n" +
                "        }\n" +
                "        .login-header {\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        .login-header h1 {\n" +
                "            color: #333;\n" +
                "            font-size: 28px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .form-group {\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .form-group label {\n" +
                "            display: block;\n" +
                "            margin-bottom: 8px;\n" +
                "            color: #555;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        .input-container {\n" +
                "            position: relative;\n" +
                "        }\n" +
                "        .form-control {\n" +
                "            width: 100%;\n" +
                "            padding: 12px 40px 12px 15px;\n" +
                "            border: 2px solid #e1e5e9;\n" +
                "            border-radius: 8px;\n" +
                "            font-size: 16px;\n" +
                "            transition: border-color 0.3s;\n" +
                "        }\n" +
                "        .form-control:focus {\n" +
                "            outline: none;\n" +
                "            border-color: #667eea;\n" +
                "        }\n" +
                "        .password-toggle {\n" +
                "            position: absolute;\n" +
                "            right: 12px;\n" +
                "            top: 50%;\n" +
                "            transform: translateY(-50%);\n" +
                "            cursor: pointer;\n" +
                "            font-size: 18px;\n" +
                "            color: #999;\n" +
                "        }\n" +
                "        .login-btn {\n" +
                "            width: 100%;\n" +
                "            padding: 14px;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            border-radius: 8px;\n" +
                "            font-size: 16px;\n" +
                "            font-weight: 600;\n" +
                "            cursor: pointer;\n" +
                "            transition: transform 0.2s;\n" +
                "        }\n" +
                "        .login-btn:hover {\n" +
                "            transform: translateY(-2px);\n" +
                "        }\n" +
                "        .error-message {\n" +
                "            color: #e74c3c;\n" +
                "            text-align: center;\n" +
                "            margin-top: 15px;\n" +
                "            display: none;\n" +
                "        }\n" +
                "        @media (max-width: 480px) {\n" +
                "            .login-container {\n" +
                "                padding: 30px 20px;\n" +
                "                margin: 20px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"login-container\">\n" +
                "        <div class=\"login-header\">\n" +
                "            <h1>MyPanel</h1>\n" +
                "            <p>請登入您的帳號</p>\n" +
                "        </div>\n" +
                "        <form id=\"loginForm\">\n" +
                "            <div class=\"form-group\">\n" +
                "                <label for=\"playerName\">請輸入玩家名稱</label>\n" +
                "                <input type=\"text\" id=\"playerName\" class=\"form-control\" required>\n" +
                "            </div>\n" +
                "            <div class=\"form-group\">\n" +
                "                <label for=\"password\">請輸入密碼</label>\n" +
                "                <div class=\"input-container\">\n" +
                "                    <input type=\"password\" id=\"password\" class=\"form-control\" required>\n" +
                "                    <span class=\"password-toggle\" onclick=\"togglePassword()\">👁</span>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <button type=\"submit\" class=\"login-btn\">登入 →</button>\n" +
                "            <div id=\"errorMessage\" class=\"error-message\"></div>\n" +
                "        </form>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        function togglePassword() {\n" +
                "            const passwordInput = document.getElementById('password');\n" +
                "            const toggleIcon = document.querySelector('.password-toggle');\n" +
                "            \n" +
                "            if (passwordInput.type === 'password') {\n" +
                "                passwordInput.type = 'text';\n" +
                "                toggleIcon.textContent = '🙈';\n" +
                "            } else {\n" +
                "                passwordInput.type = 'password';\n" +
                "                toggleIcon.textContent = '👁';\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        document.getElementById('loginForm').addEventListener('submit', async function(e) {\n" +
                "            e.preventDefault();\n" +
                "            \n" +
                "            const playerName = document.getElementById('playerName').value;\n" +
                "            const password = document.getElementById('password').value;\n" +
                "            const errorMessage = document.getElementById('errorMessage');\n" +
                "            \n" +
                "            try {\n" +
                "                const response = await fetch('/api/auth/login', {\n" +
                "                    method: 'POST',\n" +
                "                    headers: {\n" +
                "                        'Content-Type': 'application/json'\n" +
                "                    },\n" +
                "                    body: JSON.stringify({ playerName, password })\n" +
                "                });\n" +
                "                \n" +
                "                const result = await response.json();\n" +
                "                \n" +
                "                if (result.success) {\n" +
                "                    localStorage.setItem('sessionId', result.sessionId);\n" +
                "                    localStorage.setItem('playerName', playerName);\n" +
                "                    window.location.href = '/panel.html';\n" +
                "                } else {\n" +
                "                    errorMessage.textContent = result.message;\n" +
                "                    errorMessage.style.display = 'block';\n" +
                "                }\n" +
                "            } catch (error) {\n" +
                "                errorMessage.textContent = '登入失敗，請稍後再試';\n" +
                "                errorMessage.style.display = 'block';\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        // Enter 鍵切換功能\n" +
                "        document.getElementById('playerName').addEventListener('keypress', function(e) {\n" +
                "            if (e.key === 'Enter') {\n" +
                "                e.preventDefault();\n" +
                "                document.getElementById('password').focus();\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        document.getElementById('password').addEventListener('keypress', function(e) {\n" +
                "            if (e.key === 'Enter') {\n" +
                "                e.preventDefault();\n" +
                "                document.getElementById('loginForm').dispatchEvent(new Event('submit'));\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String generatePanelPage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-TW\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>MyPanel - 玩家面板</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'Microsoft JhengHei', Arial, sans-serif;\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            min-height: 100vh;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .panel-container {\n" +
                "            max-width: 1200px;\n" +
                "            margin: 0 auto;\n" +
                "            background: white;\n" +
                "            border-radius: 15px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 15px 35px rgba(0,0,0,0.1);\n" +
                "        }\n" +
                "        .panel-header {\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            color: white;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .player-info {\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            gap: 15px;\n" +
                "        }\n" +
                "        .player-avatar {\n" +
                "            width: 64px;\n" +
                "            height: 64px;\n" +
                "            background: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "        .stats-container {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n" +
                "            gap: 20px;\n" +
                "            padding: 30px;\n" +
                "        }\n" +
                "        .stat-card {\n" +
                "            background: #f8f9fa;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 10px;\n" +
                "            text-align: center;\n" +
                "            border: 2px solid #e9ecef;\n" +
                "            transition: transform 0.3s;\n" +
                "        }\n" +
                "        .stat-card:hover {\n" +
                "            transform: translateY(-5px);\n" +
                "        }\n" +
                "        .stat-emoji {\n" +
                "            font-size: 32px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .stat-name {\n" +
                "            color: #666;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        .stat-value {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .functions-container {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: 200px 1fr;\n" +
                "            gap: 20px;\n" +
                "            padding: 30px;\n" +
                "            border-top: 1px solid #e9ecef;\n" +
                "        }\n" +
                "        .function-buttons {\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            gap: 10px;\n" +
                "        }\n" +
                "        .function-btn {\n" +
                "            padding: 12px 20px;\n" +
                "            background: #f8f9fa;\n" +
                "            border: 2px solid #e9ecef;\n" +
                "            border-radius: 8px;\n" +
                "            cursor: pointer;\n" +
                "            transition: all 0.3s;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        .function-btn:hover,\n" +
                "        .function-btn.active {\n" +
                "            background: #667eea;\n" +
                "            color: white;\n" +
                "            border-color: #667eea;\n" +
                "        }\n" +
                "        .function-panel {\n" +
                "            background: #f8f9fa;\n" +
                "            border-radius: 10px;\n" +
                "            padding: 30px;\n" +
                "            min-height: 300px;\n" +
                "        }\n" +
                "        .logout-btn {\n" +
                "            position: absolute;\n" +
                "            top: 20px;\n" +
                "            right: 20px;\n" +
                "            background: rgba(255,255,255,0.2);\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            padding: 8px 16px;\n" +
                "            border-radius: 5px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "        @media (max-width: 768px) {\n" +
                "            .functions-container {\n" +
                "                grid-template-columns: 1fr;\n" +
                "            }\n" +
                "            .function-buttons {\n" +
                "                flex-direction: row;\n" +
                "                overflow-x: auto;\n" +
                "            }\n" +
                "            .function-btn {\n" +
                "                white-space: nowrap;\n" +
                "                min-width: 100px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"panel-container\">\n" +
                "        <div class=\"panel-header\">\n" +
                "            <button class=\"logout-btn\" onclick=\"logout()\">登出</button>\n" +
                "            <div class=\"player-info\">\n" +
                "                <div class=\"player-avatar\">👤</div>\n" +
                "                <div>\n" +
                "                    <h2 id=\"playerName\">載入中...</h2>\n" +
                "                    <p id=\"playerPrefix\">玩家</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"stats-container\" id=\"statsContainer\">\n" +
                "            <!-- 統計數據將動態載入 -->\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"functions-container\">\n" +
                "            <div class=\"function-buttons\">\n" +
                "                <div class=\"function-btn active\" onclick=\"showFunction('stats')\">📊 統計</div>\n" +
                "                <div class=\"function-btn\" onclick=\"showFunction('settings')\">⚙️ 設定</div>\n" +
                "                <div class=\"function-btn\" onclick=\"showFunction('friends')\">👥 好友</div>\n" +
                "                <div class=\"function-btn\" onclick=\"showFunction('achievements')\">🏆 成就</div>\n" +
                "                <div class=\"function-btn\" onclick=\"showFunction('shop')\">🛒 商店</div>\n" +
                "                <div class=\"function-btn\" onclick=\"showFunction('help')\">❓ 幫助</div>\n" +
                "            </div>\n" +
                "            <div class=\"function-panel\" id=\"functionPanel\">\n" +
                "                <h3>統計資訊</h3>\n" +
                "                <p>這裡顯示詳細的統計資訊...</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        // 檢查登入狀態\n" +
                "        const sessionId = localStorage.getItem('sessionId');\n" +
                "        const playerName = localStorage.getItem('playerName');\n" +
                "        \n" +
                "        if (!sessionId || !playerName) {\n" +
                "            window.location.href = '/';\n" +
                "        }\n" +
                "\n" +
                "        // 載入玩家資料\n" +
                "        async function loadPlayerData() {\n" +
                "            try {\n" +
                "                // 載入玩家資料\n" +
                "                const profileResponse = await fetch(`/api/player/profile?playerName=${playerName}`);\n" +
                "                const profile = await profileResponse.json();\n" +
                "                \n" +
                "                document.getElementById('playerName').textContent = profile.playerName;\n" +
                "                document.getElementById('playerPrefix').textContent = '玩家';\n" +
                "\n" +
                "                // 載入統計數據\n" +
                "                const statsResponse = await fetch(`/api/player/stats?playerName=${playerName}`);\n" +
                "                const stats = await statsResponse.json();\n" +
                "                \n" +
                "                const statsContainer = document.getElementById('statsContainer');\n" +
                "                statsContainer.innerHTML = `\n" +
                "                    <div class=\"stat-card\">\n" +
                "                        <div class=\"stat-emoji\">⚔️</div>\n" +
                "                        <div class=\"stat-name\">擊殺數</div>\n" +
                "                        <div class=\"stat-value\">${stats.kills}</div>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-card\">\n" +
                "                        <div class=\"stat-emoji\">💀</div>\n" +
                "                        <div class=\"stat-name\">死亡數</div>\n" +
                "                        <div class=\"stat-value\">${stats.deaths}</div>\n" +
                "                    </div>\n" +
                "                    <div class=\"stat-card\">\n" +
                "                        <div class=\"stat-emoji\">⏰</div>\n" +
                "                        <div class=\"stat-name\">遊戲時間</div>\n" +
                "                        <div class=\"stat-value\">${stats.playTime}分</div>\n" +
                "                    </div>\n" +
                "                `;\n" +
                "                \n" +
                "            } catch (error) {\n" +
                "                console.error('載入玩家資料失敗:', error);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        // 顯示功能面板\n" +
                "        function showFunction(functionName) {\n" +
                "            // 更新按鈕狀態\n" +
                "            document.querySelectorAll('.function-btn').forEach(btn => {\n" +
                "                btn.classList.remove('active');\n" +
                "            });\n" +
                "            event.target.classList.add('active');\n" +
                "\n" +
                "            // 更新面板內容\n" +
                "            const panel = document.getElementById('functionPanel');\n" +
                "            \n" +
                "            const content = {\n" +
                "                stats: '<h3>📊 詳細統計</h3><p>這裡顯示更詳細的統計資訊，包括各種遊戲數據...</p>',\n" +
                "                settings: '<h3>⚙️ 設定</h3><p>個人設定選項，包括隱私設定、通知設定等...</p>',\n" +
                "                friends: '<h3>👥 好友列表</h3><p>管理您的好友，查看好友狀態...</p>',\n" +
                "                achievements: '<h3>🏆 成就系統</h3><p>查看已獲得的成就和進度...</p>',\n" +
                "                shop: '<h3>🛒 商店</h3><p>購買遊戲道具和特殊物品...</p>',\n" +
                "                help: '<h3>❓ 幫助中心</h3><p>常見問題解答和使用說明...</p>'\n" +
                "            };\n" +
                "            \n" +
                "            panel.innerHTML = content[functionName] || content.stats;\n" +
                "        }\n" +
                "\n" +
                "        // 登出功能\n" +
                "        function logout() {\n" +
                "            localStorage.removeItem('sessionId');\n" +
                "            localStorage.removeItem('playerName');\n" +
                "            window.location.href = '/';\n" +
                "        }\n" +
                "\n" +
                "        // 頁面載入時執行\n" +
                "        window.addEventListener('load', loadPlayerData);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
}