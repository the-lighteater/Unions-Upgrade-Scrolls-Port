package dot.lighteater.upgrade_scrolls.network;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.custom.UpgradeScroll_Item;
import dot.lighteater.upgrade_scrolls.menu.UpgradeScrollMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ApplyUpgradeScrollPacket {

    private final int scrollSlot;

    public ApplyUpgradeScrollPacket(int scrollSlot) {
        this.scrollSlot = scrollSlot;
    }

    public static void encode(
            ApplyUpgradeScrollPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeInt(packet.scrollSlot);
    }

    public static ApplyUpgradeScrollPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new ApplyUpgradeScrollPacket(
                buffer.readInt()
        );
    }

    public static void handle(
            ApplyUpgradeScrollPacket packet,
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

            AbstractContainerMenu menu =
                    player.containerMenu;

            if (!(menu instanceof UpgradeScrollMenu scrollMenu)) {
                return;
            }

            performApply(
                    player,
                    scrollMenu,
                    packet.scrollSlot
            );
        });

        context.setPacketHandled(true);
    }

    private static void performApply(
            ServerPlayer player,
            UpgradeScrollMenu menu,
            int scrollSlot
    ) {
        /*
         * Only these four slots can be used
         * as scroll slots.
         */
        if (scrollSlot != UpgradeScrollMenu.SCROLL_SLOT_1
                && scrollSlot != UpgradeScrollMenu.SCROLL_SLOT_2
                && scrollSlot != UpgradeScrollMenu.SCROLL_SLOT_3
                && scrollSlot != UpgradeScrollMenu.SCROLL_SLOT_4) {

            menu.setResult(2);
            menu.broadcastChanges();
            return;
        }

        /*
         * Clear previous result.
         *
         * 0 = no result
         * 1 = success
         * 2 = failed
         */
        menu.setResult(0);

        ItemStack scroll =
                menu.getScroll(scrollSlot);

        ItemStack target =
                menu.getApplicationItem();

        /*
         * Revalidate everything server-side.
         */
        if (scroll.isEmpty()
                || target.isEmpty()) {

            menu.setResult(2);
            menu.broadcastChanges();
            return;
        }

        /*
         * Verify that this is actually one of our
         * UpgradeScroll items.
         */
        if (!(scroll.getItem()
                instanceof UpgradeScroll_Item scrollItem)) {

            menu.setResult(2);
            menu.broadcastChanges();
            return;
        }

        /*
         * Verify that this scroll is allowed in
         * the specific slot the player clicked.
         */
        if (!menu.getSlot(scrollSlot)
                .mayPlace(scroll)) {

            menu.setResult(2);
            menu.broadcastChanges();
            return;
        }

        /*
         * Verify that the application item is still
         * valid.
         */
        if (!menu.getSlot(
                UpgradeScrollMenu.APPLICATION_SLOT
        ).mayPlace(target)) {

            menu.setResult(2);
            menu.broadcastChanges();
            return;
        }

        int scrollId =
                scrollItem.getScrollId();

        UpgradeScrolls.LOGGER.debug(
                "[ScrollMenu] Applying scroll | player={} | slot={} | scrollId={} | target={}",
                player.getGameProfile().getName(),
                scrollSlot,
                scrollId,
                target.getItem()
        );

        /*
         * Run the actual server-side scroll effect.
         *
         * The existing scroll procedure handles
         * consumption.
         */
        boolean success =
                executeScroll(
                        player,
                        scrollId,
                        target,
                        scroll
                );

        /*
         * Tell the client what happened.
         *
         * 1 = success
         * 2 = failed
         */
        menu.setResult(
                success ? 1 : 2
        );

        menu.broadcastChanges();
    }

    private static boolean executeScroll(
            ServerPlayer player,
            int scrollId,
            ItemStack target,
            ItemStack scroll
    ) {
        return UpgradeScroll_Item.runScrollEffectFromMenu(
                player,
                scrollId,
                target,
                scroll
        );
    }

    public int getScrollSlot() {
        return scrollSlot;
    }
}