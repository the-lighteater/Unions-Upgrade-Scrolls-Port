package dot.lighteater.upgrade_scrolls.event;

import dot.lighteater.upgrade_scrolls.Config;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import dot.lighteater.upgrade_scrolls.item.custom.UpgradeScroll_Item;
import dot.lighteater.upgrade_scrolls.affix.AffixData;
import dot.lighteater.upgrade_scrolls.affix.AffixLoader;
import dot.lighteater.upgrade_scrolls.network.AffixSyncPacket;
import dot.lighteater.upgrade_scrolls.utility.CursedConfigLoader;
import dot.lighteater.upgrade_scrolls.utility.ModUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

// Events handled by the mod.

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID)
public class ModEvents {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UpgradeScrolls.LOGGER.info(
                    "[AffixSync] Sending affixes to {}",
                    player.getGameProfile().getName()
            );

            AffixSyncPacket.sendTo(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;

        ItemStack offHand = player.getOffhandItem();
        ItemStack mainHand = player.getMainHandItem();

        // This code simply adds a cooldown to your mainhand item if you're holding a scroll and a valid item.
        if ((offHand.getItem() instanceof UpgradeScroll_Item) && (ModUtility.isValidShield(mainHand) ||
                ModUtility.isValidWeaponOrCurio(mainHand) != 0 || ModUtility.isValidBow(mainHand))) {
            // Set main hand cooldown for 2 ticks (refresh every tick)
            player.getCooldowns().addCooldown(mainHand.getItem(), 20);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty() || !weapon.hasTag()) return;

        // Check for the Magician tag
        double magicianPower = weapon.getOrCreateTag().getDouble("upgradescrolls:level_bonus_magician");
        if (magicianPower <= 0) return;

        magicianPower *= (40) * (CursedConfigLoader.getMeleeBonus());

        UpgradeScrolls.LOGGER.debug("Damage modifier: {} + with config being {}",magicianPower, CursedConfigLoader.getMeleeBonus());

        LivingEntity target = event.getEntity();

        // Cancel or reduce the original hit damage
        float originalDamage = event.getAmount();

        float magicBonus = (float) (originalDamage * (magicianPower + 1));

        event.setCanceled(false);

        DamageSource magicSource = new DamageSource(player.level().registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.MAGIC));

        target.hurt(magicSource, magicBonus);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();

        int minScrolls = Config.MYSTERY_SCROLL_DROP_AMOUNT.get() > 0 ? 1 : 0;

        if (name.equals(new ResourceLocation("minecraft", "blocks/spawner")) ||
                name.equals(new ResourceLocation("iceandfire", "blocks/dread_spawner")) ||
                name.equals(new ResourceLocation("goety", "blocks/void_spawner"))) {
            LootPool pool = LootPool.lootPool()
                    .name("extra_drops")
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.UPGRADE_SCROLL_0.get()).setWeight(Config.GOLDEN_MYSTERY_SCROLL_DROP_RATE.get())
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(minScrolls, Config.MYSTERY_SCROLL_DROP_AMOUNT.get()))))
                    .add(LootItem.lootTableItem(ModItems.UPGRADE_SCROLL_20.get()).setWeight(1)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(minScrolls, Config.MYSTERY_SCROLL_DROP_AMOUNT.get()))))
                    .build();

            LootTable table = event.getTable();
            table.addPool(pool);
        }
    }
}
