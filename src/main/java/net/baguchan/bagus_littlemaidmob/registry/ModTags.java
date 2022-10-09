package net.baguchan.bagus_littlemaidmob.registry;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
	public static class Items {
		public static final TagKey<Item> LITTLE_MAID_HEALABLE = tag("little_maid_healable");
		public static final TagKey<Item> CAKE = tag("little_maid_cake");

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(new ResourceLocation(LittleMaidMod.MODID, name));
		}

	}
}
