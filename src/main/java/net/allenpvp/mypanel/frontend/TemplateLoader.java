package net.allenpvp.mypanel.frontend;

import org.yaml.snakeyaml.Yaml;
import java.io.*;
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
            placeholders = new HashMap<>(defaultPlaceholders);
            placeholders.putAll(placeholders);
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

            String content = Files.readString(Paths.get(fullPath));
            templateCache.put(fullPath, content);
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template file: " + templatePath, e);
        }
    }

    private String replacePlaceholders(String template, Map<String, Object> placeholders) {
        String result = template;
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}