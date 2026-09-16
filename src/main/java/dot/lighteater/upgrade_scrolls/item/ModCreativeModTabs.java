package dot.lighteater.upgrade_scrolls.item;

import dot.lighteater.upgrade_scrolls.UpgradeScrolls;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    UpgradeScrolls.MODID
            );

    public static final RegistryObject<CreativeModeTab> UPGRADE_SCROLLS =
            CREATIVE_MODE_TABS.register(
                    "upgrade_scrolls",
                    () -> CreativeModeTab.builder()
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> new ItemStack(
                                    ModItems.UPGRADE_SCROLL_3.get()
                            ))
                            .title(
                                    Component.translatable(
                                            "creativetab.upgrade_scrolls"
                                    )
                            )
                            .displayItems((parameters, output) -> {

                                output.accept(ModItems.UPGRADE_SCROLL_0.get());
                                output.accept(ModItems.UPGRADE_SCROLL_1.get());
                                output.accept(ModItems.UPGRADE_SCROLL_2.get());
                                output.accept(ModItems.UPGRADE_SCROLL_3.get());
                                output.accept(ModItems.UPGRADE_SCROLL_4.get());
                                output.accept(ModItems.UPGRADE_SCROLL_5.get());
                                output.accept(ModItems.UPGRADE_SCROLL_6.get());
                                output.accept(ModItems.UPGRADE_SCROLL_7.get());
                                output.accept(ModItems.UPGRADE_SCROLL_8.get());
                                output.accept(ModItems.UPGRADE_SCROLL_9.get());
                                output.accept(ModItems.UPGRADE_SCROLL_10.get());
                                output.accept(ModItems.UPGRADE_SCROLL_11.get());
                                output.accept(ModItems.UPGRADE_SCROLL_12.get());
                                output.accept(ModItems.UPGRADE_SCROLL_13.get());
                                output.accept(ModItems.UPGRADE_SCROLL_14.get());
                                output.accept(ModItems.UPGRADE_SCROLL_15.get());
                                output.accept(ModItems.UPGRADE_SCROLL_16.get());
                                output.accept(ModItems.UPGRADE_SCROLL_17.get());
                                output.accept(ModItems.UPGRADE_SCROLL_18.get());
                                output.accept(ModItems.UPGRADE_SCROLL_19.get());
                                output.accept(ModItems.UPGRADE_SCROLL_20.get());
                                output.accept(ModItems.UPGRADE_SCROLL_21.get());
                                output.accept(ModItems.UPGRADE_SCROLL_22.get());
                                output.accept(ModItems.UPGRADE_SCROLL_23.get());
                                output.accept(ModItems.UPGRADE_SCROLL_24.get());

                            })
                            .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}