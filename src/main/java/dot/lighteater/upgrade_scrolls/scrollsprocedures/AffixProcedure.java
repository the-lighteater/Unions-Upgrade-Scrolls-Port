package dot.lighteater.upgrade_scrolls.scrollsprocedures;

import dot.lighteater.upgrade_scrolls.Config;
import dot.lighteater.upgrade_scrolls.utility.ModUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.*;

@Mod.EventBusSubscriber
public class AffixProcedure {

    private static final String affixKey = "mod:tag_affix";

    @SubscribeEvent
    public static void addAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();

        if (!stack.hasTag()) return;

        if (ModUtility.isValidWeapon(stack)
                && (((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
                    && Config.ALLOW_BOTH_HANDS.get()) || (slot == EquipmentSlot.MAINHAND && event.getSlotType() == EquipmentSlot.MAINHAND)))
            applyAttributes(stack, "", "weapon", event);

        if (ModUtility.isValidBow(stack)
                && (((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
                    && Config.ALLOW_BOTH_HANDS.get()) || (slot == EquipmentSlot.MAINHAND && event.getSlotType() == EquipmentSlot.MAINHAND)))
            applyAttributes(stack, "", "bow", event);

        if (ModUtility.isValidShield(stack)
                && (((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
                    && Config.ALLOW_BOTH_HANDS.get()) || (slot == EquipmentSlot.OFFHAND && event.getSlotType() == EquipmentSlot.OFFHAND)))
            applyAttributes(stack, "_shield", "shield", event);

        if (stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == slot)
            applyAttributes(stack, "_" + slot.getName(), "armor", event);
    }

    public static void applyAttributes(ItemStack stack, String slot, String type, ItemAttributeModifierEvent event) {
        AffixData affix = null;
        for (AffixData affixes : AffixLoader.getAll()) {
            if (stack.getOrCreateTag().getDouble(affixKey + "_" + affixes.getName() + slot) > 0 && !affixes.isDisabled()) {
                affix = affixes;
                break;
            }
        }

        if (affix == null) return;
        AffixData newData = AffixLoader.AFFIXES.get(new ResourceLocation("upgrade_scrolls", affix.getName()));
        if (newData == null) return;

        List<AttributeEffect> stats = newData.getEffectsFor(type);
        if (stats == null) return;

        for (AttributeEffect attr : stats) {
            Attribute attribute = attr.getAttribute();
            if (attribute == null) continue;

            event.addModifier(attribute, new AttributeModifier(
                    getItemUUID(stack, affix.getName() + "_" + attr.getEndString() + "_bonus"),
                    "union_upgrade_scrolls." + affix.getName() + "." + attr.getEndString(),
                    attr.getValue(),
                    attr.getOperation()
            ));
        }
    }

    public static UUID getItemUUID(ItemStack stack, String attributeKey) {
        String base = stack.getOrCreateTag().getString("upgrade_scrolls:item_uuid");

        if (base.isEmpty()) {
            base = UUID.randomUUID().toString();
            stack.getOrCreateTag().putString("upgrade_scrolls:item_uuid", base);
        }

        return UUID.nameUUIDFromBytes((base + attributeKey).getBytes());
    }
}