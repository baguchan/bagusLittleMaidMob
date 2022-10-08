package net.baguchan.littlemaidmob.registry;

import net.baguchan.littlemaidmob.LittleMaidMod;
import net.baguchan.littlemaidmob.menutype.LittleMaidInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = LittleMaidMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LittleMaidMod.MODID);

    public static final RegistryObject<MenuType<LittleMaidInventoryMenu>> LITTLE_MAID_CONTAINER =
            MENU_REGISTRY.register("little_maid_container", () -> IForgeMenuType.create(LittleMaidInventoryMenu::new));

    private static String prefix(String path) {
        return LittleMaidMod.MODID + ":" + path;
    }
}
