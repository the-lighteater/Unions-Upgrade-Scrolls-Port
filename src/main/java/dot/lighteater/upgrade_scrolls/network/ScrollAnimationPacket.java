package dot.lighteater.upgrade_scrolls.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScrollAnimationPacket {

    private final ItemStack stack;

    public ScrollAnimationPacket(ItemStack stack) {
        this.stack = stack;
    }

    public ScrollAnimationPacket(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                mc.gameRenderer.displayItemActivation(stack);
            }

        });

        ctx.get().setPacketHandled(true);
    }
}