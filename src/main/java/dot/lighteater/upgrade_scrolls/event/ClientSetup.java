package dot.lighteater.upgrade_scrolls.event;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.menu.UpgradeScrollScreen;
import dot.lighteater.upgrade_scrolls.menu.UpgradeScrollsMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = UpgradeScrolls.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(
                UpgradeScrollsMenus.UPGRADE_SCROLL_MENU.get(),
                UpgradeScrollScreen::new
        ));
    }
}