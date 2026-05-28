package dot.lighteater.upgrade_scrolls.datagen;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import dot.lighteater.upgrade_scrolls.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

// Item Model Generation
// Makes the item models with runData.
// Code credit goes to Kaupenjoe for the simpleItem method and in general this file.

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, UpgradeScrolls.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.UPGRADE_SCROLL_0);
        simpleItem(ModItems.UPGRADE_SCROLL_1);
        simpleItem(ModItems.UPGRADE_SCROLL_2);
        simpleItem(ModItems.UPGRADE_SCROLL_3);
        simpleItem(ModItems.UPGRADE_SCROLL_4);
        simpleItem(ModItems.UPGRADE_SCROLL_5);
        simpleItem(ModItems.UPGRADE_SCROLL_6);
        simpleItem(ModItems.UPGRADE_SCROLL_7);
        simpleItem(ModItems.UPGRADE_SCROLL_8);
        simpleItem(ModItems.UPGRADE_SCROLL_9);
        simpleItem(ModItems.UPGRADE_SCROLL_10);
        simpleItem(ModItems.UPGRADE_SCROLL_11);
        simpleItem(ModItems.UPGRADE_SCROLL_12);
        simpleItem(ModItems.UPGRADE_SCROLL_13);
        simpleItem(ModItems.UPGRADE_SCROLL_14);
        simpleItem(ModItems.UPGRADE_SCROLL_15);
        simpleItem(ModItems.UPGRADE_SCROLL_16);
        simpleItem(ModItems.UPGRADE_SCROLL_17);
        simpleItem(ModItems.UPGRADE_SCROLL_18);
        simpleItem(ModItems.UPGRADE_SCROLL_19);
        simpleItem(ModItems.UPGRADE_SCROLL_20);
        simpleItem(ModItems.UPGRADE_SCROLL_21);

        simpleItem(ModItems.UPGRADE_SCROLL_22);
        simpleItem(ModItems.UPGRADE_SCROLL_23);
        simpleItem(ModItems.UPGRADE_SCROLL_24);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(UpgradeScrolls.MODID, "item/" + item.getId().getPath()));
    }
}
