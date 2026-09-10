package dot.lighteater.upgrade_scrolls;

import com.mojang.logging.LogUtils;
import dot.lighteater.upgrade_scrolls.item.ModCreativeModTabs;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import dot.lighteater.upgrade_scrolls.network.ModNetwork;
import dot.lighteater.upgrade_scrolls.scrollsprocedures.AffixLoader;
import dot.lighteater.upgrade_scrolls.utility.CursedConfigLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// Naming conventions and file management is credited to Kaupenjoe and his excellent 1.20.1 Forge Modding Tutorials.
// The link to his work will be credited in the description on CurseForge

@Mod(UpgradeScrolls.MODID)
public class UpgradeScrolls
{

    public static final String MODID = "upgrade_scrolls";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UpgradeScrolls(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);

        ModNetwork.register();

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);

        CursedConfigLoader.load();

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new AffixLoader());
        CursedConfigLoader.load();
    }
}
