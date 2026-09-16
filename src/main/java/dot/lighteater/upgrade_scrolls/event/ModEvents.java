package dot.lighteater.upgrade_scrolls.event;

import dot.lighteater.upgrade_scrolls.Config;
import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import dot.lighteater.upgrade_scrolls.item.custom.UpgradeScroll_Item;
import dot.lighteater.upgrade_scrolls.scrollsprocedures.AffixData;
import dot.lighteater.upgrade_scrolls.scrollsprocedures.AffixLoader;
import dot.lighteater.upgrade_scrolls.utility.CursedConfigLoader;
import dot.lighteater.upgrade_scrolls.utility.ModUtility;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;

import java.util.Comparator;
import java.util.List;

// Events handled by the mod.

@Mod.EventBusSubscriber(modid = UpgradeScrolls.MODID)
public class ModEvents {

    private static final Logger LOGGER = LogManager.getLogger();

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
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();

        // Iterate over all loaded affixes
        for (AffixData affix : AffixLoader.getAll()) {

            if (affix == null) {
                continue;
            }

            String affixName = affix.getName();

            if (affixName == null || affixName.isEmpty()) {
                continue;
            }

            // Check all keys in the item's NBT that start with this affix
            for (String nbtKey : tag.getAllKeys()) {

                if (!nbtKey.startsWith(
                        "mod:tag_affix_" + affixName
                )) {
                    continue;
                }

                // If the tag has a value > 0, the affix exists
                if (tag.getDouble(nbtKey) > 0) {

                    String displayName =
                            affixName.substring(0, 1).toUpperCase()
                                    + affixName.substring(1);

                    ChatFormatting color = affix.getColor();

                    if (color == null) {
                        color = ChatFormatting.WHITE;
                    }

                    String displayNameValue = affix.getDisplayName();

                    if (displayNameValue == null) {
                        displayNameValue = affixName;
                    }

                    event.getToolTip().add(
                            Component.literal("★ Affix: ")
                                    .withStyle(ChatFormatting.WHITE)
                                    .append(
                                            Component.literal(
                                                    displayNameValue
                                            ).setStyle(
                                                    Style.EMPTY
                                                            .withObfuscated(
                                                                    displayName.equals("Fabled")
                                                            )
                                                            .withColor(color)
                                            )
                                    )
                    );

                    break;
                }
            }
        }

        String[] cursedSlots = {
                "_head",
                "_chest",
                "_legs",
                "_feet",
                "",
                "_curio",
                "_shield",
                "_barbarian",
                "_magician"
        };

        for (String slotKey : cursedSlots) {

            String streakKey =
                    "upgradescrolls:streak" + slotKey;

            if (tag.contains(streakKey)) {

                int streak = tag.getInt(streakKey);

                if (streak > 0) {

                    String type = "Guardian";

                    if (slotKey.equals("_barbarian")) {
                        type = "Barbarian";
                    } else if (slotKey.equals("_magician")) {
                        type = "Magician";
                    }

                    event.getToolTip().add(
                            Component.literal(
                                    "★ " + type + " + " + streak
                            ).withStyle(
                                    ChatFormatting.YELLOW
                            )
                    );

                    // Star display
                    StringBuilder stars = new StringBuilder();

                    for (
                            int i = 1;
                            i <= CursedConfigLoader.getMaxLevel();
                            i++
                    ) {

                        if (i <= streak) {
                            stars.append("★");
                        } else {
                            stars.append("☆");
                        }

                        // Add a space every 5 stars,
                        // except after the last group
                        if (
                                i % 5 == 0
                                        && i != CursedConfigLoader.getMaxLevel()
                        ) {
                            stars.append(" ");
                        }
                    }

                    event.getToolTip().add(
                            Component.literal(
                                    stars.toString()
                            ).withStyle(
                                    ChatFormatting.WHITE
                            )
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onScrollTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof UpgradeScroll_Item scroll)) {
            return;
        }

        List<Component> tooltip = event.getToolTip();

        switch (scroll.getScrollId()) {
            case 3, 4, 5, 6, 7, 8, 22 -> AffixLoader.getAll().stream()
                    .filter(affix -> affix != null && !affix.isDisabled())
                    .sorted(
                            Comparator.comparingDouble(
                                    AffixData::getWeight
                            ).reversed()
                    )
                    .forEach(affix -> {

                        ChatFormatting color = affix.getColor();

                        if (color == null) {
                            color = ChatFormatting.WHITE;
                        }

                        String tooltipKey = affix.getTooltip();

                        if (tooltipKey == null || tooltipKey.isEmpty()) {
                            return;
                        }

                        tooltip.add(
                                Component.translatable(
                                        tooltipKey
                                ).withStyle(color)
                        );
                    });
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
