package net.baguchan.bagus_littlemaidmob.registry;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, LittleMaidMod.MODID);

	public static final RegistryObject<Item> LITTLE_MAID_SPAWN_EGG = ITEM_REGISTRY.register("little_maid_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.LITTLE_MAID, 0xFFFFFF, 0, (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));
}