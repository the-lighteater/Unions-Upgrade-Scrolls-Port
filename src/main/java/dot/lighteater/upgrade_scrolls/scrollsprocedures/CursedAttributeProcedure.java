package dot.lighteater.upgrade_scrolls.scrollsprocedures;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.utility.CursedConfigLoader;
import dot.lighteater.upgrade_scrolls.utility.ModUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CursedAttributeProcedure {

    @SubscribeEvent
    public static void addCursedAttributeModifier(
            ItemAttributeModifierEvent event
    ) {

        ItemStack stack = event.getItemStack();

        if (!stack.hasTag()) {
            return;
        }

        EquipmentSlot slot = event.getSlotType();

        for (CursedConfigLoader.ModifierData modifier
                : CursedConfigLoader.getModifiers()) {

            if (!isApplicable(
                    modifier,
                    stack,
                    slot
            )) {
                continue;
            }

            ResourceLocation attributeId =
                    ResourceLocation.tryParse(
                            modifier.getAttributeName()
                    );

            if (attributeId == null) {
                continue;
            }

            Attribute attribute =
                    ForgeRegistries.ATTRIBUTES.getValue(
                            attributeId
                    );

            if (attribute == null) {
                continue;
            }

            String streakKey =
                    "upgradescrolls:streak_"
                            + modifier.getStreakKey();

            double streak =
                    stack.getOrCreateTag()
                            .getDouble(streakKey);

            if (streak <= 0) {
                continue;
            }

            double amount =
                    streak * modifier.getLevelBonus();

            AttributeModifier.Operation operation =
                    parseOperation(
                            modifier.getOperation()
                    );

            if (operation == null) {
                continue;
            }

            event.addModifier(
                    attribute,
                    new AttributeModifier(
                            getItemUUID(
                                    stack,
                                    modifier.getStreakKey()
                                            + "_"
                                            + modifier.getAttributeName()
                                            + "_"
                                            + modifier.getOperation()
                            ),
                            "union_upgrade_scrolls.cursed."
                                    + modifier.getStreakKey()
                                    + "."
                                    + modifier.getAttributeName(),
                            amount,
                            operation
                    )
            );
        }
    }

    private static boolean isApplicable(
            CursedConfigLoader.ModifierData modifier,
            ItemStack stack,
            EquipmentSlot slot
    ) {

        String equipment =
                modifier.getEquipment();

        if (equipment == null) {
            return false;
        }

        return switch (equipment.toUpperCase()) {
            case "WEAPON" -> isWeaponApplication(stack, slot);
            case "SHIELD" -> isShieldApplication(stack, slot);
            case "HEAD" -> isHeadApplication(stack, slot);
            case "CHEST" -> isChestApplication(stack, slot);
            case "LEGS" -> isLegsApplication(stack, slot);
            case "FEET" -> isFeetApplication(stack, slot);
            default -> false;
        };
    }

    private static boolean isWeaponApplication(
            ItemStack stack,
            EquipmentSlot slot
    ) {

        return (slot == EquipmentSlot.MAINHAND
                || slot == EquipmentSlot.OFFHAND)
                && (
                ModUtility.isValidWeapon(stack)
                        || ModUtility.isValidBow(stack)
        );
    }

    private static boolean isShieldApplication(
            ItemStack stack,
            EquipmentSlot slot
    ) {
        return (slot == EquipmentSlot.MAINHAND
                || slot == EquipmentSlot.OFFHAND)
                && ModUtility.isValidShield(stack);
    }

    private static boolean isHeadApplication(
            ItemStack stack,
            EquipmentSlot slot
    ) {
        return slot == EquipmentSlot.HEAD
                && ModUtility.isValidHelmet(stack);
    }

    private static boolean isChestApplication(
            ItemStack stack,
            EquipmentSlot slot
    ) {
        return slot == EquipmentSlot.CHEST
                && ModUtility.isValidChestplate(stack);
    }

    private static boolean isLegsApplication(
            ItemStack stack,
            EquipmentSlot slot
    ) {
        return slot == EquipmentSlot.LEGS
                && ModUtility.isValidLeggings(stack);
    }

    private static boolean isFeetApplication(
            ItemStack stack,
            EquipmentSlot slot
    ) {
        return slot == EquipmentSlot.FEET
                && ModUtility.isValidBoots(stack);
    }

    private static AttributeModifier.Operation parseOperation(
            String operation
    ) {
        if (operation == null) {
            return null;
        }
        return switch (operation.toUpperCase()) {
            case "ADDITION" -> AttributeModifier.Operation.ADDITION;
            case "MULTIPLY_BASE" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "MULTIPLY_TOTAL" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> null;
        };
    }

    public static UUID getItemUUID(
            ItemStack stack,
            String attributeKey
    ) {

        String base =
                stack.getOrCreateTag()
                        .getString(
                                "upgrade_scrolls:item_uuid"
                        );

        if (base.isEmpty()) {
            base = UUID.randomUUID().toString();

            stack.getOrCreateTag().putString(
                    "upgrade_scrolls:item_uuid",
                    base
            );
        }

        return UUID.nameUUIDFromBytes(
                (base + attributeKey).getBytes(StandardCharsets.UTF_8)
        );
    }
}