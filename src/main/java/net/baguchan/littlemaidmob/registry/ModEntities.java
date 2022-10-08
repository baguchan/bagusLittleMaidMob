package net.baguchan.littlemaidmob.registry;

import net.baguchan.littlemaidmob.LittleMaidMod;
import net.baguchan.littlemaidmob.client.screen.ModelSelectScreen;
import net.baguchan.littlemaidmob.entity.LittleMaid;
import net.baguchan.littlemaidmob.entity.MultiModelEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = LittleMaidMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LittleMaidMod.MODID);

    public static final RegistryObject<EntityType<MultiModelEntity>> MULTI_MODEL = ENTITIES_REGISTRY.register("multi_model", () -> EntityType.Builder.<MultiModelEntity>of(MultiModelEntity::new, MobCategory.CREATURE).sized(0.6F, 1.65F).build(prefix("multi_model")));
	public static final RegistryObject<EntityType<LittleMaid>> LITTLE_MAID = ENTITIES_REGISTRY.register("little_maid", () -> EntityType.Builder.of(LittleMaid::new, MobCategory.CREATURE).sized(0.6F, 1.65F).build(prefix("little_maid")));
	public static final RegistryObject<EntityType<ModelSelectScreen.DummyModelEntity>> DUMMY = ENTITIES_REGISTRY.register("dummy", () -> EntityType.Builder.<ModelSelectScreen.DummyModelEntity>of(ModelSelectScreen.DummyModelEntity::new, MobCategory.MISC).sized(0.6F, 1.65F).noSummon().build(prefix("dummy")));

    private static String prefix(String path) {
        return LittleMaidMod.MODID + ":" + path;
    }

    @SubscribeEvent
    public static void registerEntity(EntityAttributeCreationEvent event) {
        event.put(MULTI_MODEL.get(), MultiModelEntity.createAttributes().build());
        event.put(LITTLE_MAID.get(), MultiModelEntity.createAttributes().build());
    }
}
