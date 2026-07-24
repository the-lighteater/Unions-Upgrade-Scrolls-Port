package dot.lighteater.upgrade_scrolls.scrollsprocedures;

import dot.lighteater.upgrade_scrolls.Config;
import dot.lighteater.upgrade_scrolls.utility.ItemUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CursedAttributeProcedure {
    @SubscribeEvent
    public static void addCursedAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.hasTag()) return;

        EquipmentSlot slot = event.getSlotType();

        double level = Config.CURSED_SCROLLS_STRENGTH.get();

        ResourceLocation id = new ResourceLocation(Config.CURSED_SCROLLS_ATTRIBUTE.get());
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(id);

        boolean goodID = attr != null;

        if ((ItemUtility.isValidWeapon(stack) || ItemUtility.isValidBow(stack))
                && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) && stack.getOrCreateTag().getDouble("upgradescrolls:streak_barbarian") > 0) {
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    getItemUUID(stack, "cursed_damage_bonus_weapon"),
                    "union_upgrade_scrolls.cursed.damage",
                    (stack.getOrCreateTag().getDouble("upgradescrolls:streak_barbarian") / 40),
                    AttributeModifier.Operation.MULTIPLY_BASE));
        }

        else if (ItemUtility.isValidShield(stack) && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
                && stack.getOrCreateTag().getDouble("upgradescrolls:streak_shield") > 0
                && goodID) {
            event.addModifier(attr, new AttributeModifier(
                    getItemUUID(stack, "cursed_armor_bonus_shield"),
                    "union_upgrade_scrolls.cursed.armor",
                    (stack.getOrCreateTag().getDouble("upgradescrolls:streak_shield") * level),
                    AttributeModifier.Operation.ADDITION));
        }

        else if (stack.getItem() instanceof ArmorItem && slot == EquipmentSlot.HEAD
                && (stack.getOrCreateTag().getDouble("upgradescrolls:streak_head") > 0)
                && goodID) {
            event.addModifier(attr, new AttributeModifier(
                    getItemUUID(stack, "cursed_armor_bonus_head"),
                    "union_upgrade_scrolls.cursed.armor",
                    (stack.getOrCreateTag().getDouble("upgradescrolls:streak_head") * level),
                    AttributeModifier.Operation.ADDITION));
        }

        else if (stack.getItem() instanceof ArmorItem && slot == EquipmentSlot.CHEST
                && (stack.getOrCreateTag().getDouble("upgradescrolls:streak_chest") > 0)
                && goodID) {
            event.addModifier(attr, new AttributeModifier(
                    getItemUUID(stack, "cursed_armor_bonus_chest"),
                    "union_upgrade_scrolls.cursed.armor",
                    (stack.getOrCreateTag().getDouble("upgradescrolls:streak_chest") * level),
                    AttributeModifier.Operation.ADDITION));
        }

        else if (stack.getItem() instanceof ArmorItem && slot == EquipmentSlot.LEGS
                && (stack.getOrCreateTag().getDouble("upgradescrolls:streak_legs") > 0)
                && goodID) {
            event.addModifier(attr, new AttributeModifier(
                    getItemUUID(stack, "cursed_armor_bonus_legs"),
                    "union_upgrade_scrolls.cursed.armor",
                    (stack.getOrCreateTag().getDouble("upgradescrolls:streak_legs") * level),
                    AttributeModifier.Operation.ADDITION));
        }

        else if (stack.getItem() instanceof ArmorItem && slot == EquipmentSlot.FEET
                && (stack.getOrCreateTag().getDouble("upgradescrolls:streak_feet") > 0)
                && goodID) {
            event.addModifier(attr, new AttributeModifier(
                    getItemUUID(stack, "cursed_armor_bonus_feet"),
                    "union_upgrade_scrolls.cursed.armor",
                    (stack.getOrCreateTag().getDouble("upgradescrolls:streak_feet") * level),
                    AttributeModifier.Operation.ADDITION));
        }
    }

    public static UUID getItemUUID(ItemStack stack, String attributeKey) {
        String base = stack.getOrCreateTag().getString("upgrade_scrolls:item_uuid");

        // If item has no UUID assigned, assign one once
        if (base.isEmpty()) {
            base = UUID.randomUUID().toString();
            stack.getOrCreateTag().putString("upgrade_scrolls:item_uuid", base);
        }

        // Combine the item UUID + attribute key into a stable new UUID
        return UUID.nameUUIDFromBytes((base + attributeKey).getBytes());
    }
}