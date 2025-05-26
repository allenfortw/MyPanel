package net.allenpvp.mypanel.frontend;

import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class TemplateLoader {
    private static final String CONFIG_PATH = "plugins/MyPanel/template/html/webserver.yml";
    private Map<String, Object> config;
    private Map<String, String> templateCache;

    public TemplateLoader() {
        loadConfig();
        templateCache = new HashMap<>();
    }

    private void loadConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(CONFIG_PATH);
            config = yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template configuration", e);
        }
    }

    public String loadTemplate(String templateName, Map<String, Object> placeholders) {
        // Get template configuration
        Map<String, Object> templates = (Map<String, Object>) config.get("templates");
        Map<String, Object> templateConfig = (Map<String, Object>) templates.get(templateName);

        if (templateConfig == null || !Boolean.TRUE.equals(templateConfig.get("enabled"))) {
            throw new IllegalArgumentException("Template not found or disabled: " + templateName);
        }

        // Load template content
        String templatePath = (String) templateConfig.get("path");
        String template = loadTemplateFile(templatePath);

        // Merge default placeholders from config with provided placeholders
        Map<String, Object> defaultPlaceholders = (Map<String, Object>) templateConfig.get("placeholders");
        if (defaultPlaceholders != null) {
            Map<String, Object> mergedPlaceholders = new HashMap<>(defaultPlaceholders);
            if (placeholders != null) {
                mergedPlaceholders.putAll(placeholders);
            }
            placeholders = mergedPlaceholders;
        }

        // Replace placeholders
        return replacePlaceholders(template, placeholders);
    }

    private String loadTemplateFile(String templatePath) {
        String fullPath = ((Map<String, Object>) config.get("settings")).get("template_directory") + "/" + templatePath;

        try {
            if (templateCache.containsKey(fullPath)) {
                return templateCache.get(fullPath);
            }

            // Read file content using buffered reader instead of Files.readString()
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fullPath), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String templateContent = content.toString();
            templateCache.put(fullPath, templateContent);
            return templateContent;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load template file: " + templatePath, e);
        }
    }

    private String replacePlaceholders(String template, Map<String, Object> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = String.valueOf(entry.getValue());
            result = result.replace(placeholder, value);
        }
        return result;
    }

    /**
     * Clears the template cache
     */
    public void clearCache() {
        templateCache.clear();
    }

    /**
     * Reloads configuration and clears cache
     */
    public void reload() {
        loadConfig();
        clearCache();
    }
}