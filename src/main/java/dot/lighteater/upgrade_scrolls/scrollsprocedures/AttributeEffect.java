package dot.lighteater.upgrade_scrolls.scrollsprocedures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeEffect {

    private static final Logger LOGGER = LogManager.getLogger();

    private String attribute;
    private String operation;
    private double value;
    private String endString;

    public Attribute getAttribute() {
        ResourceLocation id = new ResourceLocation(attribute);

        return ForgeRegistries.ATTRIBUTES.getValue(id);
    }

    public AttributeModifier.Operation getOperation() {
        return switch (operation) {
            case "Adding" -> AttributeModifier.Operation.ADDITION;
            default -> AttributeModifier.Operation.MULTIPLY_BASE;
        };
    }

    public double getValue() {
        return value;
    }

    public String getEndString() {
        return endString;
    }
}
