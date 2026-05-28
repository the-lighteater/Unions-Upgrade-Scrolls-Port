package dot.lighteater.upgrade_scrolls.scrollsprocedures;

import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AffixData {

    private String name;
    private double weight;
    private String color;
    private String displayName;
    private boolean disabled;
    private String tooltip;
    private boolean finalAffix;
    private Map<String, List<AttributeEffect>> effects;
    private List<String> sounds;

    public String getName() { return name; }
    public double getWeight() { return weight; }

    public ChatFormatting getColor() {
        ChatFormatting format = ChatFormatting.getByName(color.toUpperCase());
        return format != null ? format : ChatFormatting.WHITE;
    }

    public List<AttributeEffect> getEffectsFor(String type) {
        return effects.getOrDefault(type, List.of());
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isFinal() {
        return finalAffix;
    }

    public List<String> getSounds() { return sounds;}
}