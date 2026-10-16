package dot.lighteater.upgrade_scrolls.network;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {

    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(
                            UpgradeScrolls.MODID,
                            "main"
                    ),
                    () -> VERSION,
                    VERSION::equals,
                    VERSION::equals
            );

    private static int id = 0;

    public static void register() {

        /*
         * Server -> Client
         */
        CHANNEL.registerMessage(
                id++,
                ScrollAnimationPacket.class,
                ScrollAnimationPacket::encode,
                ScrollAnimationPacket::new,
                ScrollAnimationPacket::handle
        );

        /*
         * Client -> Server
         */
        CHANNEL.registerMessage(
                id++,
                ApplyUpgradeScrollPacket.class,
                ApplyUpgradeScrollPacket::encode,
                ApplyUpgradeScrollPacket::decode,
                ApplyUpgradeScrollPacket::handle,
                Optional.of(
                        NetworkDirection.PLAY_TO_SERVER
                )
        );

        /*
         * Client -> Server
         *
         * Requests that the server open the
         * Upgrade Scroll menu.
         */
        CHANNEL.registerMessage(
                id++,
                OpenUpgradeScrollMenuPacket.class,
                OpenUpgradeScrollMenuPacket::encode,
                OpenUpgradeScrollMenuPacket::decode,
                OpenUpgradeScrollMenuPacket::handle,
                Optional.of(
                        NetworkDirection.PLAY_TO_SERVER
                )
        );
    }

    public static void sendOpenScrollMenu() {

        CHANNEL.sendToServer(
                new OpenUpgradeScrollMenuPacket()
        );
    }
}