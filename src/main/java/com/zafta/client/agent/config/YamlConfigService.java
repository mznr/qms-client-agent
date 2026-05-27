package com.zafta.client.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.zafta.client.agent.config.pojos.RootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class YamlConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlConfigService.class);

    private File resolveConfigFile() {
        String[] locations = {
                "config/application.yml",
                "./config/application.yml",
                "app/config/application.yml",
                "./app/config/application.yml"
        };

        for (String loc : locations) {
            File file = new File(loc);
            if (file.exists()) {
                return file;
            }
        }
        // Fallback to default if none exist (will likely cause FileNotFoundException which we handle)
        return new File("config/application.yml");
    }

    private final ObjectMapper mapper =
            new ObjectMapper(
                    new YAMLFactory()
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            );

    public ApplicationSettings load() {
        try {
            File configFile = resolveConfigFile();
            RootConfig root = mapper.readValue(
                    configFile,
                    RootConfig.class
            );

            ApplicationSettings settings = new ApplicationSettings();
            if (root.zafta != null && root.zafta.counter != null) {
                settings.setCounterId(root.zafta.counter.id);
            }
            if (root.qms != null && root.qms.server != null) {
                settings.setServerUrl(root.qms.server.url);
            }
            return settings;

        } catch (Exception e) {
            LOGGER.error("Failed to load YAML config", e);
            return null;
        }
    }

    public void save(String counterId, String serverUrl) {
        try {
            File file = resolveConfigFile();

            RootConfig root = mapper.readValue(file, RootConfig.class);

            if (root.zafta != null && root.zafta.counter != null) {
                root.zafta.counter.id = counterId;
            }
            if (root.qms != null && root.qms.server != null) {
                root.qms.server.url = serverUrl;
            }

            mapper.writeValue(file, root);

        } catch (Exception e) {
            LOGGER.error("Failed to save YAML config", e);
        }
    }
}
