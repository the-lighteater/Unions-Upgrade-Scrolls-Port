package dot.lighteater.upgrade_scrolls.utility;

import com.mojang.blaze3d.platform.InputConstants;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = UpgradeScrolls.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ModKeyBindings {

    public static final String CATEGORY =
            "key.categories.upgrade_scrolls";

    public static final KeyMapping OPEN_SCROLLS =
            new KeyMapping(
                    "key.upgrade_scrolls.open_scroll_application_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    CATEGORY
            );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCROLLS);
    }
}