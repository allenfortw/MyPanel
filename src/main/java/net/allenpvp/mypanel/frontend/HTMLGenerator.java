package net.allenpvp.mypanel.frontend;

import java.util.HashMap;
import java.util.Map;

public class HTMLGenerator {
    private static final TemplateLoader templateLoader = new TemplateLoader();

    public static String generateLoginPage() {
        Map<String, Object> placeholders = new HashMap<>();
        // Add any dynamic placeholders here
        placeholders.put("styles", getLoginStyles());
        placeholders.put("scripts", getLoginScripts());

        return templateLoader.loadTemplate("login", placeholders);
    }

    public static String generatePanelPage() {
        Map<String, Object> placeholders = new HashMap<>();
        // Add any dynamic placeholders here
        placeholders.put("styles", getPanelStyles());
        placeholders.put("scripts", getPanelScripts());
        placeholders.put("statsCards", generateStatsCards());
        placeholders.put("functionButtons", generateFunctionButtons());

        return templateLoader.loadTemplate("panel", placeholders);
    }

    // Helper methods to generate dynamic content
    private static String getLoginStyles() {
        // Return login page styles
        return "/* ... */";
    }

    private static String getLoginScripts() {
        // Return login page scripts
        return "/* ... */";
    }

    private static String getPanelStyles() {
        // Return panel page styles
        return "/* ... */";
    }

    private static String getPanelScripts() {
        // Return panel page scripts
        return "/* ... */";
    }

    private static String generateStatsCards() {
        // Generate statistics cards HTML
        return "/* ... */";
    }

    private static String generateFunctionButtons() {
        // Generate function buttons HTML
        return "/* ... */";
    }
}