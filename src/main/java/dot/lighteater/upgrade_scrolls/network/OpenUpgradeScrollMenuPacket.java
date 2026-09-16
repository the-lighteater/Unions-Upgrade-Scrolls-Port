package dot.lighteater.upgrade_scrolls.network;

import dot.lighteater.upgrade_scrolls.menu.UpgradeScrollMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class OpenUpgradeScrollMenuPacket {

    public OpenUpgradeScrollMenuPacket() {
    }

    public static void encode(
            OpenUpgradeScrollMenuPacket packet,
            FriendlyByteBuf buffer
    ) {
    }

    public static OpenUpgradeScrollMenuPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new OpenUpgradeScrollMenuPacket();
    }

    public static void handle(
            OpenUpgradeScrollMenuPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context =
                contextSupplier.get();

        context.enqueueWork(() -> {

            ServerPlayer player =
                    context.getSender();

            if (player == null) {
                return;
            }

            NetworkHooks.openScreen(
                    player,
                    new SimpleMenuProvider(
                            (containerId, inventory, serverPlayer) ->
                                    new UpgradeScrollMenu(
                                            containerId,
                                            inventory
                                    ),
                            Component.literal("Upgrade Scrolls")
                    )
            );
        });

        context.setPacketHandled(true);
    }
}