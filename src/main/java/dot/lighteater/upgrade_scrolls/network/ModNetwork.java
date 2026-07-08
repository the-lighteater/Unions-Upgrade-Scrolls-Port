package dot.lighteater.upgrade_scrolls.network;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.network.ScrollAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(UpgradeScrolls.MODID, "main"),
                    () -> VERSION,
                    VERSION::equals,
                    VERSION::equals
            );

    private static int id = 0;

    public static void register() {

        CHANNEL.registerMessage(
                id++,
                ScrollAnimationPacket.class,
                ScrollAnimationPacket::encode,
                ScrollAnimationPacket::new,
                ScrollAnimationPacket::handle
        );

    }
}