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

        if (attribute == null || attribute.isEmpty()) {
            LOGGER.warn("Affix AttributeEffect has no attribute configured.");
            return null;
        }

        ResourceLocation id = ResourceLocation.tryParse(attribute);

        if (id == null) {
            LOGGER.warn(
                    "Invalid attribute ResourceLocation: {}",
                    attribute
            );
            return null;
        }

        Attribute result =
                ForgeRegistries.ATTRIBUTES.getValue(id);

        if (result == null) {
            LOGGER.warn(
                    "Unknown attribute: {}",
                    attribute
            );
        }

        return result;
    }

    public AttributeModifier.Operation getOperation() {

        if (operation == null) {
            return AttributeModifier.Operation.MULTIPLY_BASE;
        }

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
