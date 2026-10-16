package dot.lighteater.upgrade_scrolls.affix;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AffixLoader extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = new Gson();
    public static final Map<ResourceLocation, AffixData> AFFIXES = new HashMap<>();

    public AffixLoader() {
        super(GSON, "affixes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap,
                         ResourceManager manager,
                         ProfilerFiller profiler) {

        AFFIXES.clear();

        double total = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            try {
                AffixData affix = GSON.fromJson(entry.getValue(), AffixData.class);
                AFFIXES.put(entry.getKey(), affix);

                UpgradeScrolls.LOGGER.debug("Affix Chance for {} is {}", affix.getDisplayName(), affix.getWeight());

                total += affix.getWeight();
            } catch (Exception e) {
                System.err.println("Failed to load affix: " + entry.getKey());
            }
        }

        for (Map.Entry<ResourceLocation, AffixData> affixEntry : AFFIXES.entrySet()) {
            AffixData data = affixEntry.getValue();
            UpgradeScrolls.LOGGER.debug("Affix {} with weight {} has a chance of {}",
                    data.getDisplayName(), data.getWeight(), (data.getWeight() / total));

            data.setPercentage(data.getWeight() / total);
        }

        System.out.println("Loaded " + AFFIXES.size() + " affixes.");
    }

    public static Collection<AffixData> getAll() {
        return AFFIXES.values();
    }
}