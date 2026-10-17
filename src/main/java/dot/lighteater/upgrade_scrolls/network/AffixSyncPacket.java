
package dot.lighteater.upgrade_scrolls.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.affix.AffixData;
import dot.lighteater.upgrade_scrolls.affix.AffixLoader;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class AffixSyncPacket {

    private final String json;

    public AffixSyncPacket(String json) {
        this.json = json;
    }

    public static void encode(
            AffixSyncPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeUtf(packet.json, 1048576);
    }

    public static AffixSyncPacket decode(FriendlyByteBuf buffer) {
        return new AffixSyncPacket(buffer.readUtf(1048576));
    }

    public static void handle(
            AffixSyncPacket packet,
            Supplier<NetworkEvent.Context> supplier
    ) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            try {
                JsonObject root = JsonParser.parseString(packet.json)
                        .getAsJsonObject();

                AffixLoader.AFFIXES.clear();

                root.entrySet().forEach(entry -> {
                    ResourceLocation id =
                            ResourceLocation.tryParse(entry.getKey());

                    if (id == null) {
                        UpgradeScrolls.LOGGER.warn(
                                "[AffixSync] Invalid affix ID: {}",
                                entry.getKey()
                        );
                        return;
                    }

                    AffixData affix = AffixLoader.GSON.fromJson(
                            entry.getValue(),
                            AffixData.class
                    );

                    if (affix != null) {
                        AffixLoader.AFFIXES.put(id, affix);
                    }
                });

                UpgradeScrolls.LOGGER.info(
                        "[AffixSync] Client received {} affixes",
                        AffixLoader.AFFIXES.size()
                );

            } catch (Exception e) {
                UpgradeScrolls.LOGGER.error(
                        "[AffixSync] Failed to deserialize affixes",
                        e
                );
            }
        });

        context.setPacketHandled(true);
    }

    public static void sendTo(ServerPlayer player) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new AffixSyncPacket(createJson())
        );
    }

    public static void syncToClients() {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() == null) {
            UpgradeScrolls.LOGGER.warn(
                    "[AffixSync] Cannot sync affixes: server is not available yet"
            );
            return;
        }

        String json = createJson();

        ModNetwork.CHANNEL.send(
                PacketDistributor.ALL.noArg(),
                new AffixSyncPacket(json)
        );
    }

    private static String createJson() {
        JsonObject root = new JsonObject();

        AffixLoader.AFFIXES.forEach((id, affix) ->
                root.add(id.toString(), AffixLoader.GSON.toJsonTree(affix))
        );

        return root.toString();
    }
}
