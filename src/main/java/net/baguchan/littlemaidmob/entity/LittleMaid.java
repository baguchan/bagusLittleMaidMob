package net.baguchan.littlemaidmob.entity;

import net.baguchan.littlemaidmob.resource.holder.TextureHolder;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.baguchan.littlemaidmob.resource.manager.LMModelManager;
import net.baguchan.littlemaidmob.resource.manager.LMTextureManager;
import net.baguchan.littlemaidmob.resource.util.TextureColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

//NM読み込むのに要る
public class LittleMaid extends LittleMaidBaseEntity {

    public LittleMaid(EntityType<? extends LittleMaid> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    public boolean isMale() {
        return false;
    }
}
