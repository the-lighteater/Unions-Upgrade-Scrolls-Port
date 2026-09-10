package dot.lighteater.upgrade_scrolls.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class CursedConfigLoader {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH =
            FMLPaths.CONFIGDIR.get()
                    .resolve("upgrade_scrolls")
                    .resolve("cursed.json");

    // Default value
    private static final double DEFAULT_CHANCE = 0.8;

    // Runtime value
    private static double chance = DEFAULT_CHANCE;

    public static double getChance() {
        return chance;
    }

    public static void load() {
        try {
            // Make sure config/upgrade_scrolls exists
            Files.createDirectories(CONFIG_PATH.getParent());

            // Generate default file if it doesn't exist
            if (!Files.exists(CONFIG_PATH)) {
                createDefault();
            }

            // Load the JSON
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                CursedData data = GSON.fromJson(reader, CursedData.class);

                if (data == null) {
                    throw new IOException("cursed.json was empty");
                }

                chance = data.chance;

                UpgradeScrolls.LOGGER.info(
                        "[CursedConfig] Loaded cursed chance: {}",
                        chance
                );
            }

        } catch (Exception e) {
            UpgradeScrolls.LOGGER.error(
                    "[CursedConfig] Failed to load cursed.json. Using default values.",
                    e
            );

            chance = DEFAULT_CHANCE;
        }
    }

    private static void createDefault() throws IOException {
        CursedData data = new CursedData();
        data.chance = DEFAULT_CHANCE;

        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(data, writer);
        }

        UpgradeScrolls.LOGGER.info(
                "[CursedConfig] Created default config: {}",
                CONFIG_PATH
        );
    }

    private static class CursedData {
        double chance;
    }
}