package dot.lighteater.upgrade_scrolls.compat;

import dot.lighteater.upgrade_scrolls.scrollsprocedures.AffixData;
import dot.lighteater.upgrade_scrolls.scrollsprocedures.AffixLoader;
import dot.lighteater.upgrade_scrolls.scrollsprocedures.AttributeEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;
import java.util.UUID;

public class CurioAffixProcedure {

    private static final String affixKey = "mod:tag_affix";

    @SubscribeEvent
    public static void onCurioAttributes(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.hasTag()) {
            if (stack.getOrCreateTag().getDouble("upgradescrolls:streak_curio") != 0.0)
                event.addModifier(Attributes.ARMOR, new AttributeModifier(getItemUUID(stack, "curio_guardian"),
                        "union_upgrade_scrolls.curio.guardian",
                        (stack.getOrCreateTag().getDouble("upgradescrolls:streak_curio") / 2),
                        AttributeModifier.Operation.ADDITION));
            applyCurioAttributes(stack, event);
        }
    }

    public static void applyCurioAttributes(ItemStack stack, CurioAttributeModifierEvent event) {
        AffixData affix = null;
        for (AffixData affixes : AffixLoader.getAll()) {
            if (stack.getOrCreateTag().getDouble(affixKey + "_" + affixes.getName() + "_curio") > 0 && !affixes.isDisabled()) {
                affix = affixes;
                break;
            }
        }

        if (affix == null) return;
        AffixData newData = AffixLoader.AFFIXES.get(new ResourceLocation("upgrade_scrolls", affix.getName()));
        if (newData == null) return;

        List<AttributeEffect> stats = newData.getEffectsFor("curio");
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
