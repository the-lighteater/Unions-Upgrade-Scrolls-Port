package dot.lighteater.upgrade_scrolls;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.Bindings;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.security.cert.CertPathBuilderSpi;

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> GOLDEN_MYSTERY_SCROLL_DROP_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> MYSTERY_SCROLL_DROP_AMOUNT;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_BOTH_HANDS;

    static {
        BUILDER.push("Mystery Scrolls");

        GOLDEN_MYSTERY_SCROLL_DROP_RATE = BUILDER.comment("The rate golden scrolls drop compared to regular mystery scrolls, meaning for this many spawners dropping regular scrolls, 1 will drop golden mystery scrolls. (Random not coded to be a fixed ratio)")
                .defineInRange("Golden Mystery Scroll Drop Rate", 150, 1, 500);

        MYSTERY_SCROLL_DROP_AMOUNT = BUILDER.comment("The upper limit of mystery scrolls dropped from spawners")
                .define("Max amount of scrolls dropped", 3);

        BUILDER.pop();

        BUILDER.push("Affix Application");

        ALLOW_BOTH_HANDS = BUILDER.comment("Allows affixes for weapons, bows, and shields to work in both hands. Disabled means it will only work in one hand.")
                .define("Allow both hands affixes", false);

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
    }
}
